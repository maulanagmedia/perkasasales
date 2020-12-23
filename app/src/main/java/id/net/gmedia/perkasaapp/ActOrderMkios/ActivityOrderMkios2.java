package id.net.gmedia.perkasaapp.ActOrderMkios;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.ModelPulsa;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityOrderMkios2 extends AppCompatActivity implements LocationListener {

    private List<ModelPulsa> listPulsa = new ArrayList<>();
    private double totalHarga;
    private Context context;
    private SessionManager session;
    private String nomor = "", kodeCV = "";
    private TextView txt_nama;
    private static TextView txt_total;
    private DialogBox dialogBox;
    private static ItemValidation iv = new ItemValidation();
    private TextView tvNomor, tvSegmentasi;
    //private AdapterOrderMkios2 adapter;
    private boolean isLoading = false;
    private Button btnProses;
    private final String TAG = "ORDERMKIOS";
    public static final String flag = "MKIOS";

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
    private Button btnPeta;
    private TextView tvNama1, tvJml1, tvHarga1, tvTotal1;
    private TextView tvNama5, tvJml5, tvHarga5, tvTotal5;
    private TextView tvNama10, tvJml10, tvHarga10, tvTotal10;
    private TextView tvNama20, tvJml20, tvHarga20, tvTotal20;
    private TextView tvNama25, tvJml25, tvHarga25, tvTotal25;
    private TextView tvNama50, tvJml50, tvHarga50, tvTotal50;
    private TextView tvNama100, tvJml100, tvHarga100, tvTotal100;
    private TextView tvNamaBulk, tvJmlBulk, tvHargaBulk, tvTotalBulk;
    private EditText edtJml1, edtJml5, edtJml10, edtJml20, edtJml25, edtJml50, edtJml100, edtJmlBulk;
    private RadioGroup rgCrbayar;
    private RadioButton rbTunai, rbBank;
    private String crBayar = "T";
    private List<OptionItem> listAccount = new ArrayList<>();
    private ArrayAdapter adapterAccount;
    private Spinner spAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_mkios2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Mkios");
        }

        context = this;
        session = new SessionManager(context);
        dialogBox = new DialogBox(context);

        initLocationUtils();
        initUI();
        initData();
        //initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(ActivityOrderMkios2.this);
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

    private void initUI() {

        txt_nama = findViewById(R.id.txt_nama);
        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        txt_total = findViewById(R.id.txt_total);
        tvSegmentasi = (TextView) findViewById(R.id.tv_segmentasi);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);
        btnPeta = (Button) findViewById(R.id.btn_peta);

        tvNama1 = (TextView) findViewById(R.id.tv_nama1);
        tvNama5 = (TextView) findViewById(R.id.tv_nama5);
        tvNama10 = (TextView) findViewById(R.id.tv_nama10);
        tvNama20 = (TextView) findViewById(R.id.tv_nama20);
        tvNama25 = (TextView) findViewById(R.id.tv_nama25);
        tvNama50 = (TextView) findViewById(R.id.tv_nama50);
        tvNama100 = (TextView) findViewById(R.id.tv_nama100);
        tvNamaBulk = (TextView) findViewById(R.id.tv_namabulk);

        edtJml1 = (EditText) findViewById(R.id.edt_jml1);
        edtJml5 = (EditText) findViewById(R.id.edt_jml5);
        edtJml10 = (EditText) findViewById(R.id.edt_jml10);
        edtJml20 = (EditText) findViewById(R.id.edt_jml20);
        edtJml25 = (EditText) findViewById(R.id.edt_jml25);
        edtJml50 = (EditText) findViewById(R.id.edt_jml50);
        edtJml100 = (EditText) findViewById(R.id.edt_jml100);
        edtJmlBulk = (EditText) findViewById(R.id.edt_jmlbulk);

        tvHarga1 = (TextView) findViewById(R.id.tv_harga1);
        tvHarga5 = (TextView) findViewById(R.id.tv_harga5);
        tvHarga10 = (TextView) findViewById(R.id.tv_harga10);
        tvHarga20 = (TextView) findViewById(R.id.tv_harga20);
        tvHarga25 = (TextView) findViewById(R.id.tv_harga25);
        tvHarga50 = (TextView) findViewById(R.id.tv_harga50);
        tvHarga100 = (TextView) findViewById(R.id.tv_harga100);
        tvHargaBulk = (TextView) findViewById(R.id.tv_hargabulk);

        tvTotal1 = (TextView) findViewById(R.id.tv_total1);
        tvTotal5 = (TextView) findViewById(R.id.tv_total5);
        tvTotal10 = (TextView) findViewById(R.id.tv_total10);
        tvTotal20 = (TextView) findViewById(R.id.tv_total20);
        tvTotal25 = (TextView) findViewById(R.id.tv_total25);
        tvTotal50 = (TextView) findViewById(R.id.tv_total50);
        tvTotal100 = (TextView) findViewById(R.id.tv_total100);
        tvTotalBulk = (TextView) findViewById(R.id.tv_totalbulk);

        spAkun = (Spinner) findViewById(R.id.sp_akun);
        rgCrbayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        rbTunai = (RadioButton) findViewById(R.id.rb_tunai);
        rbBank = (RadioButton) findViewById(R.id.rb_bank);

        if(getIntent().hasExtra("nomor")){
            //ModelOutlet mkios = getIntent().getParcelableExtra("mkios");
            nomor = getIntent().getExtras().getString("nomor", "");
        }

        //Inisialisasi Recycler View Pulsa
        totalHarga = 0;
        isLoading = false;

        listPulsa = new ArrayList<>();
        /*adapter = new AdapterOrderMkios2(listPulsa, context);
        RecyclerView rcy_pulsa = findViewById(R.id.rcy_pulsa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcy_pulsa.setLayoutManager(layoutManager);
        rcy_pulsa.setItemAnimator(new DefaultItemAnimator());
        rcy_pulsa.setAdapter(adapter);*/

        adapterAccount = new ArrayAdapter(context, R.layout.layout_simple_list, listAccount);
        spAkun.setAdapter(adapterAccount);
        rgCrbayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i == R.id.rb_tunai){

                    crBayar = "T";
                }else if(i == R.id.rb_bank){

                    crBayar = "B";
                }

                getDataAccount();
            }
        });

        crBayar = "T";
        getDataAccount();
    }

    private void getDataAccount() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("flag", crBayar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDataAccount, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listAccount.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i++){

                            JSONObject jo = ja.getJSONObject(i);

                            listAccount.add(new OptionItem(
                                    jo.getString("kodeakun")
                                    ,jo.getString("namaakun")
                            ));
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
                            getDataAccount();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                    //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
                }

                adapterAccount.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataAccount();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
                //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {

        isLoading = true;
        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", nomor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDetailResellerMkios, new ApiVolley.VolleyCallback() {
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

                        JSONObject jo = response.getJSONObject("response");
                        txt_nama.setText(jo.getString("nama"));
                        nomor = jo.getString("nomor");
                        tvNomor.setText(nomor);
                        kodeCV = jo.getString("kode_cv");
                        tvSegmentasi.setText(jo.getString("segmentasi"));

                        // Denom
                        listPulsa.add(new ModelPulsa("1", jo.getString("jk1"), "", ""));
                        tvNama1.setText("1");
                        tvHarga1.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk1"))));
                        edtJml1.setText("");

                        listPulsa.add(new ModelPulsa("5", jo.getString("jk5"), "", ""));
                        tvNama5.setText("5");
                        tvHarga5.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk5"))));
                        edtJml5.setText("");

                        listPulsa.add(new ModelPulsa("10", jo.getString("jk10"), "", ""));
                        tvNama10.setText("10");
                        tvHarga10.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk10"))));
                        edtJml10.setText("");

                        listPulsa.add(new ModelPulsa("20", jo.getString("jk20"), "", ""));
                        tvNama20.setText("20");
                        tvHarga20.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk20"))));
                        edtJml20.setText("");

                        listPulsa.add(new ModelPulsa("25", jo.getString("jk25"), "", ""));
                        tvNama25.setText("25");
                        tvHarga25.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk25"))));
                        edtJml25.setText("");

                        listPulsa.add(new ModelPulsa("50", jo.getString("jk50"), "", ""));
                        tvNama50.setText("50");
                        tvHarga50.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk50"))));
                        edtJml50.setText("");

                        listPulsa.add(new ModelPulsa("100", jo.getString("jk100"), "", ""));
                        tvNama100.setText("100");
                        tvHarga100.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("jk100"))));
                        edtJml100.setText("");

                        listPulsa.add(new ModelPulsa("Bulk", jo.getString("jual_bulk"), "", ""));
                        tvNamaBulk.setText("Bulk");
                        tvHargaBulk.setText(iv.doubleToString(100 - iv.parseNullDouble(jo.getString("jual_bulk"))) + "%");
                        edtJmlBulk.setText("");

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

                //adapter.notifyDataSetChanged();
                /*adapter = new AdapterOrderMkios2(listPulsa, context);
                RecyclerView rcy_pulsa = findViewById(R.id.rcy_pulsa);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                rcy_pulsa.setLayoutManager(layoutManager);
                rcy_pulsa.setItemAnimator(new DefaultItemAnimator());
                rcy_pulsa.setAdapter(adapter);*/

                initEvent();
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

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi

                if(listPulsa.size()  < 0){

                    Toast.makeText(context, "Data denom belum termuat, harap tunggu", Toast.LENGTH_LONG).show();
                    return;
                }

                if(isLoading){
                    Toast.makeText(context, "Harap tunggu hingga proses pengambilan data selesai", Toast.LENGTH_LONG).show();
                    return;
                }

                double totalJumlah = 0;
                for(ModelPulsa pulsa : listPulsa){

                    if(!pulsa.getJumlah().equals("")){

                        totalJumlah += iv.parseNullDouble(pulsa.getJumlah());
                    }
                }

                if(totalJumlah <= 0){
                    Toast.makeText(context, "Harap pilih minimal satu denom", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin melakukan order MKIOS dengan total " + iv.ChangeToCurrencyFormat(iv.doubleToString(totalHarga)) +" ?")
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

        edtJml1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml25.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJml100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJmlBulk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
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

    private void getLokasiReseller() {

        isLoading = true;
        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", nomor);
            jBody.put("keyword", "");
            jBody.put("start", "");
            jBody.put("count", "");
            jBody.put("kdcus", "");
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

    private void saveData() {

        btnProses.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", nomor);

            for(ModelPulsa pulsa : listPulsa){

                if(pulsa.getPulsaString().equals("Bulk")){

                    jBody.put("hargabulk",pulsa.getTotalHarga());
                    jBody.put("value_bulk",pulsa.getJumlah());
                }else{
                    jBody.put("S"+pulsa.getPulsaString(),pulsa.getJumlah());
                    jBody.put("hargaS"+pulsa.getPulsaString(),pulsa.getHargaString());
                }

            }

            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));

            String imei = "";
            ArrayList<String> imeis = iv.getIMEI(context);
            if(imeis != null) if(imeis.size() > 0) imei = imeis.get(0);
            jBody.put("imei", imei);
            jBody.put("crbayar", crBayar);
            jBody.put("kodeakun", ((OptionItem)spAkun.getSelectedItem()).getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveTransaksiMkios, new ApiVolley.VolleyCallback() {
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

    public void updateHarga(){

        totalHarga = 0;

        double jml1 = iv.parseNullDouble(edtJml1.getText().toString());
        double jml5 = iv.parseNullDouble(edtJml5.getText().toString());
        double jml10 = iv.parseNullDouble(edtJml10.getText().toString());
        double jml20 = iv.parseNullDouble(edtJml20.getText().toString());
        double jml25 = iv.parseNullDouble(edtJml25.getText().toString());
        double jml50 = iv.parseNullDouble(edtJml50.getText().toString());
        double jml100 = iv.parseNullDouble(edtJml100.getText().toString());
        double jmlBulk = iv.parseNullDouble(edtJmlBulk.getText().toString());

        double harga1 = iv.parseNullDouble(listPulsa.get(0).getHargaString());
        double harga5 = iv.parseNullDouble(listPulsa.get(1).getHargaString());
        double harga10 = iv.parseNullDouble(listPulsa.get(2).getHargaString());
        double harga20 = iv.parseNullDouble(listPulsa.get(3).getHargaString());
        double harga25 = iv.parseNullDouble(listPulsa.get(4).getHargaString());
        double harga50 = iv.parseNullDouble(listPulsa.get(5).getHargaString());
        double harga100 = iv.parseNullDouble(listPulsa.get(6).getHargaString());
        double hargaBulk = iv.parseNullDouble(listPulsa.get(7).getHargaString());

        listPulsa.get(0).setJumlah(edtJml1.getText().toString());
        listPulsa.get(1).setJumlah(edtJml5.getText().toString());
        listPulsa.get(2).setJumlah(edtJml10.getText().toString());
        listPulsa.get(3).setJumlah(edtJml20.getText().toString());
        listPulsa.get(4).setJumlah(edtJml25.getText().toString());
        listPulsa.get(5).setJumlah(edtJml50.getText().toString());
        listPulsa.get(6).setJumlah(edtJml100.getText().toString());
        listPulsa.get(7).setJumlah(edtJmlBulk.getText().toString());

        double total1 = jml1 * harga1;
        double total5 = jml5 * harga5;
        double total10 = jml10 * harga10;
        double total20 = jml20 * harga20;
        double total25 = jml25 * harga25;
        double total50 = jml50 * harga50;
        double total100 = jml100 * harga100;
        double totalBulk = (100 - hargaBulk) / 100 * jmlBulk;

        listPulsa.get(0).setTotalHarga(iv.doubleToString(total1));
        listPulsa.get(1).setTotalHarga(iv.doubleToString(total5));
        listPulsa.get(2).setTotalHarga(iv.doubleToString(total10));
        listPulsa.get(3).setTotalHarga(iv.doubleToString(total20));
        listPulsa.get(4).setTotalHarga(iv.doubleToString(total25));
        listPulsa.get(5).setTotalHarga(iv.doubleToString(total50));
        listPulsa.get(6).setTotalHarga(iv.doubleToString(total100));
        listPulsa.get(7).setTotalHarga(iv.doubleToString(totalBulk));

        tvTotal1.setText(iv.ChangeToCurrencyFormat(listPulsa.get(0).getTotalHarga()));
        tvTotal5.setText(iv.ChangeToCurrencyFormat(listPulsa.get(1).getTotalHarga()));
        tvTotal10.setText(iv.ChangeToCurrencyFormat(listPulsa.get(2).getTotalHarga()));
        tvTotal20.setText(iv.ChangeToCurrencyFormat(listPulsa.get(3).getTotalHarga()));
        tvTotal25.setText(iv.ChangeToCurrencyFormat(listPulsa.get(4).getTotalHarga()));
        tvTotal50.setText(iv.ChangeToCurrencyFormat(listPulsa.get(5).getTotalHarga()));
        tvTotal100.setText(iv.ChangeToCurrencyFormat(listPulsa.get(6).getTotalHarga()));
        tvTotalBulk.setText(iv.ChangeToCurrencyFormat(listPulsa.get(7).getTotalHarga()));

        totalHarga = total1 + total5 + total10 + total20 + total25 + total50 + total100 + totalBulk;

        //adapter.notifyDataSetChanged();
        txt_total.setText(iv.ChangeToRupiahFormat(totalHarga));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                        if (ActivityCompat.checkSelfPermission(ActivityOrderMkios2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityOrderMkios2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(ActivityOrderMkios2.this, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult(ActivityOrderMkios2.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ActivityOrderMkios2.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(ActivityOrderMkios2.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(ActivityOrderMkios2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityOrderMkios2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityOrderMkios2.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityOrderMkios2.this,
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
        ActivityCompat.requestPermissions(ActivityOrderMkios2.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation/* && !editMode*/){
            if(!isLoading) getJarak();
        }
    }

    private void getJarak() {

        isLoading = true;
        //dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", nomor);
            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            jBody.put("kdcus", "");
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
}
