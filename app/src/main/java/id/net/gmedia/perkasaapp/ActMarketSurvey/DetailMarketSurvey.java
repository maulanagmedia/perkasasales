package id.net.gmedia.perkasaapp.ActMarketSurvey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.perkasaapp.ActMarketSurvey.Adapter.AdapterProviderNonTelkomsel;
import id.net.gmedia.perkasaapp.ActMarketSurvey.Adapter.AdapterProviderTNonTelkomsel;
import id.net.gmedia.perkasaapp.ActMarketSurvey.Adapter.AdapterProviderTelkomsel;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DetailMarketSurvey extends AppCompatActivity implements LocationListener {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private String jarak = "",range = "", latitudeOutlet = "", longitudeOutlet = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private boolean isUpdateLocation = false;
    private TextView tvJarak;
    private ImageView ivRefreshJarak;
    private TextView tvNama;
    private RecyclerView rvTelkomsel, rvNonTelkomsel, rvTNonTelkomsel;
    private Button btnProses, btnPeta;
    private boolean isLoading = false;
    private String kdcus = "";
    private final String TAG = "DETAIL";
    private List<CustomItem> listTelkomsel = new ArrayList<>(), listNonTelkomsel = new ArrayList<>(), listTNonTelkomsel = new ArrayList<>();
    private AdapterProviderTelkomsel adapterTelkomsel;
    private AdapterProviderNonTelkomsel adapterNonTelkomsel;
    private AdapterProviderTNonTelkomsel adapterTNonTelkomsel;
    private EditText edtKeterangan;
    private String state = "";
    public static final String flag = "MARKETSURVEY";
    private String idSurvey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_market_survey);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Market Survey");
        }

        context = this;
        dialogBox = new DialogBox(context);

        initLocationUtils();
        initUI();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(DetailMarketSurvey.this);
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        rvTelkomsel = (RecyclerView) findViewById(R.id.rv_telkomsel);
        rvNonTelkomsel = (RecyclerView) findViewById(R.id.rv_non_telkomsel);
        rvTNonTelkomsel = (RecyclerView) findViewById(R.id.rv_t_non_telkomsel);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);
        btnPeta = (Button) findViewById(R.id.btn_peta);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);

        isLoading = false;
        listTelkomsel = new ArrayList<>();
        listNonTelkomsel = new ArrayList<>();
        listTNonTelkomsel = new ArrayList<>();
        adapterTelkomsel = new AdapterProviderTelkomsel(listTelkomsel, context);
        adapterNonTelkomsel = new AdapterProviderNonTelkomsel(listNonTelkomsel, context);
        adapterTNonTelkomsel = new AdapterProviderTNonTelkomsel(listTNonTelkomsel, context);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvTelkomsel.setLayoutManager(layoutManager);
        rvTelkomsel.setItemAnimator(new DefaultItemAnimator());
        rvTelkomsel.setAdapter(adapterTelkomsel);

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        rvNonTelkomsel.setLayoutManager(layoutManager1);
        rvNonTelkomsel.setItemAnimator(new DefaultItemAnimator());
        rvNonTelkomsel.setAdapter(adapterNonTelkomsel);

        // Transaksi Perdana dan Evoucher
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        rvTNonTelkomsel.setLayoutManager(layoutManager2);
        rvTNonTelkomsel.setItemAnimator(new DefaultItemAnimator());
        rvTNonTelkomsel.setAdapter(adapterTNonTelkomsel);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            String nama = bundle.getString("nama", "");

            idSurvey = bundle.getString("id", "");

            tvNama.setText(nama);

            if(!idSurvey.isEmpty()){

                //btnProses.setEnabled(false);
            }else{
                //btnProses.setEnabled(true);
            }
        }
    }

    private void getDetailSurvey() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", idSurvey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getMarketSurvey, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");

                        for(CustomItem tsel:listTelkomsel){

                            tsel.setItem3(jo.getString("unit_"+tsel.getItem1()));
                        }

                        for(CustomItem nonTsel:listNonTelkomsel){

                            nonTsel.setItem3(jo.getString("unit_"+nonTsel.getItem1()+"_small"));
                            nonTsel.setItem4(jo.getString("unit_"+nonTsel.getItem1()+"_medium"));
                            nonTsel.setItem5(jo.getString("unit_"+nonTsel.getItem1()+"_high"));
                        }

                        for(CustomItem tNonTsel:listTNonTelkomsel){

                            tNonTsel.setItem3(jo.getString("kartu_"+tNonTsel.getItem1()+"_perdana"));
                            tNonTsel.setItem4(jo.getString("kartu_"+tNonTsel.getItem1()+"_evoucher"));
                        }

                        latitude = iv.parseNullDouble(jo.getString("latitude"));
                        longitude = iv.parseNullDouble(jo.getString("longitude"));
                        state = jo.getString("state");

                        getJarak();
                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDetailSurvey();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                adapterTelkomsel = new AdapterProviderTelkomsel(listTelkomsel, context);
                adapterNonTelkomsel = new AdapterProviderNonTelkomsel(listNonTelkomsel, context);
                adapterTNonTelkomsel = new AdapterProviderTNonTelkomsel(listTNonTelkomsel, context);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                rvTelkomsel.setLayoutManager(layoutManager);
                rvTelkomsel.setItemAnimator(new DefaultItemAnimator());
                rvTelkomsel.setAdapter(adapterTelkomsel);

                RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context);
                rvNonTelkomsel.setLayoutManager(layoutManager1);
                rvNonTelkomsel.setItemAnimator(new DefaultItemAnimator());
                rvNonTelkomsel.setAdapter(adapterNonTelkomsel);

                RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context);
                rvTNonTelkomsel.setLayoutManager(layoutManager2);
                rvTNonTelkomsel.setItemAnimator(new DefaultItemAnimator());
                rvTNonTelkomsel.setAdapter(adapterTNonTelkomsel);
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDetailSurvey();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
            }
        });
    }


    private void initLocationUtils() {

        // getLocation update by google
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mRequestingLocationUpdates = false;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setCriteria();
        latitude = 0;
        longitude = 0;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        //location = getLocation();
        updateAllLocation();
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi

                if(!idSurvey.isEmpty()){

                    Toast.makeText(context, "Maaf data yang sudah tersimpan tidak dapat diubah", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isTselEmpty = true;
                for(CustomItem tsel : listTelkomsel){

                    if(!tsel.getItem3().equals("")){

                        isTselEmpty = false;
                    }else{
                        tsel.setItem3("0");
                    }
                }

                if(isTselEmpty){

                    Toast.makeText(context, "Harap isi data Provider Telkomsel", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isNonTselEmpty = true;
                for(CustomItem nonTsel : listNonTelkomsel){

                    if(!nonTsel.getItem3().equals("")){

                        isNonTselEmpty = false;
                    }else{
                        nonTsel.setItem3("0");
                    }

                    if(!nonTsel.getItem4().equals("")){

                        isNonTselEmpty = false;
                    }else{
                        nonTsel.setItem4("0");
                    }

                    if(!nonTsel.getItem5().equals("")){

                        isNonTselEmpty = false;
                    }else{
                        nonTsel.setItem5("0");
                    }
                }

                if(isNonTselEmpty){

                    Toast.makeText(context, "Harap isi data Provider Non Telkomsel", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses data survey?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        ivRefreshJarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isLoading) updateAllLocation();
            }
        });

        btnPeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLokasiReseller();
            }
        });
    }

    private void initData() {

        isLoading = true;
        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getProvider, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listTelkomsel.clear();
                    listNonTelkomsel.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");

                        for(int i = 0; i < ja.length();i++){

                            JSONObject jo = ja.getJSONObject(i);
                            String idGroup = jo.getString("id_group");

                            if(idGroup.equals("1")){ // Telkomsel

                                listTelkomsel.add(new CustomItem(
                                        jo.getString("field_name")
                                        ,jo.getString("provider")
                                        ,""
                                ));
                            }else if(idGroup.equals("2")){ // non telkonsel

                                listNonTelkomsel.add(new CustomItem(
                                        jo.getString("field_name")
                                        ,jo.getString("provider")
                                        ,""
                                        ,""
                                        ,""
                                ));
                            }else if(idGroup.equals("3")){ // Perdana & Evoucher

                                listTNonTelkomsel.add(new CustomItem(
                                        jo.getString("field_name")
                                        ,jo.getString("provider")
                                        ,""
                                        ,""
                                ));
                            }
                        }
                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                adapterTelkomsel.notifyDataSetChanged();
                adapterNonTelkomsel.notifyDataSetChanged();
                adapterTNonTelkomsel.notifyDataSetChanged();

                if(!idSurvey.isEmpty()) getDetailSurvey();
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
            }
        });
    }

    private void saveData() {

        btnProses.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {

            jBody.put("kdcus", kdcus);

            for(CustomItem tsel : listTelkomsel){

                jBody.put("unit_"+tsel.getItem1(), tsel.getItem3());
            }

            for(CustomItem nonTsel : listNonTelkomsel){

                jBody.put("unit_"+nonTsel.getItem1()+"_small", nonTsel.getItem3());
                jBody.put("unit_"+nonTsel.getItem1()+"_medium", nonTsel.getItem4());
                jBody.put("unit_"+nonTsel.getItem1()+"_high", nonTsel.getItem5());
            }

            for(CustomItem tNonTsel : listTNonTelkomsel){

                jBody.put("kartu_"+tNonTsel.getItem1()+"_perdana", tNonTsel.getItem3());
                jBody.put("kartu_"+tNonTsel.getItem1()+"_evoucher", tNonTsel.getItem4());
            }

            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            jBody.put("state", state);
            jBody.put("keterangan", edtKeterangan.getText().toString());
            String imei = "";
            ArrayList<String> imeis = iv.getIMEI(context);
            if(imeis != null) if(imeis.size() > 0) imei = imeis.get(0);
            jBody.put("imei", imei);
            jBody.put("jarak", jarak);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveMarketSurvey, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                btnProses.setEnabled(true);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        Intent intent = new Intent(context, ActivityHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("flag", flag);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(context, "Terjadi kesalahan koneksi, harap ulangi kembali nanti", Toast.LENGTH_SHORT).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                btnProses.setEnabled(true);
            }
        });
    }

    //region location

    private void getLokasiReseller() {

        isLoading = true;
        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", "");
            jBody.put("keyword", "");
            jBody.put("start", "");
            jBody.put("count", "");
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getLokasiReseller, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        JSONObject jo = ja.getJSONObject(0);
                        String namaRS = jo.getString("nama");
                        latitudeOutlet = jo.getString("latitude");
                        longitudeOutlet = jo.getString("longitude");
                        String photo = jo.getString("image");

                        if(latitude != 0 || longitude != 0){

                            Intent intent = new Intent(context, MapsResellerActivity.class);
                            intent.putExtra("lat", iv.doubleToStringFull(latitude));
                            intent.putExtra("long", iv.doubleToStringFull(longitude));
                            intent.putExtra("lat_outlet", latitudeOutlet);
                            intent.putExtra("long_outlet", longitudeOutlet);
                            intent.putExtra("nama", namaRS);
                            intent.putExtra("photo", photo);

                            startActivity(intent);
                        }
                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getLokasiReseller();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                    //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getLokasiReseller();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.

        isUpdateLocation = true;
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        isUpdateLocation = false;
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(DetailMarketSurvey.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailMarketSurvey.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(DetailMarketSurvey.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location clocation) {

                                        mRequestingLocationUpdates = true;
                                        if (clocation != null) {

                                            onLocationChanged(clocation);
                                        }else{
                                            location = getLocation();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DetailMarketSurvey.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(DetailMarketSurvey.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                                //refreshMode = false;
                        }

                        //get Location
                        isUpdateLocation = false;
                        location = getLocation();
                    }
                });
    }

    private void updateAllLocation(){
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_SETTINGS){

            if(resultCode == Activity.RESULT_CANCELED){

                mRequestingLocationUpdates = false;
            }else if(resultCode == Activity.RESULT_OK){

                startLocationUpdates();
            }

        }
    }

    public Location getLocation() {

        isUpdateLocation = true;
        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(DetailMarketSurvey.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(DetailMarketSurvey.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailMarketSurvey.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailMarketSurvey.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailMarketSurvey.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        isUpdateLocation = false;
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {

                        if(jarak != ""){ // Jarak sudah terdeteksi

                            Location locationBuffer = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            location = locationBuffer;

                        }else{
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                        if (location != null) {
                            //Changed(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {

                        if(jarak != ""){ // Jarak sudah terdeteksi

                            Location locationBuffer = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            location = locationBuffer;

                        }else{
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

                        if (location != null) {
                            //onLocationChanged(location);
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        isUpdateLocation = false;
        if(location != null){
            onLocationChanged(location);
        }

        return location;
    }

    public void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(DetailMarketSurvey.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation && idSurvey.isEmpty()){
            if(!isLoading) getJarak();
        }
    }

    private void getJarak() {

        isLoading = true;
        //dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", "");
            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getJarakReseller, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                //dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");
                        String jarak = jo.getString("jarak");
                        tvJarak.setText(jarak);

                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    /*View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getJarak();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");*/
                    Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
                }

                //get address
                new Thread(new Runnable(){
                    public void run(){
                        state = getAddress(location);
                    }
                }).start();
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                /*dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getJarak();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");*/
                Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getAddress(Location location)
    {
        List<Address> addresses;
        try{
            addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return findAddress(addresses);
        }
        catch (Exception e) {

            return "";
        }
    }

    private String findAddress(List<Address> addresses)
    {
        String address="";
        if(addresses!=null)
        {
            for(int i=0 ; i < addresses.size() ; i++){

                Address addre = addresses.get(i);
                String street = addre.getAddressLine(0);
                if(null == street)
                    street="";

                String city = addre.getLocality();
                if(city == null) city = "";

                String state=addre.getAdminArea();
                if(state == null) state="";

                String country = addre.getCountryName();
                if(country == null) country = "";

                address = street+", "+city+", "+state+", "+country;
            }
            return address;
        }
        return address;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        //location = getLocation();
    }

    @Override
    public void onProviderEnabled(String s) {

        //location = getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
