package id.net.gmedia.perkasaapp.ActMarketSurvey;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.perkasaapp.ActMarketSurvey.Adapter.ListMarketSurveyAdapter;
import id.net.gmedia.perkasaapp.ActRiwayatPenjualan.ActivityListSales;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ListMarketSurvey extends AppCompatActivity {

    private Context context;
    private DialogBox dialogBox;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private FloatingActionButton fabAdd;
    private TextView tvStart, tvEnd;
    private ImageView ivShow;
    private ListView lvSurvey;
    private LinearLayout btnKalenderStart, btnKalenderEnd;
    private String keyword = "", dateStart = "", dateEnd = "";
    private EditText edtKeyword;
    private List<CustomItem> listSurvey = new ArrayList<>();
    private ListMarketSurveyAdapter adapter;
    private LinearLayout llCustom;
    private Button btnPilihSales;
    private TextView tvSales;
    private String selectedNikGa = "", selectedNikMkios = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_market_survey);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Market Survey");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        llCustom = (LinearLayout) findViewById(R.id.ll_custom);
        btnPilihSales = (Button) findViewById(R.id.btn_pilih_sales);
        tvSales = (TextView) findViewById(R.id.tv_sales);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        tvStart = (TextView) findViewById(R.id.tv_start);
        tvEnd = (TextView) findViewById(R.id.tv_end);
        ivShow = (ImageView) findViewById(R.id.iv_show);
        lvSurvey = (ListView) findViewById(R.id.lv_survey);
        btnKalenderStart = findViewById(R.id.btn_kalender_start);
        btnKalenderEnd = findViewById(R.id.btn_kalender_end);
        edtKeyword = (EditText) findViewById(R.id.edt_keyword);

        keyword = "";
        dateStart = iv.getCurrentDate(FormatItem.formatDate);
        dateEnd = iv.getCurrentDate(FormatItem.formatDate);
        tvStart.setText(iv.ChangeFormatDateString(dateStart, FormatItem.formatDate, FormatItem.formatDateDisplay));
        tvEnd.setText(iv.ChangeFormatDateString(dateEnd, FormatItem.formatDate, FormatItem.formatDateDisplay));

        listSurvey = new ArrayList<>();
        adapter = new ListMarketSurveyAdapter((Activity) context, listSurvey);
        lvSurvey.setAdapter(adapter);

        lvSurvey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailMarketSurvey.class);
                intent.putExtra("id", item.getItem1());
                intent.putExtra("kdcus", item.getItem5());
                intent.putExtra("nama", item.getItem3());
                view.getContext().startActivity(intent);
            }
        });

        tvSales.setText(session.getNama());
        if(session.isSuperuser()){
            llCustom.setVisibility(View.VISIBLE);
        }else{
            llCustom.setVisibility(View.GONE);
        }
    }

    private void initEvent() {

        btnKalenderStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);
                Date dateValue = null;
                final Calendar customDate;

                try {
                    dateValue = sdf.parse(iv.ChangeFormatDateString(dateStart, FormatItem.formatDate, FormatItem.formatDateDisplay));
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
                        dateStart = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), FormatItem.formatDateDisplay, FormatItem.formatDate);
                        tvStart.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        btnKalenderEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);
                Date dateValue = null;
                final Calendar customDate;

                try {
                    dateValue = sdf.parse(iv.ChangeFormatDateString(dateEnd, FormatItem.formatDate, FormatItem.formatDateDisplay));
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
                        dateEnd = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), FormatItem.formatDateDisplay, FormatItem.formatDate);
                        tvEnd.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();
            }
        });

        edtKeyword.addTextChangedListener(new TextWatcher() {
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

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListResellerMarketSurvey.class);
                startActivity(intent);
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
                listSurvey.clear();
                initData();
            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("datestart", dateStart);
            jBody.put("dateend", dateEnd);
            jBody.put("keyword", keyword);
            jBody.put("nik_ga", selectedNikGa);
            jBody.put("nik_mkios", selectedNikMkios);
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
                    listSurvey.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");

                        for(int i = 0; i < ja.length();i++){

                            JSONObject jo = ja.getJSONObject(i);
                            listSurvey.add(new CustomItem(
                                    jo.getString("id")
                                    ,iv.ChangeFormatDateString(jo.getString("tanggal"), FormatItem.formatDate, FormatItem.formatDateDisplay) + " "+jo.getString("jam")
                                    ,jo.getString("customer")
                                    ,jo.getString("state")
                                    ,jo.getString("kdcus")
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
                            initData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                adapter.notifyDataSetChanged();
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
