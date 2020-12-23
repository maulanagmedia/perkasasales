package id.net.gmedia.perkasaapp.ActKunjunganAdmin;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DetailKunjunganAdmin extends AppCompatActivity implements LocationListener {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private SessionManager session;

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
    private Button btnProses, btnPeta;
    private String idKunjungan = "";
    private EditText edtNama;
    private ProgressBar pbLoading;
    private static int PICK_IMAGE_REQUEST = 1212;
    private boolean isEdit = false;
    public static final String flag = "KUNJUNGAN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kunjungan_admin);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Survey Kunjungan Admin");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initLocationUtils();
        initUI();
        initEvent();
    }

    private void initUI() {

        edtNama = (EditText) findViewById(R.id.edt_nama);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);
        btnPeta = (Button) findViewById(R.id.btn_peta);

        //initial data image
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus","");
            String nama = bundle.getString("nama","");
            edtNama.setText(nama);
        }
    }

    private void initEvent() {

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

                if(tvJarak.getText().toString().isEmpty()){

                    Toast.makeText(context, "Mohon tunggu hingga jarak diketahui atau tekan tombol refresh", Toast.LENGTH_LONG).show();
                    return;
                }

                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin melanjutkan ke survey?")
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.validateSurveyAdmin, new ApiVolley.VolleyCallback() {
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

                        Intent intent = new Intent(context, SurveyKunjunganAdmin.class);
                        intent.putExtra("kdcus", kdcus);
                        intent.putExtra("lat", iv.doubleToStringFull(latitude));
                        intent.putExtra("long", iv.doubleToStringFull(longitude));
                        startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(DetailKunjunganAdmin.this);
    }

    //region File Chooser


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
                        if (ActivityCompat.checkSelfPermission(DetailKunjunganAdmin.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailKunjunganAdmin.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(DetailKunjunganAdmin.this, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult(DetailKunjunganAdmin.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailKunjunganAdmin.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailKunjunganAdmin.this,
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
        ActivityCompat.requestPermissions(DetailKunjunganAdmin.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation && idKunjungan.isEmpty()){
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
