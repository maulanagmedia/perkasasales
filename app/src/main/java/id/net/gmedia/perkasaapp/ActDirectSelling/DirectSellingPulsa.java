package id.net.gmedia.perkasaapp.ActDirectSelling;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActDirectSelling.Adapter.ListBalasanInjectAdapter;
import id.net.gmedia.perkasaapp.ActDirectSelling.Adapter.ListBarangDSAdapter;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Services.USSDService;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.PinManager;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DirectSellingPulsa extends AppCompatActivity implements LocationListener {

    private static Context context;
    private static SessionManager session;
    private static ItemValidation iv = new ItemValidation();
    private static EditText edtNomor;
    public static final String flag = "DS";
    private static EditText edtHarga;
    private static String nik = "";
    private ListView lvBalasan;
    private static List<CustomItem> listBalasan;
    private static ListBalasanInjectAdapter balasanAdapter;
    private Button btnProses;
    private static final String TAG = "DetailInject";
    public static boolean isActive = false;
    private static ListView lvBarang;
    private static ListBarangDSAdapter adapterBarang;
    private static List<CustomItem> listBarang;
    private static boolean isProses = false;
    private static ProgressBar pbProses;
    private static LinearLayout llNominal;
    private static EditText edtNominal;
    private static String flagOrder = "", selectedHarga = "";
    private static CustomItem selectedItemOrder;
    private static String currentString = "";

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private static Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private static String jarak = "";
    private Button btnMapsOutlet;

    private boolean isUpdateLocation = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private static boolean editMode = false;
    private static String nomorEvent = "", nama = "", alamat = "", kdcus = "", poiName = "";
    private boolean isEvent = false, isPOI = false;
    private Button btnAppInfo;
    private LinearLayout llJarak;
    private static String lastKodebrg = "";
    private static String lastFlagOrder = "";
    private static String lastSN = "";
    private static String lastSuccessBalasan = "";
    private static String lastSuccessSender = "";
    private static boolean isKonfirmasiManual = false;
    private static boolean isSaveButtonClicked = false;
    private static boolean isOnSaving = false;
    private static boolean isLoading = false;
    private static String transactionID = "";
    private TextView tvJarak;
    private ImageView ivRefreshJarak;
    private static DialogBox dialogBox;
    private String latitudeOutlet = "", longitudeOutlet = "";
    private static EditText edtPin;
    private static CheckBox cbPin;
    private static PinManager pinManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_selling_pulsa);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Inject Pulsa");
        context = this;
        isLoading = false;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);
        pinManager = new PinManager(context);

        nik = session.getNikGa();
        transactionID = iv.getCurrentDate(FormatItem.formatDateTimePure);

        initLocationUtils();
        initUI();
        initEvent();
        getBarang();
    }

    private void initUI() {

        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        edtHarga = (EditText) findViewById(R.id.edt_harga);
        btnProses = (Button) findViewById(R.id.btn_proses);
        llNominal = (LinearLayout) findViewById(R.id.ll_nominal);
        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        lvBalasan = (ListView) findViewById(R.id.lv_balasan);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        pbProses = (ProgressBar) findViewById(R.id.pb_proses);
        llJarak = (LinearLayout) findViewById(R.id.ll_jarak);
        btnMapsOutlet = (Button) findViewById(R.id.btn_peta);
        btnAppInfo = (Button) findViewById(R.id.btn_app_info);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        edtPin = (EditText) findViewById(R.id.edt_pin);
        cbPin = (CheckBox) findViewById(R.id.cb_pin);

        listBalasan = new ArrayList<>();
        nomorEvent = "";
        nama = "";
        alamat = "";
        kdcus = "";
        jarak = "";
        lastSN = "";
        lastSuccessBalasan = "";
        lastSuccessSender = "";
        isKonfirmasiManual = false;
        isSaveButtonClicked = false;
        isOnSaving = false;
        cbPin.setChecked(pinManager.isPinSaved());
        edtPin.setText(pinManager.getPin());

        isEvent = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nomorEvent = bundle.getString("nomorevent","");
            kdcus = bundle.getString("kdcus", "");
            nama = bundle.getString("nama", "");
            alamat = bundle.getString("alamat", "");
            poiName = bundle.getString("namapoi", "");

            if(nomorEvent.length() > 0){

                //edtNomor.setText(nomor);
                isEvent = true;
            }
        }

        if(!isEvent){

            if(kdcus.length() == 0){
                llJarak.setVisibility(View.GONE);
                btnMapsOutlet.setVisibility(View.GONE);
                isPOI = false;
            }else{ //POI
                llJarak.setVisibility(View.VISIBLE);
                btnMapsOutlet.setVisibility(View.VISIBLE);
                isPOI = true;
            }
        }else{

            //btnMapsOutlet.setText("Peta Event");
        }
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

        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String packageName = context.getPackageName();

                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);

                } catch ( ActivityNotFoundException e ) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validasi
                if(edtNomor.getText().toString().length() == 0){

                    edtNomor.setError("Nomor harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{
                    edtNomor.setError(null);
                }

                if(edtNominal.getText().toString().length() == 0){

                    if(edtNominal.getVisibility() == View.VISIBLE){
                        edtNominal.setError("Nominal harap diisi");
                        edtNominal.requestFocus();
                    }else{

                        Toast.makeText(context, "Harga tidak termuat, harap cek proses atau kolom nominal", Toast.LENGTH_LONG).show();
                    }
                    return;
                }else{
                    edtNominal.setError(null);
                }

                if(iv.parseNullDouble(selectedHarga) <= 0){

                    Toast.makeText(context, "Harga tidak termuat, harap cek proses atau kolom nominal", Toast.LENGTH_LONG).show();
                    return;
                }

                if(isProses){

                    Toast.makeText(context, "Harap tunggu hingga proses selesai", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtPin.getText().toString().isEmpty()){

                    edtPin.setError("Pin harap diisi");
                    edtPin.requestFocus();
                    return;
                }else{

                    edtPin.setError(null);
                }

                if(cbPin.isChecked()){

                    pinManager.savePin(edtPin.getText().toString());
                }else{
                    pinManager.clearPin();
                }

                //1. kdbrg
                //2. namabrg
                //3. hargajual
                //4. flag dipilih
                //5. flag jenis order MK, BL, TC
                //6. pin rs
                //7. format inject

                isKonfirmasiManual = false;
                lastFlagOrder = selectedItemOrder.getItem5();
                lastKodebrg = selectedItemOrder.getItem1();

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Apakah anda yakin ingin memproses order?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                isSaveButtonClicked = true;
                                showDialogLoading();
                                String format = selectedItemOrder.getItem7().replace("[tujuan]",edtNomor.getText().toString());
                                if(flagOrder.equals("BL2")){
                                    format = format.replace("[nominal]", selectedHarga.substring(0, selectedHarga.length() - 3));
                                }else{
                                    format = format.replace("[nominal]", selectedHarga);
                                }
                                format = format.replace("[pin]", edtPin.getText().toString());
                                format = format.replace("#", Uri.encode("#"));

                                Log.d(TAG, "onClick: " + format);

                                //String code = "*123" + Uri.encode("#");
                                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + format)));

                                lvBalasan.setAdapter(null);
                                listBalasan  = new ArrayList<>();
                                balasanAdapter = new ListBalasanInjectAdapter((Activity) context, listBalasan);
                                lvBalasan.setAdapter(balasanAdapter);
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

        edtNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                hitungHarga();
            }
        });

        ivRefreshJarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isUpdateLocation){
                    updateAllLocation();
                }
            }
        });

        btnMapsOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLokasiReseller();
            }
        });
    }

    private static void getBarang() {

        isLoading(true);
        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", nik);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getBarangDS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                isLoading(false);
                String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listBarang = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            listBarang.add(new CustomItem(
                                    jo.getString("kodebrg"),
                                    jo.getString("namabrg"),
                                    jo.getString("hargajual"),
                                    "0",
                                    jo.getString("flag"),
                                    jo.getString("pin"),
                                    jo.getString("format")));
                            //1. kdbrg
                            //2. namabrg
                            //3. hargajual
                            //4. flag dipilih
                            //5. flag jenis order MK, BL, TC
                            //6. pin rs
                            //7. format inject
                            //8. balasan
                            //break;
                        }
                    }else{
                        DialogBox.showDialog(context, 3, message);
                    }

                    setTableBarang(listBarang);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showDialog(3, "Terjadi kesalahan saat mengambil data barang, harap ulangi");
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                isLoading(false);
                showDialog(3, result);
            }
        });
    }

    private static void setTableBarang(List<CustomItem> listBarang) {

        lvBarang.setAdapter(null);
        if(listBarang != null){

            adapterBarang = new ListBarangDSAdapter((Activity) context, listBarang);
            lvBarang.setAdapter(adapterBarang);
            if(listBarang.size() > 0) setSelectedItem(listBarang.get(0));
        }
    }

    public static void setSelectedItem(CustomItem item){

        //1. kdbrg
        //2. namabrg
        //3. hargajual
        //4. flag dipilih
        //5. flag jenis order MK, BL, TC, BL2
        //6. pin rs

        selectedItemOrder = item;

        flagOrder = item.getItem5();

        if(iv.parseNullDouble(item.getItem3()) > 0){ // ada harganya
            llNominal.setVisibility(View.GONE);
            edtNominal.setText(selectedItemOrder.getItem3());
            edtHarga.setVisibility(View.VISIBLE);
            //hitungHarga();
        }else{
            //llNominal.setVisibility(View.VISIBLE);
            edtNominal.setText("1");
            edtHarga.setVisibility(View.GONE);
        }

        if(flagOrder.equals("BL2")){ // Bulk reguler

            edtNominal.setText("");
            llNominal.setVisibility(View.VISIBLE);
            edtHarga.setVisibility(View.VISIBLE);
        }else{
            llNominal.setVisibility(View.GONE);
        }
    }

    private static void hitungHarga(){

        selectedHarga = edtNominal.getText().toString();
        edtHarga.setText(iv.ChangeToRupiahFormat(selectedHarga));
    }

    private static void showDialogLoading(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_loading_ds, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
        final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
        final Button btnKonfirmasi = (Button) viewDialog.findViewById(R.id.btn_konfirmasi);

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) {
                    try {

                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin membatalkan proses?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((Activity) context).onBackPressed();
                            }
                        })
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                showDialogLoading();
                            }
                        })
                        .show();
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showKonfirmasiDialog();
            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void showKonfirmasiDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_konfirmasi, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
        final EditText edtNominal = (EditText) viewDialog.findViewById(R.id.edt_nomimal);
        final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
        final Button btnProses = (Button) viewDialog.findViewById(R.id.btn_proses);

        currentString = "";

        edtNominal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtNominal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtNominal.setText(formatted);
                    edtNominal.setSelection(formatted.length());
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                isKonfirmasiManual = false;
                if(alert != null) {
                    try {

                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //showDialogLoading();
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String nominal = edtNominal.getText().toString().replaceAll("[,.]", "");

                if(nominal.isEmpty()){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else if(iv.parseNullDouble(nominal) == 0){

                    edtNominal.setError("Nominal harap lebih dari 0");
                    edtNominal.requestFocus();
                    return;
                }else{
                    edtNominal.setError(null);
                }

                isKonfirmasiManual = true;
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin proses order dengan nominal "+ iv.ChangeToCurrencyFormat(nominal)+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                lastSN = "";
                                selectedHarga = nominal;
                                edtNominal.setText(nominal);

                                if(isOnSaving){
                                    Toast.makeText(context, "Harap tunggu hingga proses selesai dan ulangi menyimpan", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                saveData();
                            }
                        })
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                return;
                            }
                        })
                        .show();
            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

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
                String message = "Terjadi kesalahan saat memuat data, harap ulangi";

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

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(DirectSellingPulsa.this);

        isActive = true;

        boolean isAccessGranted =  isAccessibilityEnabled(context.getPackageName() + "/" + context.getPackageName() + ".Services.USSDService");
        if(isAccessGranted){

            //Log.d(TAG, "granted");
        }else{
            //Log.d(TAG, "not granted");
            Snackbar.make(findViewById(android.R.id.content), "Mohon ijinkan accessibility pada Perkasa Sales Force, Cari Perkasa Sales Force dan ubah ke enable",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();

        }

        if(!iv.isServiceRunning(context,USSDService.class)){
            startService(new Intent(context, USSDService.class));
        }
    }

    public boolean isAccessibilityEnabled(String id){
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){
            //Log.d(TAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            //Log.d(TAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    //Log.d(TAG, "Setting: " + accessabilityService);
                    if (accessabilityService.toLowerCase().equals(id.toLowerCase())){
                        //Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            //Log.d(TAG, "***END***");
        }
        else{
            //Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    public static void addTambahBalasan(String sender, String text){

        if(session != null
                && edtNomor != null
                && edtNominal != null
                && context != null
                && selectedItemOrder != null
                && !text.toLowerCase().equals("[ussd code running…]")
                && !text.toLowerCase().equals("[phone]")
                && !text.toLowerCase().equals("[detail inject pulsa]")
                && !text.toLowerCase().equals("[]")
                && !text.toLowerCase().equals("[clipboard]")){

            try {

                if(balasanAdapter != null){

                    CustomItem item = new CustomItem(iv.getCurrentDate(FormatItem.formatTime), text);
                    balasanAdapter.addData(item);
                }

                logBalasan(sender, text);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void logBalasan(final  String sender, final String text) {

        isLoading(true);
        String nik = session.getNikGa();

        if(text.toLowerCase().contains("berhasil")){
            lastSuccessBalasan = text;
            lastSuccessSender = sender;
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", nik);
            jBody.put("sender", sender);
            jBody.put("balasan", text);
            jBody.put("flag_order", lastFlagOrder);
            jBody.put("nomor", generalizePhoneNumber(edtNomor.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.logBalasanDS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading(false);
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        String flag = response.getJSONObject("response").getString("flag");

                        if(flag.equals("1")){

                            String harga = response.getJSONObject("response").getString("harga");
                            String nomor = response.getJSONObject("response").getString("nomor");
                            String outstanding = response.getJSONObject("response").getString("outstanding");

                            if(generalizePhoneNumber(nomor).equals(generalizePhoneNumber(edtNomor.getText().toString()))
                                    && !outstanding.equals("1")){

                                lastSN = response.getJSONObject("response").getString("sn");
                                edtNomor.setText(nomor);
                                selectedHarga = harga;
                                edtNominal.setText(harga);

                                if(!isKonfirmasiManual && isSaveButtonClicked){

                                    if(isOnSaving){
                                        Toast.makeText(context, "Harap tunggu hingga proses selesai", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    saveData();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(text.toLowerCase().contains("berhasil")){
                        lastSuccessBalasan = text;
                        lastSuccessSender = sender;
                        showDialog(5, "Laporan tidak masuk, harap tekan ulangi proses");
                    }
                }
            }

            @Override
            public void onError(String result) {
                isLoading(false);
                if(text.toLowerCase().contains("berhasil")){
                    lastSuccessBalasan = text;
                    lastSuccessSender = sender;
                    showDialog(5, "Laporan tidak masuk, harap tekan ulangi proses");
                }
            }
        });

    }

    private static void saveData() {

        isOnSaving = true;
        isLoading(true);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();

        PackageInfo pInfo = null;
        String version = "";

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;

        JSONArray jData = new JSONArray();

        //1. kdbrg
        //2. namabrg
        //3. hargajual
        //4. flag dipilih
        //5. flag jenis order MK, BL, TC
        //6. pin rs
        //7. format inject
        //break;

        JSONObject jDataDetail = new JSONObject();
        try {
            jDataDetail.put("nobukti","");
            jDataDetail.put("nik", nik);
            jDataDetail.put("kodebrg", lastKodebrg);
            jDataDetail.put("ccid", "");
            jDataDetail.put("harga", selectedHarga);
            jDataDetail.put("jumlah", "1");
            jDataDetail.put("total", selectedHarga);
            jDataDetail.put("kdcus", kdcus);
            jDataDetail.put("nama", nama);
            jDataDetail.put("alamat", alamat);
            jDataDetail.put("nomor", generalizePhoneNumber(edtNomor.getText().toString()));
            jDataDetail.put("jarak", jarak);
            jDataDetail.put("sn", lastSN);
            jDataDetail.put("transaction_id", String.valueOf(transactionID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jData.put(jDataDetail);

        try {
            jBody.put("version", version);
            jBody.put("data", jData);
            jBody.put("latitude", iv.doubleToStringFull(location.getLatitude()));
            jBody.put("longitude", iv.doubleToStringFull(location.getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ServerURL.saveOrderDS, method = "POST";
        ApiVolley request = new ApiVolley(context, jBody, method, url, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isOnSaving = false;
                isLoading(false);
                try {

                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }

                String superMessage = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    superMessage = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        Intent intent = new Intent(context, ActivityHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("flag", flag);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }else{
                        //Toast.makeText(DetailOrderPulsa.this, superMessage, Toast.LENGTH_LONG).show();
                        showDialog(2, superMessage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, superMessage, Toast.LENGTH_LONG).show();
                    showDialog(4, "Laporan tidak masuk, harap tekan ulangi proses");
                }
            }

            @Override
            public void onError(String result) {

                isOnSaving = false;
                isLoading(false);

                try {

                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //Toast.makeText(context, "Terjadi kesalahan saat menyimpan data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                showDialog(4, "Laporan tidak masuk, harap tekan ulangi proses");
            }
        });
    }

    private static void showDialog(int state, String message){

        if(state == 1){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_success, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {
                        try {

                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(state == 2){ // failed
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_failed, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {
                        try {

                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(state == 3){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null){

                        try {

                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    getBarang();
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(state == 4){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null){
                        try {

                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if(isOnSaving){
                        Toast.makeText(context, "Harap tunggu hingga proses selesai dan ulangi menyimpan", Toast.LENGTH_LONG).show();
                        return;
                    }

                    saveData();
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(state == 5){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {

                        try {

                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    logBalasan(lastSuccessSender, lastSuccessBalasan);
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void isLoading(boolean status){

        isProses = status;

        if(pbProses != null){
            if(status){
                pbProses.setVisibility(View.VISIBLE);
            }else{
                pbProses.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        isActive = false;
    }

    //region location

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
                        if (ActivityCompat.checkSelfPermission(DirectSellingPulsa.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DirectSellingPulsa.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(DirectSellingPulsa.this, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult(DirectSellingPulsa.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(DirectSellingPulsa.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(DirectSellingPulsa.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(DirectSellingPulsa.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DirectSellingPulsa.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DirectSellingPulsa.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(DirectSellingPulsa.this,
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
        ActivityCompat.requestPermissions(DirectSellingPulsa.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation && !kdcus.isEmpty()){
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

        try {
            stopService(new Intent(context, USSDService.class));
        }catch (Exception e){
            e.printStackTrace();
        }

        isSaveButtonClicked = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSaveButtonClicked = false;
    }

    private static String generalizePhoneNumber(String number){

        String result = number;

        if(number.length() > 3){

            if(number.startsWith("+62")){

                result = number.replace("+62", "0");
            }else if(number.startsWith("62")){

                result = "0" + number.substring(2);
            }else if(number.startsWith("8")){

                result = "0" + number;
            }
        }

        return result;
    }

}
