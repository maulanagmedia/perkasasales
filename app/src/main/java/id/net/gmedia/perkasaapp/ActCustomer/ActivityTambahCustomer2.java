package id.net.gmedia.perkasaapp.ActCustomer;

import android.Manifest;
import android.app.Activity;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.PermissionUtils;
import com.maulana.custommodul.PhotoModel;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.intik.overflowindicator.OverflowPagerIndicator;
import cz.intik.overflowindicator.SimpleSnapHelper;
import id.net.gmedia.perkasaapp.ActBranding.Adapter.PhotosAdapter;
import id.net.gmedia.perkasaapp.ActVerifikasiReseller.ActivityVerifikasiOutlet1;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.CustomMapView;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityTambahCustomer2 extends AppCompatActivity implements OnMapReadyCallback,LocationListener {


    private ModelOutlet outlet;
    private TextView txt_nama, txt_alamat, txt_kota, txt_nomor, txt_nomorhp, txt_email, txt_bank
            , txt_rekening, txt_cp, txt_area, txt_tempo, txt_ktp, txt_kelurahan, txt_kecamatan, txt_digipos, txt_kategori_outlet, txt_jenis_outlet, txt_limit_order_malam, txt_limit_konsinyasi;
    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION = 2;
    private final int REQUEST_PERMISSION_FINE_LOCATION = 3;
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
    private String TAG = "DetailCustomer";
    public static final String flag = "DETAILCUSTOMER";
    private SessionManager session;
    private GoogleMap maps;
    private String address0 = "";
    private Button btnRefreshPosition;

    private List<PhotoModel> listPhoto = new ArrayList<>();
    private OverflowPagerIndicator opiPhoto;
    private RecyclerView rvPhoto;
    private ImageButton ibPhoto;
    private PhotosAdapter adapterPhoto;
    private String imageFilePath = "";
    private File photoFile;
    private int RESULT_OK = -1;
    private int PICK_IMAGE_REQUEST = 1212;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri photoURI;
    private File saveDirectory;
    private String filePathURI = "";
    private CheckBox cbTempo, cbKonsinyasi;
    private Spinner spnKelurahan, spnKecamatan, spnKabupaten, spnSegmentasi, spnKategoriOutlet, spnJenisOutlet;
    private String kdcus = "", statusCustomer = "";
    private boolean isEdit = false;
    private Button btnTolak, btnSimpan;
    private RelativeLayout rlKtp;
    private Button btnKtp;
    private boolean isKtp = false;
    private ImageView ivKtp;
    private boolean isVerifikasi = false;
    private List<OptionItem> listSegmentasi = new ArrayList<>(),
            listKelurahan = new ArrayList<>()
            , listKecamatan = new ArrayList<>()
            , listKabupaten = new ArrayList<>()
            , listKategoriOutlet = new ArrayList<>()
            , listJenisOutlet = new ArrayList<>();

    private String fileKtp = "";
    private int defaultSelectedSegment = 0
            , defaultSelectedKelurahan = 0
            , defaultSelectedKecamatan = 0
            , defaultSelectedKabupaten = 0
            , defaultSelectedKategori = 0
            , defaultSelectedJenis = 0;

    private String idKab = "", idKec = "";
    private ArrayAdapter adapterKelurahan, adapterKecamatan, adapterKabupaten, adapterKategoriOutelt, adapterJenisOutlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_customer2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Reseller");
        }

        context = this;
        isEdit = false;
        isVerifikasi = false;
        dialogBox = new DialogBox(context);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            statusCustomer = bundle.getString("status", "");
           if(!kdcus.isEmpty()) isEdit = true;
        }

        initLocationUtils();
        initUI();
        initEvent();
        getDaerah();
    }

    private void initUI() {

        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_kota = findViewById(R.id.txt_kota);
        txt_nomor = findViewById(R.id.txt_nomor);
        txt_nomorhp = findViewById(R.id.txt_nomorhp);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_bank = (EditText) findViewById(R.id.txt_bank);
        txt_rekening = (EditText) findViewById(R.id.txt_rekening);
        txt_cp = (EditText) findViewById(R.id.txt_kontak);
        cbTempo = (CheckBox) findViewById(R.id.cb_tempo);
        txt_tempo = (EditText) findViewById(R.id.txt_tempo);
        txt_digipos = (EditText) findViewById(R.id.txt_digipos);
        txt_kategori_outlet = (EditText) findViewById(R.id.txt_kategori_outlet);
        txt_jenis_outlet = (EditText) findViewById(R.id.txt_jenis_outlet);
        txt_ktp = (EditText) findViewById(R.id.txt_ktp);
        txt_kelurahan = (EditText) findViewById(R.id.txt_kelurahan);
        txt_kecamatan = (EditText) findViewById(R.id.txt_kecamatan);

        spnKelurahan = (Spinner) findViewById(R.id.spn_kelurahan);
        spnKecamatan = (Spinner) findViewById(R.id.spn_kecamatan);
        spnKabupaten = (Spinner) findViewById(R.id.spn_kabupaten);
        spnSegmentasi = (Spinner) findViewById(R.id.spn_segmentasi);
        spnKategoriOutlet = (Spinner) findViewById(R.id.spn_kategori_outlet);
        spnJenisOutlet = (Spinner) findViewById(R.id.spn_jenis_outlet);

        spnSegmentasi.setEnabled(false);

        adapterKelurahan = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listKelurahan);
        spnKelurahan.setAdapter(adapterKelurahan);

        adapterKecamatan = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listKecamatan);
        spnKecamatan.setAdapter(adapterKecamatan);

        adapterKabupaten = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listKabupaten);
        spnKabupaten.setAdapter(adapterKabupaten);

        adapterKategoriOutelt = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listKategoriOutlet);
        spnKategoriOutlet.setAdapter(adapterKategoriOutelt);

        adapterJenisOutlet = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listJenisOutlet);
        spnJenisOutlet.setAdapter(adapterJenisOutlet);

        txt_limit_order_malam = (EditText) findViewById(R.id.txt_limit_order_malam);
        cbKonsinyasi = (CheckBox) findViewById(R.id.cb_konsinyasi);
        txt_limit_konsinyasi = (EditText) findViewById(R.id.txt_limit_konsinyasi);

        txt_area = (EditText) findViewById(R.id.txt_area);
        btnRefreshPosition = (Button) findViewById(R.id.btn_refresh_position);
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        ibPhoto = (ImageButton) findViewById(R.id.ib_photo);
        opiPhoto = (OverflowPagerIndicator) findViewById(R.id.opi_photo);
        ivKtp = (ImageView) findViewById(R.id.iv_ktp);
        rlKtp = (RelativeLayout) findViewById(R.id.rl_ktp);
        btnKtp = (Button) findViewById(R.id.btn_ktp);
        btnTolak = (Button) findViewById(R.id.btn_tolak);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);

        listPhoto = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterPhoto = new PhotosAdapter(context, listPhoto);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(adapterPhoto);
        opiPhoto.attachToRecyclerView(rvPhoto);
        new SimpleSnapHelper(opiPhoto).attachToRecyclerView(rvPhoto);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!statusCustomer.isEmpty()){

            btnSimpan.setText("Setujui");

            if(statusCustomer.equals("2")){ //butuh verifikasi spv

                isVerifikasi = true;
                btnTolak.setVisibility(View.VISIBLE);
                rlKtp.setVisibility(View.VISIBLE);
            }else{

                isVerifikasi = false;
                rlKtp.setVisibility(View.VISIBLE);
                btnTolak.setVisibility(View.GONE);
                btnSimpan.setVisibility(View.GONE);
            }
        }else{

            rlKtp.setVisibility(View.GONE);
            btnTolak.setVisibility(View.GONE);

            if(isEdit){
                btnSimpan.setVisibility(View.GONE);
            }

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

        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isKtp = false;
                loadChooserDialog();
            }
        });

        btnKtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isKtp = true;
                loadChooserDialog();
            }
        });

        btnRefreshPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //refreshMode = true;
                //location = getLocation();
                updateAllLocation();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(isEdit){

                }

                if(txt_nama.getText().toString().isEmpty()){

                    txt_nama.setError("Harap diisi");
                    txt_nama.requestFocus();
                    return;
                }else{

                    txt_nama.setError(null);
                }

                if(txt_alamat.getText().toString().isEmpty()){

                    txt_alamat.setError("Harap diisi");
                    txt_alamat.requestFocus();
                    return;
                }else{

                    txt_alamat.setError(null);
                }

                if(cbKonsinyasi.isChecked() && txt_limit_konsinyasi.getText().toString().isEmpty()){

                    txt_limit_konsinyasi.setError("Harap diisi");
                    txt_limit_konsinyasi.requestFocus();
                    return;
                }else{

                    txt_limit_konsinyasi.setError(null);
                }

                if(cbTempo.isChecked() && txt_tempo.getText().toString().isEmpty()){

                    txt_tempo.setError("Harap diisi");
                    txt_tempo.requestFocus();
                    return;
                }else{

                    txt_tempo.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(isVerifikasi) statusCustomer = "3";
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

        btnTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(isEdit){


                }

                if(txt_nama.getText().toString().isEmpty()){

                    txt_nama.setError("Harap diisi");
                    txt_nama.requestFocus();
                    return;
                }else{

                    txt_nama.setError(null);
                }

                if(txt_alamat.getText().toString().isEmpty()){

                    txt_alamat.setError("Harap diisi");
                    txt_alamat.requestFocus();
                    return;
                }else{

                    txt_alamat.setError(null);
                }

                if(cbKonsinyasi.isChecked() && txt_limit_konsinyasi.getText().toString().isEmpty()){

                    txt_limit_konsinyasi.setError("Harap diisi");
                    txt_limit_konsinyasi.requestFocus();
                    return;
                }else{

                    txt_limit_konsinyasi.setError(null);
                }

                if(cbTempo.isChecked() && txt_tempo.getText().toString().isEmpty()){

                    txt_tempo.setError("Harap diisi");
                    txt_tempo.requestFocus();
                    return;
                }else{

                    txt_tempo.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                statusCustomer = "0";
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

        btnSimpan.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONArray jImage = new JSONArray();

        for(PhotoModel image: listPhoto){

            JSONObject dataImage = new JSONObject();
            try {
                dataImage.put("foto", image.getKeterangan());
                jImage.put(dataImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nama", txt_nama.getText().toString());
            jBody.put("alamat", txt_alamat.getText().toString());
            jBody.put("kota", ((OptionItem) spnKabupaten.getSelectedItem()).getText());
            jBody.put("nomor", txt_nomor.getText().toString());
            jBody.put("hp_owner", txt_nomorhp.getText().toString());
            jBody.put("email", txt_email.getText().toString());
            jBody.put("bank", txt_bank.getText().toString());
            jBody.put("rekening", txt_rekening.getText().toString());
            jBody.put("flag_tempo", cbTempo.isChecked() ? "Y" : "N");
            jBody.put("tempo", txt_tempo.getText().toString());
            jBody.put("contact", txt_cp.getText().toString());
            jBody.put("digipos", txt_digipos.getText().toString());
            jBody.put("kategori", ((OptionItem) spnKategoriOutlet.getSelectedItem()).getText());
            jBody.put("jenis", ((OptionItem) spnJenisOutlet.getSelectedItem()).getText());
            jBody.put("id_kategori_outlet", ((OptionItem) spnKategoriOutlet.getSelectedItem()).getValue());
            jBody.put("id_jenis_outlet", ((OptionItem) spnJenisOutlet.getSelectedItem()).getValue());
            String segmentasi = "";
            try {
                if(spnSegmentasi.getSelectedItem() != null){
                    segmentasi = ((OptionItem) spnSegmentasi.getSelectedItem()).getValue();
                }
            }catch (Exception e) {}
            jBody.put("segmentasi", segmentasi);
            jBody.put("limit_malam", txt_limit_order_malam.getText().toString());
            jBody.put("flag_konsinyasi", cbKonsinyasi.isChecked() ? "Y" : "N");
            jBody.put("jumlah_konsinyasi", txt_limit_konsinyasi.getText().toString());
            jBody.put("kelurahan", ((OptionItem) spnKelurahan.getSelectedItem()).getText());
            jBody.put("kecamatan", ((OptionItem) spnKecamatan.getSelectedItem()).getText());
            jBody.put("id_kelurahan", ((OptionItem) spnKelurahan.getSelectedItem()).getValue());
            jBody.put("id_kecamatan", ((OptionItem) spnKecamatan.getSelectedItem()).getValue());
            jBody.put("id_kabupaten", ((OptionItem) spnKabupaten.getSelectedItem()).getValue());
            jBody.put("no_ktp", txt_ktp.getText().toString());
            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            String imei = "";
            ArrayList<String> imeis = iv.getIMEI(context);
            if(imeis != null) if(imeis.size() > 0) imei = imeis.get(0);
            jBody.put("imei", imei);
            jBody.put("image", jImage);
            if(isVerifikasi){
                jBody.put("kdcus", kdcus);
                jBody.put("status", statusCustomer);
                jBody.put("ktp", fileKtp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ServerURL.saveCustomer;
        if(isVerifikasi) url = ServerURL.updateCustomer;
        ApiVolley request = new ApiVolley(context, jBody, "POST", url, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                btnSimpan.setEnabled(true);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        Intent intent = new Intent(context, ActivityHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("flag", isVerifikasi ? ActivityVerifikasiOutlet1.flag :flag);
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
                btnSimpan.setEnabled(true);
            }
        });
    }

    private void loadChooserDialog(){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_chooser, null);
        builder.setView(view);

        final LinearLayout llBrowse= (LinearLayout) view.findViewById(R.id.ll_browse);
        final LinearLayout llCamera = (LinearLayout) view.findViewById(R.id.ll_camera);

        final android.app.AlertDialog alert = builder.create();

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

        if(PermissionUtils.hasPermissions(context,Manifest.permission.CAMERA)){

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

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
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

    private void getDaerah(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDaerah, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan, harap ulangi proses";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listKelurahan.clear();
                    listKecamatan.clear();
                    listKabupaten.clear();
                    listKategoriOutlet.clear();
                    listJenisOutlet.clear();

                    if(iv.parseNullInteger(status) == 200){

                        defaultSelectedKelurahan = 0;
                        defaultSelectedKecamatan = 0;
                        defaultSelectedKabupaten = 0;
                        defaultSelectedKategori = 0;
                        defaultSelectedJenis = 0;

                        JSONArray jaKelurahan = response.getJSONObject("response").getJSONArray("kelurahan");
                        JSONArray jaKecamatan = response.getJSONObject("response").getJSONArray("kecamatan");
                        JSONArray jaKabupaten = response.getJSONObject("response").getJSONArray("kabupaten");
                        JSONArray jaKategori = response.getJSONObject("response").getJSONArray("kategori");
                        JSONArray jaJenis = response.getJSONObject("response").getJSONArray("jenis");

                        // kelurahan
                        for(int i = 0; i < jaKelurahan.length(); i++){

                            JSONObject jo = jaKelurahan.getJSONObject(i);
                            listKelurahan.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("kelurahan")
                                    ,jo.getString("id_kec")
                            ));
                        }

                        // kecamatan
                        for(int i = 0; i < jaKecamatan.length(); i++){

                            JSONObject jo = jaKecamatan.getJSONObject(i);
                            listKecamatan.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("kecamatan")
                                    ,jo.getString("id_kab")
                            ));
                        }

                        // kabupaten
                        for(int i = 0; i < jaKabupaten.length(); i++){

                            JSONObject jo = jaKabupaten.getJSONObject(i);
                            listKabupaten.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("kabupaten")
                            ));
                        }

                        // kategori
                        for(int i = 0; i < jaKategori.length(); i++){

                            JSONObject jo = jaKategori.getJSONObject(i);
                            listKategoriOutlet.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("nama")
                            ));
                        }

                        // Jenis
                        for(int i = 0; i < jaJenis.length(); i++){

                            JSONObject jo = jaJenis.getJSONObject(i);
                            listJenisOutlet.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("nama")
                            ));
                        }

                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                    adapterKelurahan.notifyDataSetChanged();
                    adapterKecamatan.notifyDataSetChanged();
                    adapterKabupaten.notifyDataSetChanged();
                    adapterKategoriOutelt.notifyDataSetChanged();
                    adapterJenisOutlet.notifyDataSetChanged();

                    spnKabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            idKab = ((OptionItem) adapterView.getItemAtPosition(i)).getValue();

                            List<OptionItem> filteredKecamatan = new ArrayList<>();
                            for(OptionItem item: listKecamatan){
                                if(item.getAtt1().equals(idKab)) filteredKecamatan.add(item);
                            }

                            adapterKecamatan = new ArrayAdapter(context,android.R.layout.simple_list_item_1, filteredKecamatan);
                            spnKecamatan.setAdapter(adapterKecamatan);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    spnKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            idKec = ((OptionItem) adapterView.getItemAtPosition(i)).getValue();

                            List<OptionItem> filteredKelurahan = new ArrayList<>();
                            for(OptionItem item: listKelurahan){
                                if(item.getAtt1().equals(idKec)) filteredKelurahan.add(item);
                            }

                            adapterKelurahan = new ArrayAdapter(context,android.R.layout.simple_list_item_1, filteredKelurahan);
                            spnKelurahan.setAdapter(adapterKelurahan);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    getSegmentasi();

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDaerah();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDaerah();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    private void getSegmentasi(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getSegmentasi, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan, harap ulangi proses";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listSegmentasi = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        defaultSelectedSegment = 0;
                        for(int i = 0; i < ja.length(); i++){

                            JSONObject jo = ja.getJSONObject(i);
                            listSegmentasi.add(new OptionItem(
                                    jo.getString("id")
                                    ,jo.getString("nama")
                            ));
                            if (jo.getString("nama").toUpperCase().equals("BRONZE")) defaultSelectedSegment = i;
                        }

                        setSegmentasiAdapter();

                        if(isEdit){

                            getDetailReseller();
                        }else{

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
                            getSegmentasi();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getSegmentasi();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    private void setSegmentasiAdapter() {

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listSegmentasi);
        spnSegmentasi.setAdapter(adapter);
        spnSegmentasi.setSelection(defaultSelectedSegment);
    }

    private void getDetailReseller(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getResellerInfo, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan, harap ulangi proses";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        final JSONObject jo = response.getJSONObject("response");
                        txt_nama.setText(jo.getString("nama"));
                        txt_alamat.setText(jo.getString("alamat"));
                        txt_ktp.setText(jo.getString("no_ktp"));
                        txt_kelurahan.setText(jo.getString("kelurahan"));
                        txt_kecamatan.setText(jo.getString("kecamatan"));
                        txt_kota.setText(jo.getString("kota"));
                        txt_nomor.setText(jo.getString("notelp"));
                        txt_nomor.setText(jo.getString("notelp"));
                        txt_nomorhp.setText(jo.getString("nohp"));
                        txt_email.setText(jo.getString("email"));
                        txt_bank.setText(jo.getString("bank"));
                        txt_rekening.setText(jo.getString("norekening"));
                        txt_rekening.setText(jo.getString("norekening"));
                        String tempo = jo.getString("flag_tempo");
                        cbTempo.setChecked(tempo.toUpperCase().equals("Y"));
                        txt_tempo.setText(jo.getString("tempo"));
                        txt_cp.setText(jo.getString("contact_person"));
                        txt_digipos.setText(jo.getString("id_digipos"));
                        txt_kategori_outlet.setText(jo.getString("kategori_outlet"));
                        txt_jenis_outlet.setText(jo.getString("jenis_outlet"));
                        txt_limit_order_malam.setText(jo.getString("limit_malam"));
                        txt_area.setText(jo.getString("omo"));
                        String idSegmentasi = jo.getString("id_segmentasi");

                        defaultSelectedKelurahan = 0;
                        defaultSelectedKecamatan = 0;
                        defaultSelectedKabupaten = 0;
                        defaultSelectedKategori = 0;
                        defaultSelectedJenis = 0;

                        for(OptionItem item : listKabupaten){

                            if(item.getValue().equals(jo.getString("id_kabupaten"))){
                                spnKabupaten.setSelection(defaultSelectedKabupaten);
                                break;
                            }

                            defaultSelectedKabupaten ++;
                        }

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i< adapterKecamatan.getCount(); i ++){
                                    try {
                                        if(((OptionItem)adapterKecamatan.getItem(i)).getValue().equals(jo.getString("id_kecamatan"))){
                                            defaultSelectedKecamatan = i;
                                            spnKecamatan.setSelection(defaultSelectedKecamatan);
                                            break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(int i = 0; i< adapterKelurahan.getCount(); i ++){
                                            try {
                                                if(((OptionItem) adapterKelurahan.getItem(i)).getValue().equals(jo.getString("id_kelurahan"))){
                                                    defaultSelectedKelurahan = i;
                                                    spnKelurahan.setSelection(defaultSelectedKelurahan);
                                                    break;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }, 500);

                            }
                        }, 500);

                        int x = 0;
                        for(OptionItem item : listSegmentasi){

                            if(item.getValue().equals(idSegmentasi)){
                                spnSegmentasi.setSelection(x);
                                break;
                            }
                            x++;
                        }

                        for(OptionItem item : listKategoriOutlet){

                            if(item.getValue().equals(jo.getString("id_kategori_outlet"))){
                                spnKategoriOutlet.setSelection(defaultSelectedKategori);
                                break;
                            }

                            defaultSelectedKategori ++;
                        }

                        for(OptionItem item : listJenisOutlet){

                            if(item.getValue().equals(jo.getString("id_jenis_outlet"))){
                                spnJenisOutlet.setSelection(defaultSelectedJenis);
                                break;
                            }

                            defaultSelectedJenis ++;
                        }

                        String konsinyasi = jo.getString("konsinyasi");
                        cbKonsinyasi.setChecked(konsinyasi.toUpperCase().equals("Y"));
                        txt_limit_konsinyasi.setText(jo.getString("limit_konsinyasi"));

                        latitude = iv.parseNullDouble(jo.getString("lat"));
                        longitude = iv.parseNullDouble(jo.getString("long"));
                        location = new Location("set");
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);

                        String ktp = jo.getString("file_ktp");
                        ImageUtils iu = new ImageUtils();
                        iu.LoadRealImage(context, ktp, ivKtp);

                        setPointMap();

                        getCustomerImage();
                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDetailReseller();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDetailReseller();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogBox.dismissDialog();
    }

    private void getCustomerImage(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getCustomerImage, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan, harap ulangi proses";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listPhoto.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length();i++ ){

                            JSONObject jo = ja.getJSONObject(i);
                            listPhoto.add(new PhotoModel("", jo.getString("image"), ""));
                        }

                    }else{

                        //DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getCustomerImage();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

                adapterPhoto.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getCustomerImage();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final ScrollView layout_scroll = findViewById(R.id.layout_scroll);
        ((CustomMapView) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new CustomMapView.OnTouchListener() {
            @Override
            public void onTouch() {
                layout_scroll.requestDisallowInterceptTouchEvent(true);
            }
        });

        maps = googleMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Hasil dari QR Code Scanner

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
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

            Uri filePath = ImageUtils.getImageUri(getApplicationContext(), imageBitmap);

            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);

            /*Matrix matrix = new Matrix();
            try {

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                int columnIndex = returnCursor.getColumnIndex(filePathColumn[0]);
                String picturePath = returnCursor.getString(columnIndex);
                ExifInterface exif = new ExifInterface(picturePath);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = iv.exifToDegrees(rotation);
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            } catch (Exception e) {
                e.printStackTrace();
            }*/

            copyFileFromUri(context, filePath, namaFile, null);

        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getExtras().get("data") != null) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri filePath = ImageUtils.getImageUri(getApplicationContext(), photo);
            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, filePath, namaFile, null);

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Cursor returnCursor =
                    getApplication().getContentResolver().query(photoURI,
                            null, null, null, null);

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
        }else{
            super.onActivityResult(requestCode, resultCode, data);
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
            Bitmap bm2 = null;
            if(extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png") || extension.equals(".bmp")){

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                bm2 = BitmapFactory.decodeStream(inputStream);
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

                if(matrix != null){

                    bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);
                }

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

            if(isKtp){
                ImageUtils iu = new ImageUtils();
                File file = new File(filePathURI);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                int newWidth = 0;
                int newHeight = 0;

                if(imageHeight > imageWidth){

                    newWidth = 512;
                    newHeight = newWidth * imageHeight / imageWidth;
                }else{

                    newHeight = 512;
                    newWidth = newHeight * imageWidth / imageHeight;
                }

                fileKtp = ImageUtils.convert(bm2);
                iu.LoadRealImage(context, file, ivKtp, newWidth, newHeight);
            }else{

                if(bm2 != null){

                    listPhoto.add(new PhotoModel(filePathURI, "", ImageUtils.convert(bm2)));
                    adapterPhoto.notifyDataSetChanged();
                }
            }

            //new UploadFileToServer().execute();
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
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
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
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
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
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
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation && !isEdit){

            setPointMap();
        }
    }

    private void setPointMap(){

        if(maps != null){

            maps.clear();
            maps.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f)
                    .draggable(true)
                    .position(new LatLng(latitude, longitude)));

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Please allow location access from your app permission", Toast.LENGTH_SHORT).show();
                return;
            }

            //googleMap.setMyLocationEnabled(true);
            maps.getUiSettings().setZoomControlsEnabled(true);
            MapsInitializer.initialize(context);
            LatLng position = new LatLng(latitude, longitude);
            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(15).build();
            maps.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            updateKeterangan(position);

            maps.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    LatLng position = marker.getPosition();
                    updateKeterangan(position);
                    Log.d(TAG, "onMarkerDragEnd: " + position.latitude +" "+ position.longitude);
                }
            });

            maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    maps.clear();
                    maps.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .draggable(true)
                            .position(latLng));
                    updateKeterangan(latLng);
                    Log.d(TAG, "onMarkerDragEnd: " + latLng.latitude +" "+ latLng.longitude);
                }
            });
        }
    }

    private void updateKeterangan(LatLng position){

        latitude = position.latitude;
        longitude = position.longitude;

        //get address
        new Thread(new Runnable(){
            public void run(){
                address0 = getAddress(location);
            }
        }).start();

        /*edtLatitude.setText(iv.doubleToStringFull(latitude));
        edtLongitude.setText(iv.doubleToStringFull(longitude));
        edtState.setText(address0);*/
    }

    private String getAddress(Location location)
    {
        List<Address> addresses;
        try{
            addresses = new Geocoder(this,Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
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
