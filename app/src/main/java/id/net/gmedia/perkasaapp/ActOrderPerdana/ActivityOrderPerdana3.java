package id.net.gmedia.perkasaapp.ActOrderPerdana;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter.AdapterOrderPerdanaCcid;
import id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter.AdapterOrderPerdanaCcidRentang;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.MapsResellerActivity;
import id.net.gmedia.perkasaapp.ModelCcid;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.MocLocChecker;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityOrderPerdana3 extends AppCompatActivity implements LocationListener {

    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();

    //List semua Ccid yang bisa dipilih
    private List<ModelCcid> allCcid = new ArrayList<>();
    //List Ccid yang sudah dipilih atau akan dibeli
    private List<ModelCcid> selectedCcid = new ArrayList<>();

    //UI yang menampilkan CCID
    private TextView txt_total, txt_total_harga, txt_no_ccid, txt_nama_ccid, txt_harga_ccid;

    //UI Checkbox
    private LinearLayout layout_checkbox;
    private CheckBox all_checkbox;
    private List<CheckBox> checkBoxes = new ArrayList<>();

    //Popup dialog pembelian
    private Dialog dialog_list, dialog_rentang;

    private AdapterOrderPerdanaCcid adapter;

    private TextView txt_nama, txt_harga;
    private Button btn_list, btn_rentang, btn_scan;
    private String kdbrg = "", namabrg = "", hargabrg = "", suratJalan = "";
    private LinearLayout llTanggal;
    private EditText edtTanggal;
    private List<ModelCcid> selectedCcidRentang;
    private AdapterOrderPerdanaCcidRentang adapterRentang;
    private EditText txt_banyak_ccid;
    private Button btnProses;
    private String kdcus = "";

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
    private String TAG = "PERDANA";
    private SessionManager session;
    private RadioButton rbSegel, rbEvent;
    private RadioGroup rgJenisBarang;
    public static final String flag = "PERDANA";
    private TextView tvJarak;
    private ImageView ivRefreshJarak;
    private boolean isLoading = false;
    private Button btnPeta;
    private String tanggalTempo = "", resellerTempo = "", currentCrbayar = "";
    private RadioGroup rgJenisTransaksi;
    private LinearLayout llAkun;
    private List<OptionItem> listAccount = new ArrayList<>();
    private ArrayAdapter adapterAccount;
    private Spinner spAkun;
    private RadioGroup rgCrbayar;
    private RadioButton rbTunai, rbBank;
    private String crBayar = "T";
    private TextView tvTanggalTempo;
    private int stateTempo = 1;
    private RadioButton rbTempo;
    private String jenisBarang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_perdana3);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Perdana");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);
        allCcid = new ArrayList<>();
        selectedCcid = new ArrayList<>();
        checkBoxes = new ArrayList<>();

        initLocationUtils();
        initUI();
        initEvent();
        //Mengisi allCcid yang berisi list CCID yang bisa dibeli
        initCcid(1, "", "", "");
        initDataReseller();

        //Inisialisasi dialog list CCID
        dialog_list = new Dialog(ActivityOrderPerdana3.this, R.style.PopupTheme);
        dialog_list.setContentView(R.layout.dialog_order_perdana_list);
        Window window = dialog_list.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layout_checkbox = dialog_list.findViewById(R.id.layout_checkbox);

        final EditText edtSearch = dialog_list.findViewById(R.id.edt_search);
        final TextView btn_simpan_list = dialog_list.findViewById(R.id.btn_simpan);
        final TextView btn_batal_list = dialog_list.findViewById(R.id.btn_batal);
        btn_batal_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_list.dismiss();
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if(id == EditorInfo.IME_ACTION_SEARCH){

                    checkBoxes.clear();
                    layout_checkbox.removeAllViews();

                    String keyword = edtSearch.getText().toString();

                    //Menginisialisasi Checkbox CCID
                    all_checkbox = addCheckBox("All", 0);
                    all_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(all_checkbox.isChecked()){
                                if(checkBoxes.size() > 0){
                                    for(CheckBox c : checkBoxes){
                                        c.setChecked(true);
                                    }
                                }
                            }
                            else{
                                if(checkBoxes.size() > 0){
                                    for(CheckBox c : checkBoxes){

                                        boolean isExist = false;
                                        for(ModelCcid ccid: selectedCcid){
                                            if(c.getText().equals(ccid.getCcid())){

                                                isExist = true;
                                                break;
                                            }
                                        }

                                        c.setChecked(isExist);
                                    }
                                }
                            }
                        }
                    });

                    for(int i = 0; i < allCcid.size(); i++) {

                        if(allCcid.get(i).getCcid().contains(keyword)){

                            final CheckBox c = addCheckBox(allCcid.get(i).getCcid(), i + 1);
                            c.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!c.isChecked()){
                                        all_checkbox.setChecked(false);
                                    }
                                }
                            });

                            boolean isExist = false;
                            for(ModelCcid ccid: selectedCcid){
                                if(c.getText().equals(ccid.getCcid())){

                                    isExist = true;
                                    break;
                                }
                            }

                            c.setChecked(isExist);

                            checkBoxes.add(c);
                        }
                    }

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        btn_simpan_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i < checkBoxes.size(); i++){

                    if(checkBoxes.get(i).isChecked()){

                        boolean isExist = false;
                        for(ModelCcid ccid: selectedCcid){

                            if(ccid.getCcid().equals(checkBoxes.get(i).getText())){
                                isExist = true;
                                break;
                            }
                        }

                        if(!isExist) selectedCcid.add(allCcid.get(i));
                    }else{

                        boolean isExist = false;
                        int j = 0;
                        for(ModelCcid ccid: selectedCcid){

                            if(ccid.getCcid().equals(checkBoxes.get(i).getText())){
                                isExist = true;
                                break;
                            }
                            j++;
                        }

                        if(isExist) selectedCcid.remove(j);
                    }
                }

                updateCcid();
                dialog_list.dismiss();
            }
        });

        // Selected CCID
        RecyclerView rcy_ccid = findViewById(R.id.rcy_ccid);
        adapter = new AdapterOrderPerdanaCcid(ActivityOrderPerdana3.this, selectedCcid, txt_no_ccid, txt_nama_ccid, txt_harga_ccid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        rcy_ccid.setLayoutManager(layoutManager);
        rcy_ccid.setItemAnimator(new DefaultItemAnimator());
        rcy_ccid.setAdapter(adapter);

        // ambil dari list
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menginisialisasi semua CCID yang bisa dipilih pada dialog
                checkBoxes.clear();
                layout_checkbox.removeAllViews();

                //Menginisialisasi Checkbox CCID
                all_checkbox = addCheckBox("All", 0);
                all_checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(all_checkbox.isChecked()){
                            if(checkBoxes.size() > 0){
                                for(CheckBox c : checkBoxes){
                                    c.setChecked(true);
                                }
                            }
                        }
                        else{
                            if(checkBoxes.size() > 0){
                                for(CheckBox c : checkBoxes){

                                    boolean isExist = false;
                                    for(ModelCcid ccid: selectedCcid){
                                        if(c.getText().equals(ccid.getCcid())){

                                            isExist = true;
                                            break;
                                        }
                                    }

                                    c.setChecked(isExist);
                                }
                            }
                        }
                    }
                });

                for(int i = 0; i < allCcid.size(); i++) {
                    final CheckBox c = addCheckBox(allCcid.get(i).getCcid(), i + 1);
                    c.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!c.isChecked()){
                                all_checkbox.setChecked(false);
                            }
                        }
                    });

                    boolean isExist = false;
                    for(ModelCcid ccid: selectedCcid){
                        if(c.getText().equals(ccid.getCcid())){

                            isExist = true;
                            break;
                        }
                    }

                    c.setChecked(isExist);

                    checkBoxes.add(c);
                }

                dialog_list.show();
            }
        });

        // Ambil Dari rentang
        btn_rentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogRentangCCID();
            }
        });

        // Scan CCID
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ActivityOrderPerdana3.this);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MocLocChecker checker = new MocLocChecker(ActivityOrderPerdana3.this);
    }

    private void initDataReseller() {

        isLoading = true;
        //dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getResellerInfo, new ApiVolley.VolleyCallback() {
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
                        resellerTempo = jo.getString("tempo");
                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initDataReseller();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                //dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initDataReseller();
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

    private void showDialogRentangCCID() {

        // Ambil dengan Rentang
        dialog_rentang = new Dialog(ActivityOrderPerdana3.this, R.style.PopupTheme);
        dialog_rentang.setContentView(R.layout.dialog_order_perdana_rentang);
        Window window = dialog_rentang.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView txt_ccid_awal = dialog_rentang.findViewById(R.id.txt_ccid_awal);
        final TextView txt_ccid_akhir = dialog_rentang.findViewById(R.id.txt_ccid_akhir);
        txt_banyak_ccid = dialog_rentang.findViewById(R.id.txt_banyak_ccid);
        final TextView btn_simpan_rentang = dialog_rentang.findViewById(R.id.btn_simpan);
        final TextView btn_batal_rentang = dialog_rentang.findViewById(R.id.btn_batal);
        final Button btn_ambil_ccid = dialog_rentang.findViewById(R.id.btn_ambil_ccid);
        final RecyclerView rcy_ccid_rentang = dialog_rentang.findViewById(R.id.rcy_ccid);
        selectedCcidRentang = new ArrayList<>();
        adapterRentang = new AdapterOrderPerdanaCcidRentang(selectedCcidRentang);
        RecyclerView.LayoutManager layoutManagerRentang = new LinearLayoutManager(ActivityOrderPerdana3.this);
        rcy_ccid_rentang.setLayoutManager(layoutManagerRentang);
        rcy_ccid_rentang.setItemAnimator(new DefaultItemAnimator());
        rcy_ccid_rentang.setAdapter(adapterRentang);

        btn_ambil_ccid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ccid_awal = txt_ccid_awal.getText().toString();
                String ccid_akhir = txt_ccid_akhir.getText().toString();

                if(ccid_awal.isEmpty()){
                    txt_ccid_awal.setError("Harap diisi");
                    txt_ccid_awal.requestFocus();
                    return;
                }else{
                    txt_ccid_awal.setError(null);
                }

                if(ccid_akhir.isEmpty()){
                    txt_ccid_akhir.setError("Harap diisi");
                    txt_ccid_akhir.requestFocus();
                    return;
                }else{
                    txt_ccid_akhir.setError(null);
                }

                initCcid(2,"", ccid_awal, ccid_akhir);
            }
        });

        btn_batal_rentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_rentang.dismiss();
            }
        });

        btn_simpan_rentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(ModelCcid ccid : selectedCcidRentang){

                    boolean isExist = false;
                    for(ModelCcid selCCID : selectedCcid){

                        if(selCCID.getCcid().equals(ccid.getCcid())){
                            isExist = true;
                            break;
                        }
                    }

                    if(!isExist){
                        selectedCcid.add(ccid);
                    }
                }

                updateCcid();
                dialog_rentang.dismiss();
            }
        });

        selectedCcidRentang.clear();
        adapterRentang.notifyDataSetChanged();

        dialog_rentang.show();
    }

    private void initUI() {

        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_no_ccid = findViewById(R.id.txt_no_ccid);
        txt_nama_ccid = findViewById(R.id.txt_nama_ccid);
        txt_harga_ccid = findViewById(R.id.txt_harga_ccid);
        txt_total = findViewById(R.id.txt_total);
        txt_total_harga = findViewById(R.id.txt_total_harga);
        tvTanggalTempo = (TextView) findViewById(R.id.textView62);
        llTanggal = (LinearLayout) findViewById(R.id.ll_tanggal);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        btnPeta = (Button) findViewById(R.id.btn_peta);
        rgJenisTransaksi = (RadioGroup) findViewById(R.id.rg_jenis_transaksi);
        rgCrbayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        rbTunai = (RadioButton) findViewById(R.id.rb_tunai);
        rbBank = (RadioButton) findViewById(R.id.rb_bank);
        rbTempo = (RadioButton) findViewById(R.id.rb_tempo);

        btn_list = findViewById(R.id.btn_list);
        btn_rentang = findViewById(R.id.btn_rentang);
        btn_scan = findViewById(R.id.btn_scan);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJarak = (TextView) findViewById(R.id.tv_jarak);
        ivRefreshJarak = (ImageView) findViewById(R.id.iv_refresh_jarak);

        rbSegel = (RadioButton) findViewById(R.id.rb_segel);
        rbEvent = (RadioButton) findViewById(R.id.rb_event);

        rgJenisBarang = (RadioGroup) findViewById(R.id.rg_jenis_barang);
        llAkun = (LinearLayout) findViewById(R.id.ll_akun);
        spAkun = (Spinner) findViewById(R.id.sp_akun);

        isLoading = false;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdbrg = bundle.getString("kdbrg", "");
            namabrg = bundle.getString("namabrg", "");
            hargabrg = bundle.getString("harga", "");
            suratJalan = bundle.getString("suratjalan", "");
            kdcus = bundle.getString("kdcus", "");
            jenisBarang = bundle.getString("jenis_barang", "");

            txt_nama.setText(namabrg);
            txt_harga.setText(iv.ChangeToRupiahFormat(hargabrg));
        }
    }

    private void initEvent() {

        tanggalTempo = iv.getCurrentDate(FormatItem.formatDate);
        edtTanggal.setText(iv.getCurrentDate(FormatItem.formatDateDisplay));
        currentCrbayar = "T";

        /*llTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);
                Date dateValue = null;
                final Calendar customDate;

                try {
                    dateValue = sdf.parse(iv.ChangeFormatDateString(tanggalTempo, FormatItem.formatDate, FormatItem.formatDateDisplay));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        tanggalTempo = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), FormatItem.formatDateDisplay, FormatItem.formatDate);
                        edtTanggal.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });*/

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(suratJalan.isEmpty()){

                    Toast.makeText(context, "Tidak ada surat jalan, mohon cek data anda", Toast.LENGTH_LONG).show();
                    return;
                }

                if(selectedCcid.size() <= 0){

                    Toast.makeText(context, "Silahkan pilih minimal satu ccid", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin melakukan penjualan "+String.valueOf(selectedCcid.size())+ " item dengan harga " + txt_total_harga.getText().toString() +" ?")
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

        rgJenisTransaksi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(radioGroup.getCheckedRadioButtonId() == R.id.rb_tunai){

                    currentCrbayar = "T";
                    tanggalTempo = iv.sumDate(tanggalTempo, iv.parseNullInteger(resellerTempo),FormatItem.formatDate);
                    edtTanggal.setText(iv.ChangeFormatDateString(tanggalTempo, FormatItem.formatDate, FormatItem.formatDateDisplay));
                    tvTanggalTempo.setVisibility(View.GONE);
                    llTanggal.setVisibility(View.GONE);

                }else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_tempo){

                    currentCrbayar = "K";
                    stateTempo = 2;
                    getDataTempo();
                    tvTanggalTempo.setVisibility(View.VISIBLE);
                    llTanggal.setVisibility(View.VISIBLE);
                }else{

                    currentCrbayar = "B";
                    tanggalTempo = iv.getCurrentDate(FormatItem.formatDate);
                    edtTanggal.setText(iv.ChangeFormatDateString(tanggalTempo, FormatItem.formatDate, FormatItem.formatDateDisplay));
                    tvTanggalTempo.setVisibility(View.GONE);
                    llTanggal.setVisibility(View.GONE);
                }

            }
        });

        adapterAccount = new ArrayAdapter(context, R.layout.layout_simple_list, listAccount);
        spAkun.setAdapter(adapterAccount);
        rgCrbayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i == R.id.rb_tunai_bayar){

                    crBayar = "T";
                }else if(i == R.id.rb_bank){

                    crBayar = "B";
                }

                getDataAccount();
            }
        });

        crBayar = "T";
        getDataAccount();

        stateTempo = 1;
        getDataTempo();
    }

    private void getDataTempo() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
            jBody.put("nomor", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getTanggalTempo, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        if(stateTempo == 1){

                            String flagTempo = response.getJSONObject("response").getString("flag_tempo");

                            if(flagTempo.trim().toUpperCase().equals("N")){

                                rbTempo.setVisibility(View.GONE);
                            }else{

                                rbTempo.setVisibility(View.VISIBLE);
                            }
                        }else{

                            String tempo = response.getJSONObject("response").getString("tempo");
                            tanggalTempo = iv.sumDate(tanggalTempo, iv.parseNullInteger(tempo),FormatItem.formatDate);
                            edtTanggal.setText(iv.ChangeFormatDateString(tanggalTempo, FormatItem.formatDate, FormatItem.formatDateDisplay));
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
                            getDataTempo();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                    //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataTempo();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
                //Toast.makeText(context,"Terjadi kesalahan saat menghitung jarak, harap ulangi proses" , Toast.LENGTH_LONG).show();
            }
        });
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

    private void saveData() {

        btnProses.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONArray jualD = new JSONArray();

        for(ModelCcid ccid: selectedCcid){

            JSONObject joJualD = new JSONObject();
            try {
                joJualD.put("kodebrg", kdbrg);
                joJualD.put("ccid", ccid.getCcid());
                joJualD.put("harga", ccid.getHarga());
                joJualD.put("jumlah", "1");
                joJualD.put("nopengeluaran", suratJalan);
                jualD.put(joJualD);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RadioButton selectedRadio = (RadioButton) findViewById(rgJenisBarang.getCheckedRadioButtonId());
        //String jenisBarang = selectedRadio.getText().toString();

        JSONObject jualH = new JSONObject();
        try {
            jualH.put("tgl", iv.getCurrentDate(FormatItem.formatDate));
            jualH.put("kdcus", kdcus);
            jualH.put("tgltempo", iv.ChangeFormatDateString(edtTanggal.getText().toString(), FormatItem.formatDateDisplay, FormatItem.formatDate));
            jualH.put("nik", session.getNikGa());
            jualH.put("total", txt_total_harga.getText().toString().replaceAll("[,.]", ""));
            jualH.put("userid", session.getUsername());
            jualH.put("useru", session.getUsername());
            jualH.put("usertgl", iv.getCurrentDate(FormatItem.formatTimestamp));
            jualH.put("status", "1");
            jualH.put("nomutasi", suratJalan);
            jualH.put("crbayar", currentCrbayar);
            jualH.put("jenis_barang", jenisBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("jual_d", jualD);
            jBody.put("jual_h", jualH);
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

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveTransaksiPerdana, new ApiVolley.VolleyCallback() {
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

    private CheckBox addCheckBox(String text, int id){

        //Fungsi untuk Menambah item CheckBox baru ke Dialog
        CheckBox cb = new CheckBox(ActivityOrderPerdana3.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int padding5 = (int) getResources().getDimension(R.dimen.padding5);
        params.setMargins(padding5, padding5, padding5, padding5);
        cb.setText(text);
        if (Build.VERSION.SDK_INT < 21) {
            CompoundButtonCompat.setButtonTintList(cb, ColorStateList.valueOf(Color.WHITE));
        } else {
            cb.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
        }
        cb.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text14));
        cb.setTypeface(Typeface.DEFAULT_BOLD);
        cb.setTextColor(Color.WHITE);

        cb.setId(id);
        layout_checkbox.addView(cb, params);
        return cb;
    }

    private void initCcid(final int flag, final String ccid, final String start, final String end){

        /*allCcid.add(new ModelCcid(
                "3289732497298423"
                , "3001"
                , "20000"
                , false));

        allCcid.add(new ModelCcid(
                "3289732497298424"
                , "3001"
                , "20000"
                , false));

        allCcid.add(new ModelCcid(
                "3289732497298425"
                , "3001"
                , "20000"
                , false));

        allCcid.add(new ModelCcid(
                "3289732497298426"
                , "3001"
                , "20000"
                , false));

        allCcid.add(new ModelCcid(
                "3289732497298427"
                , "3001"
                , "20000"
                , false));*/
        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {

            jBody.put("kdbrg", kdbrg);
            jBody.put("kdcus", kdcus);
            jBody.put("ccid", ccid);
            jBody.put("ccid_awal", start);
            jBody.put("ccid_akhir", end);
            jBody.put("nobukti", suratJalan);
            jBody.put("keyword", "");
            jBody.put("start", "");
            jBody.put("count", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getListCCID, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, harap ulangi proses";
                switch (flag){
                    case 1:
                        allCcid.clear();
                        break;
                    case 2:
                        selectedCcidRentang.clear();
                        adapterRentang.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");

                        if(flag == 2){
                            txt_banyak_ccid.setText(iv.ChangeToCurrencyFormat(String.valueOf(jsonArray.length())));
                        }

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            if(flag == 1){

                                allCcid.add(new ModelCcid(
                                        jo.getString("ccid")
                                        , jo.getString("namabrg")
                                        , jo.getString("harga")
                                        , false
                                ));
                            }else if(flag == 2){

                                selectedCcidRentang.add(new ModelCcid(
                                        jo.getString("ccid")
                                        , jo.getString("namabrg")
                                        , jo.getString("harga")
                                        , false
                                ));
                            }else if(flag == 3){

                                selectedCcid.add(new ModelCcid(
                                        jo.getString("ccid")
                                        , jo.getString("namabrg")
                                        , jo.getString("harga")
                                        , false
                                ));
                                updateCcid();
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
                            initCcid(flag, ccid, start, end);
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

                if(flag == 1){
                    adapter.notifyDataSetChanged();
                }else if(flag == 2){
                    adapterRentang.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initCcid(flag, ccid, start, end);
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
            }
        });
    }

    public void updateCcid(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
            jBody.put("kodebrg", kdbrg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getHargaMarkup, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        String hargaMarkup = response.getJSONObject("response").getString("harga");
                        String maks = response.getJSONObject("response").getString("maks");
                        for(ModelCcid c : selectedCcid){
                            c.setHarga(hargaMarkup);
                            //total += iv.parseNullDouble(c.getHarga());
                        }

                        if(selectedCcid.size() > iv.parseNullLong(maks)){

                            Toast.makeText(context, "Maaf, barang ini hanya diperbolehkan maksimal " +maks, Toast.LENGTH_LONG).show();

                            for(int i = selectedCcid.size() - 1; i >= iv.parseNullInteger(maks) ;i--){

                                selectedCcid.remove(i);
                            }

                        }
                    }

                    //Mengupdate tampilan informasi berdasarkan CCID yang dipilih
                    //Update RecyclerView
                    //System.out.println(this.selectedCcid.size());
                    adapter.notifyDataSetChanged();

                    //Update total
                    txt_total.setText(String.valueOf(selectedCcid.size()));
                    double total = 0;
                    for(ModelCcid c : selectedCcid){
                        total += iv.parseNullDouble(c.getHarga());
                    }
                    txt_total_harga.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(total)));

                    //Update CCID ditampilkan
                    txt_no_ccid.setText("-");
                    txt_nama_ccid.setText("-");
                    txt_harga_ccid.setText("-");

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            updateCcid();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        updateCcid();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });

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

        }else if(result != null) {

            if(result.getContents() != null){
                //System.out.println(result.getContents());

                //Menambahkan data CCID ke list
                if(result.getContents().length() >= 21) initCcid(3, result.getContents().substring(5,21),"", "");
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                        if (ActivityCompat.checkSelfPermission(ActivityOrderPerdana3.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityOrderPerdana3.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(ActivityOrderPerdana3.this, new OnSuccessListener<Location>() {
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
                                    rae.startResolutionForResult(ActivityOrderPerdana3.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ActivityOrderPerdana3.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(ActivityOrderPerdana3.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(ActivityOrderPerdana3.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityOrderPerdana3.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityOrderPerdana3.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityOrderPerdana3.this,
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
        ActivityCompat.requestPermissions(ActivityOrderPerdana3.this,
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
