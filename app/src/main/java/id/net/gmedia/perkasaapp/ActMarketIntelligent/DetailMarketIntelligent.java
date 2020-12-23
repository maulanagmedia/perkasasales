package id.net.gmedia.perkasaapp.ActMarketIntelligent;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.PermissionUtils;
import com.maulana.custommodul.PhotoModel;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.intik.overflowindicator.OverflowPagerIndicator;
import cz.intik.overflowindicator.SimpleSnapHelper;
import id.net.gmedia.perkasaapp.ActBranding.Adapter.PhotosAdapter;
import id.net.gmedia.perkasaapp.ActMarketIntelligent.Adapter.PhotosKeteranganAdapter;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

import static com.maulana.custommodul.ImageUtils.getImageUri;

public class DetailMarketIntelligent extends AppCompatActivity implements LocationListener {

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
    private boolean isLoading = false;
    private String kdcus = "";
    private String state = "";
    private final String TAG = "DB";

    //Umum
    private Button btnProses, btnPeta;
    private TextView tvNama;
    private RecyclerView rvKompetitor, rvSinyal, rvBooked;
    private ImageButton ibKompetitor, ibSinyal, ibBooked;
    private PhotosAdapter adapterSinyal, adapterBooked;
    private PhotosKeteranganAdapter adapterKompetitor;
    private List<PhotoModel> listKompetitor = new ArrayList<>(), listSinyal = new ArrayList<>(), listBooked = new ArrayList<>();
    private OverflowPagerIndicator opiKompetitor, opiSinyal, opiBooked;
    private String idMarketIntelligent = "";

