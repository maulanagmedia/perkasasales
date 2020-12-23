package id.net.gmedia.perkasaapp.ActSetoran;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.perkasaapp.ActRiwayatPenjualan.ActivityListSales;
import id.net.gmedia.perkasaapp.ActSetoran.Adapter.ListSetoranSalesAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ListSetoran extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private DialogBox dialogBox;
    private int start_day, start_month, start_year, end_day, end_month, end_year;
    private TextView txt_start, txt_end;
    private LinearLayout btn_kalender_start, btn_kalender_end;
    private ImageView btn_confirm;
    private List<CustomItem> listSetoran = new ArrayList<>();
    private ListSetoranSalesAdapter adapterRiwayat;
    private EditText edtCrBayar;
    private ListView lvSetoran;
    private String keyword = "";
    private TextView tvTotal;
    private LinearLayout llCustom;
    private Button btnPilihSales;
    private TextView tvSales;
    private String selectedNikGa = "", selectedNikMkios = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_setoran);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Setoran");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        //Inisialisasi UI Spinner
        /*String[] arraySpinner = new String[] {
                "Gmedia Test", "Andi Kusworo"
        };
        Spinner s = findViewById(R.id.spn_sales);
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.item_riwayat_penjualan_spinner, arraySpinner);
        s.setAdapter(adapter);*/

        //Inisialisasi UI Kalender

        llCustom = (LinearLayout) findViewById(R.id.ll_custom);
        btnPilihSales = (Button) findViewById(R.id.btn_pilih_sales);
        tvSales = (TextView) findViewById(R.id.tv_sales);

        tvSales.setText(session.getNama());
        if(session.isSuperuser()){
            llCustom.setVisibility(View.VISIBLE);
        }else{
            llCustom.setVisibility(View.GONE);
        }

        txt_start = findViewById(R.id.txt_start);
        txt_end = findViewById(R.id.txt_end);
        keyword = "";

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_kalender_start = findViewById(R.id.btn_kalender_start);
        btn_kalender_end = findViewById(R.id.btn_kalender_end);
        lvSetoran = (ListView) findViewById(R.id.lv_setoran);
        edtCrBayar = (EditText) findViewById(R.id.edt_crbayar);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        Calendar now = Calendar.getInstance();
        start_day = end_day = now.get(Calendar.DATE);
        start_month = end_month = now.get(Calendar.MONTH);
        start_year = end_year = now.get(Calendar.YEAR);
        txt_start.setText(iv.getCurrentDate(FormatItem.formatDateDisplay));
        txt_end.setText(iv.getCurrentDate(FormatItem.formatDateDisplay));

        listSetoran = new ArrayList<>();
        adapterRiwayat = new ListSetoranSalesAdapter(context, listSetoran);
        lvSetoran.setAdapter(adapterRiwayat);

        if(session.isSuperuser()){
            llCustom.setVisibility(View.VISIBLE);
        }else{
            llCustom.setVisibility(View.GONE);
        }
    }

    private void initEvent() {

        btn_kalender_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar customDate;
                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        txt_start.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
            }
        });

        btn_kalender_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar customDate;
                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        txt_end.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = false;
                if(end_year == start_year){
                    if(end_month == start_month){
                        if(end_day >= start_day){
                            valid = true;
                        }
                    }
                    else if(end_month > start_month){
                        valid = true;
                    }
                }
                else if(end_year > start_year){
                    valid = true;
                }

                if(!valid){
                    Toast.makeText(context, "Tanggal mulai tidak boleh melebihi tanggal akhir", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(ActivityRiwayatPenjualan.this, "Tanggal valid", Toast.LENGTH_SHORT).show();

                    //Tunjukkan histori penjualan
                    //Update total
                    initData();
                }
            }
        });

        edtCrBayar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtCrBayar.getText().toString();
                    listSetoran.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        edtCrBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                keyword = editable.toString();
            }
        });

        btnPilihSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ActivityListSales.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                selectedNikGa = data.getStringExtra("nik_ga");
                selectedNikMkios = data.getStringExtra("nik_mkios");
                String nama = data.getStringExtra("nama");
                tvSales.setText(nama);
                listSetoran.clear();
                initData();
            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("tgl_awal", iv.ChangeFormatDateString(txt_start.getText().toString(), FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("tgl_akhir", iv.ChangeFormatDateString(txt_end.getText().toString(), FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("keyword", keyword);
            jBody.put("nik", selectedNikGa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPembayaranSetoran, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                double totalAll = 0;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listSetoran.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        String lastTgl = "";
                        double totalPerNama = 0;

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);

                            //header
                            String tgl = iv.ChangeFormatDateString(jo.getString("tgl"), FormatItem.formatDate, FormatItem.formatDateDisplay);
                            if(!lastTgl.equals(tgl)){

                                lastTgl = tgl;
                                listSetoran.add(new CustomItem("H", tgl));

                                totalPerNama = 0;
                            }

                            if(i < jsonArray.length() - 1){

                                JSONObject jo2 = jsonArray.getJSONObject(i+1);
                                String newTgl = iv.ChangeFormatDateString(jo2.getString("tgl"), FormatItem.formatDate, FormatItem.formatDateDisplay);
                                if(jo2.getString("bayar").equals(jo.getString("bayar")) && lastTgl.equals(newTgl)){

                                    listSetoran.add(new CustomItem("I", jo.getString("customer")
                                            , jo.getString("total")
                                            , jo.getString("bayar")+ " - "+ jo.getString("namaakun") + " ("+jo.getString("nobukti") + ")")
                                    );
                                    totalPerNama += iv.parseNullLong(jo.getString("total"));
                                }else{

                                    listSetoran.add(new CustomItem("I", jo.getString("customer")
                                            , jo.getString("total")
                                            , jo.getString("bayar")+ " - "+ jo.getString("namaakun") + " ("+jo.getString("nobukti") + ")"));
                                    totalPerNama += iv.parseNullLong(jo.getString("total"));
                                    listSetoran.add(new CustomItem("F", String.valueOf(totalPerNama), "Total "+jo.getString("bayar")));
                                    totalPerNama = 0;
                                }
                            }else{

                                listSetoran.add(new CustomItem("I", jo.getString("customer")
                                        , jo.getString("total")
                                        , jo.getString("bayar")+ " - "+ jo.getString("namaakun") + " ("+jo.getString("nobukti") + ")"));
                                totalPerNama += iv.parseNullLong(jo.getString("total"));
                                listSetoran.add(new CustomItem("F", String.valueOf(totalPerNama), "Total "+jo.getString("bayar")));
                                totalPerNama = 0;
                            }

                            totalAll += iv.parseNullDouble(jo.getString("total"));
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

                tvTotal.setText(iv.ChangeToRupiahFormat(totalAll));
                adapterRiwayat.notifyDataSetChanged();
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