    //Upload Handler
    private static int RESULT_OK = -1;
    private static int PICK_IMAGE_REQUEST = 1212;
    private ImageUtils iu = new ImageUtils();
    private File saveDirectory;
    private ProgressDialog dialog;
    private String filePathURI;
    private File sourceFile;
    private int totalSize;
    private SessionManager session;
    private String token0 = "", token1 = "", token2 = "", token3 = "", token4 = "", token5 = "";
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private int stateUpload = 1;
    private String imageFilePath = "";
    private Uri photoURI;
    private int counterKompetitor = 0, counterSinyal = 0, counterBooked = 0;
    private int statusUpload = 0;
    private String currentFlag = "1";
    public static final String flag = "MARKETINTELLIGENT";
    private File photoFile;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_market_intelligent);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Market Intelligent");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initLocationUtils();
        initUI();
        initEvent();
        if(!idMarketIntelligent.isEmpty()) initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(DetailMarketIntelligent.this);
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        rvKompetitor = (RecyclerView) findViewById(R.id.rv_kompetitor);
        rvSinyal = (RecyclerView) findViewById(R.id.rv_sinyal);
        rvBooked= (RecyclerView) findViewById(R.id.rv_booked);
        ibKompetitor = (ImageButton) findViewById(R.id.ib_kompetitor);
        ibSinyal = (ImageButton) findViewById(R.id.ib_sinyal);
        ibBooked = (ImageButton) findViewById(R.id.ib_booked);
        opiKompetitor = findViewById(R.id.opi_kompetitor);
        opiSinyal = findViewById(R.id.opi_sinyal);
        opiBooked = findViewById(R.id.opi_booked);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);
        btnPeta = (Button) findViewById(R.id.btn_peta);
        isEdit = false;
        isLoading = false;

        listKompetitor = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterKompetitor = new PhotosKeteranganAdapter(context, listKompetitor);
        rvKompetitor.setLayoutManager(layoutManager);
        rvKompetitor.setAdapter(adapterKompetitor);
        opiKompetitor.attachToRecyclerView(rvKompetitor);
        new SimpleSnapHelper(opiKompetitor).attachToRecyclerView(rvKompetitor);

        listSinyal = new ArrayList<>();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterSinyal = new PhotosAdapter(context, listSinyal);
        rvSinyal.setLayoutManager(layoutManager1);
        rvSinyal.setAdapter(adapterSinyal);
        opiSinyal.attachToRecyclerView(rvSinyal);
        new SimpleSnapHelper(opiSinyal).attachToRecyclerView(rvSinyal);

        listBooked = new ArrayList<>();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterBooked = new PhotosAdapter(context, listBooked);
        rvBooked.setLayoutManager(layoutManager2);
        rvBooked.setAdapter(adapterBooked);
        opiBooked.attachToRecyclerView(rvBooked);
        new SimpleSnapHelper(opiBooked).attachToRecyclerView(rvBooked);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            String nama = bundle.getString("nama", "");

            idMarketIntelligent = bundle.getString("id", "");

            tvNama.setText(nama);

            if(!idMarketIntelligent.isEmpty()){

                isEdit = true;
                //btnProses.setEnabled(false);
            }else{
                //btnProses.setEnabled(true);
            }
        }

    }

    private void initEvent() {

        ibKompetitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stateUpload = 1;
                loadChooserDialog();
            }
        });

        ibSinyal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stateUpload = 2;
                loadChooserDialog();
            }
        });

        ibBooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stateUpload = 3;
                loadChooserDialog();
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

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isEdit){

                    Toast.makeText(context, "Data yang telah tersimpan tidak dapat diubah", Toast.LENGTH_LONG).show();
                    return;
                }

                if(listKompetitor.size() == 0){

                    Toast.makeText(context, "Foto Info Kompetitor harap diisi", Toast.LENGTH_LONG).show();
                    return;
                }

                if(listSinyal.size() == 0){

                    Toast.makeText(context, "Foto Info Sinyal harap diisi", Toast.LENGTH_LONG).show();
                    return;
                }

                if(listBooked.size() == 0){

                    Toast.makeText(context, "Foto Log Booked harap diisi", Toast.LENGTH_LONG).show();
                    return;
                }

                if(tvJarak.getText().toString().isEmpty()){

                    Toast.makeText(context, "Mohon tunggu hingga jarak diketahui", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses data market intelligent?")
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
    }

    private void saveData() {

        if(idMarketIntelligent.isEmpty()){

            btnProses.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Menyimpan...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            JSONObject jBody = new JSONObject();
            try {

                jBody.put("kdcus", kdcus);
                jBody.put("latitude", iv.doubleToStringFull(latitude));
                jBody.put("longitude", iv.doubleToStringFull(longitude));
                jBody.put("state", state);
                String imei = "";
                ArrayList<String> imeis = iv.getIMEI(context);
                if(imeis != null) if(imeis.size() > 0) imei = imeis.get(0);
                jBody.put("imei", imei);
                jBody.put("jarak", jarak);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveMarketIntelligentHeader, new ApiVolley.VolleyCallback() {
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

                            JSONObject jo = response.getJSONObject("response");
                            idMarketIntelligent = jo.getString("id");
                            currentFlag = "1";
                            counterKompetitor = listKompetitor.size() - 1;
                            counterSinyal = listSinyal.size() - 1;
                            counterBooked = listBooked.size() - 1;

                            if(counterKompetitor >= 0){
                                new UploadFileToServer().execute();
                            }
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
        }else{

            currentFlag = "1";
            currentFlag = "1";
            counterKompetitor = listKompetitor.size() - 1;
            counterSinyal = listSinyal.size() - 1;
            counterBooked = listBooked.size() - 1;

            if(counterKompetitor >= 0){
                new UploadFileToServer().execute();
            }
        }
    }

    private void loadChooserDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_chooser, null);
        builder.setView(view);

        final LinearLayout llBrowse= (LinearLayout) view.findViewById(R.id.ll_browse);
        final LinearLayout llCamera = (LinearLayout) view.findViewById(R.id.ll_camera);

        final AlertDialog alert = builder.create();

        llBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
                alert.dismiss();
            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();
                alert.dismiss();
            }
        });

        alert.show();
    }

    //region File Chooser

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void openCamera(){

        if(PermissionUtils.hasPermissions(context, Manifest.permission.CAMERA)){

            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);*/

            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //Create a file to store the image
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(context,
                            "id.net.gmedia.perkasaapp.provider", photoFile);

                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            REQUEST_IMAGE_CAPTURE);
                }
            }
        }else{

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Ijin dibutuhkan")
                    .setMessage("Ijin dibutuhkan untuk mengakses kamera, harap ubah ijin kamera ke \"diperbolehkan\"")
                    .setPositiveButton("Buka Ijin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
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

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", idMarketIntelligent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getListMarketIntelligent, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        //JSONObject jo = response.getJSONObject("response");
                        JSONArray jArray = response.getJSONArray("response");
                        for(int i = 0; i < jArray.length(); i++){

                            JSONObject jo = jArray.getJSONObject(i);
                            if(jo.getString("keterangan_tipe").toLowerCase().equals("info kompetitor")){
                                listKompetitor.add(new PhotoModel("", jo.getString("gambar"), jo.getString("keterangan")));
                            }else if(jo.getString("keterangan_tipe").toLowerCase().equals("info sinyal")){
                                listSinyal.add(new PhotoModel("", jo.getString("gambar")));
                            }else if(jo.getString("keterangan_tipe").toLowerCase().equals("log booked")){
                                listBooked.add(new PhotoModel("", jo.getString("gambar")));
                            }
                            latitude = iv.parseNullDouble(jo.getString("latitude"));
                            longitude = iv.parseNullDouble(jo.getString("longitude"));
                            state = jo.getString("state");
                        }

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
                            initData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                /*adapterTelkomsel = new AdapterProviderTelkomsel(listTelkomsel, context);
                adapterNonTelkomsel = new AdapterProviderNonTelkomsel(listNonTelkomsel, context);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                rvTelkomsel.setLayoutManager(layoutManager);
                rvTelkomsel.setItemAnimator(new DefaultItemAnimator());
                rvTelkomsel.setAdapter(adapterTelkomsel);*/
                adapterKompetitor.notifyDataSetChanged();
                adapterSinyal.notifyDataSetChanged();
                adapterBooked.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

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
                        if (ActivityCompat.checkSelfPermission(DetailMarketIntelligent.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailMarketIntelligent.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(DetailMarketIntelligent.this, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult(DetailMarketIntelligent.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(DetailMarketIntelligent.this, errorMessage, Toast.LENGTH_LONG).show();
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

        }else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imgUri = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        imgUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);

            Uri filePath = getImageUri(getApplicationContext(), imageBitmap);

            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, filePath, namaFile, null);

        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getExtras().get("data") != null) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri filePath = getImageUri(getApplicationContext(), photo);
            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, filePath, namaFile, null);

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Cursor returnCursor =
                    getContentResolver().query(photoURI, null, null, null, null);

            Matrix matrix = new Matrix();
            try {

                ExifInterface exif = new ExifInterface(photoFile.toString());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = iv.exifToDegrees(rotation);
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            } catch (Exception e) {
                e.printStackTrace();
            }

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, photoURI, namaFile, matrix);
        }
    }

    public boolean copyFileFromUri(Context context, Uri fileUri, String namaFile, Matrix matrix)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String extension = namaFile.substring(namaFile.lastIndexOf("."));
        FileOutputStream out = null;

        try
        {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if(root == null){
                //Log.d(TAG, "Failed to get root");
            }

            // create a directory
            saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "PerkasaSalesForce"  +File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            final int time = (int) (new Date().getTime()/1000);

            extension = extension.toLowerCase();
            if(extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png") || extension.equals(".bmp")){

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                Bitmap bm2 = BitmapFactory.decodeStream(inputStream);
                int scale = 80;

                int imageHeight = bm2.getHeight();
                int imageWidth = bm2.getWidth();

                int newWidth = 0;
                int newHeight = 0;

                if(imageHeight > imageWidth){

                    newWidth = 640;
                    newHeight = newWidth * imageHeight / imageWidth;
                }else{

                    newHeight = 640;
                    newWidth = newHeight * imageWidth / imageHeight;
                }

                bm2 = Bitmap.createScaledBitmap(bm2, newWidth, newHeight, false);
                if(matrix != null) bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);

                bm2.compress(Bitmap.CompressFormat.JPEG, scale, outputStream);

                File file = new File(saveDirectory, time + namaFile);
                //Log.i(TAG, "" + file);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream outstreamBitmap = new FileOutputStream(file);
                    bm2.compress(Bitmap.CompressFormat.JPEG, scale, outstreamBitmap);
                    outstreamBitmap.flush();
                    outstreamBitmap.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                if(outputStream != null){
                    Log.e( TAG, "Output Stream Opened successfully");
                }

                byte[] buffer = new byte[1000];
                int bytesRead = 0;
                while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 )
                {
                    outputStream.write( buffer, 0, buffer.length );
                }
            }

            filePathURI = Environment.getExternalStorageDirectory() + File.separator + "PerkasaSalesForce"  +File.separator + time + namaFile;

            if(stateUpload == 1){

                listKompetitor.add(new PhotoModel(filePathURI, "", ""));
                adapterKompetitor.notifyDataSetChanged();
            }else if(stateUpload == 2){

                listSinyal.add(new PhotoModel(filePathURI, ""));
                adapterSinyal.notifyDataSetChanged();
            }else if(stateUpload == 3){

                listBooked.add(new PhotoModel(filePathURI, ""));
                adapterBooked.notifyDataSetChanged();
            }
            //new UploadFileToServer().execute();
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            statusUpload = 1;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog dialogConf = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Konfirmasi")
                            .setMessage("Anda yakin ingin membatalkan proses")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    statusUpload = 0;
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.show();
                                }
                            })
                            .show();
                }
            });
            dialog.show();

            //uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            if(currentFlag.equals("1")){

                sourceFile = new File(listKompetitor.get(counterKompetitor).getUrl());
            }else if (currentFlag.equals("2")){

                sourceFile = new File(listSinyal.get(counterSinyal).getUrl());
            }else if (currentFlag.equals("3")){

                sourceFile = new File(listBooked.get(counterBooked).getUrl());
            }
            totalSize = (int) sourceFile.length();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //Log.d("PROG", progress[0]);
            //pbLoading.setProgress(Integer.parseInt(progress[0])); //Updating progress
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFile.getName();
            //Log.d(TAG, "filename upload: " +fileName);
            String result = "";

            session = new SessionManager(context);
            token0  = iv.encodeMD5(iv.encodeBase64(iv.getCurrentDate("SSSHHMMddmmyyyyss")));
            token1  = session.getNikGa();
            token2  = iv.getCurrentDate("SSSHHyyyyssMMddmm");
            token3  = iv.sha256(token1+"&"+token2,token1+"die");
            token4  = session.getNikMkios();
            token5  = session.getNikGa();

            try {

                connection = (HttpURLConnection) new URL(ServerURL.saveMarketIntelligentDetail).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Client-Service", "perkasa-sales");
                connection.setRequestProperty("Auth-Key", "gmedia");
                connection.setRequestProperty("User-Id", token1);
                connection.setRequestProperty("Timestamp", token2);
                connection.setRequestProperty("Signature", token3);
                connection.setRequestProperty("Nik-Mkios", token4);
                connection.setRequestProperty("Nik-Ga", token5);
                connection.setRequestProperty("id_market", idMarketIntelligent);
                connection.setRequestProperty("tipe", currentFlag);
                if(currentFlag.equals("1")) connection.setRequestProperty("keterangan", listKompetitor.get(counterKompetitor).getKeterangan());
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"fileToUpload\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress(""+(int)((progress*100)/totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    result = line;
                }

            } catch (Exception e) {
                // Exception
            } finally {
                if (connection != null) connection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                dialog.dismiss();
            } catch(Exception e) {
            }

            try {
                result = result.replace("</div>", "");
                JSONObject response = new JSONObject(result);
                String status = response.getJSONObject("metadata").getString("status");
                String message = response.getJSONObject("metadata").getString("message");
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(status.equals("200")){

                    if(currentFlag.equals("1")){

                        counterKompetitor--;
                        if(counterKompetitor < 0){
                            currentFlag = "2";
                        }

                        new UploadFileToServer().execute();

                    }else if(currentFlag.equals("2")){

                        counterSinyal--;
                        if(counterSinyal < 0){

                            currentFlag = "3";
                        }

                        new UploadFileToServer().execute();

                    }else if(currentFlag.equals("3")){

                        counterBooked--;
                        if(counterBooked < 0){

                            //done
                            Toast.makeText(context, "All files already uploaded", Toast.LENGTH_LONG).show();
                            saveFinalMarketIntelligent();
                        }else{

                            new UploadFileToServer().execute();
                        }
                    }

                    /*if(statusUpload == 1){

                        getChatData();
                    }*/
                    //berhasil
                }else{
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
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
                Toast.makeText(DetailMarketIntelligent.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(DetailMarketIntelligent.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailMarketIntelligent.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailMarketIntelligent.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailMarketIntelligent.this,
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

    private void saveFinalMarketIntelligent() {

        btnProses.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {

            jBody.put("id", idMarketIntelligent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveFinalMarketIntel, new ApiVolley.VolleyCallback() {
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
        ActivityCompat.requestPermissions(DetailMarketIntelligent.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation && idMarketIntelligent.isEmpty()){
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
