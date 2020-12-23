package id.net.gmedia.perkasaapp.ActKunjunganAdmin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
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

import id.net.gmedia.perkasaapp.ActKunjunganAdmin.Adapter.ListKunjunganAdminAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ListKunjunganAdmin extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private ListView lvKunjungan;
    private boolean firstLoad = true;
    private boolean isLoading = false;
    private int startIndex = 0, count = 10;
    private View footerList;
    private List<CustomItem> masterList = new ArrayList<>();
    private ListKunjunganAdminAdapter adapter;
    private FloatingActionButton btnAdd;
    private String flag = "";
    private String nik;
    private boolean selfCheckin = true;
    private String namaSales = "";
    private LinearLayout llTop;
    private TextView tvDate;
    private String dateString = "";
    private String dateKunjungan = "";
    private String selectedNikGa = "", selectedNikMkios = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kunjungan_admin);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getSupportActionBar().setTitle("Kunjungan Admin");
        }

        context = this;
        dialogBox = new DialogBox(context);

        initUI();
        initEvent();

    }

    private void initUI() {

        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvDate = (TextView) findViewById(R.id.tv_date);
        lvKunjungan = (ListView) findViewById(R.id.lv_kunjungan);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        session = new SessionManager(context);
        dateKunjungan = iv.getCurrentDate(FormatItem.formatDate);

        selfCheckin = true;
        startIndex = 0;
        count = 10;
        isLoading = false;

        lvKunjungan.addFooterView(footerList);
        adapter = new ListKunjunganAdminAdapter((Activity) context, masterList);
        lvKunjungan.removeFooterView(footerList);
        lvKunjungan.setAdapter(adapter);

        lvKunjungan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvKunjungan.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvKunjungan.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        startIndex += count;
                        getDataKunjungan();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        lvKunjungan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem selectedItem = (CustomItem) adapterView.getItemAtPosition(i);
                /*if(!selectedItem.getItem9().equals("0")){
                    *//*Intent intent = new Intent(context, DetailKunjungan.class);
                    intent.putExtra("kdcus", selectedItem.getItem1());
                    intent.putExtra("timestamp", selectedItem.getItem2());
                    intent.putExtra("nik", selectedItem.getItem7());
                    intent.putExtra("flag", selfCheckin);
                    intent.putExtra("nama", namaSales);
                    intent.putExtra("id", selectedItem.getItem9());
                    ((Activity) context).startActivity(intent);*//*
                }*/
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            nik = bundle.getString("nik");
            selfCheckin = false;

            if(nik == null || nik.length() == 0){
                nik = session.getNikGa();
                selfCheckin = true;
            }else{
                namaSales = bundle.getString("nama");
                setTitle("Kunjungan Sales");
                getSupportActionBar().setSubtitle("a/n "+ namaSales);

            }
        }else{
            nik = session.getNikGa();
        }
    }

    private void initEvent() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListOutletKunjunganAdmin.class);
                startActivity(intent);
            }
        });

        initHeaderEvent();

    }

    private void initHeaderEvent(){

        llTop.setVisibility(View.VISIBLE);
        dateString = iv.getCurrentDate(FormatItem.formatDateDisplay);
        tvDate.setText(dateString);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateString);
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
                        dateString = sdFormat.format(customDate.getTime());
                        tvDate.setText(dateString);
                        dateKunjungan = iv.ChangeFormatDateString(dateString, FormatItem.formatDateDisplay, FormatItem.formatDate);
                        startIndex = 0;
                        masterList.clear();
                        getDataKunjungan();
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startIndex = 0;
        masterList.clear();
        getDataKunjungan();
    }

    private void getDataKunjungan() {

        isLoading = true;
        if(startIndex == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        selectedNikGa = session.getNikGa();
        selectedNikMkios = session.getNikMkios();
        lvKunjungan.addFooterView(footerList);

        try {
            jBody.put("start", startIndex);
            jBody.put("count", count);
            jBody.put("tgl", dateKunjungan);
            jBody.put("keyword", "");
            jBody.put("nomor", "");
            jBody.put("kdcus", "");
            jBody.put("nik_ga", selectedNikGa);
            jBody.put("nik_mkios", selectedNikMkios);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getJadwalSurveyAdmin, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvKunjungan.removeFooterView(footerList);
                if(startIndex == 0) dialogBox.dismissDialog();
                String message = "";
                isLoading = false;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            masterList.add(
                                    new CustomItem(
                                            jo.getString("kdcus")
                                            , jo.getString("timestamp")
                                            , jo.getString("nama")
                                            , jo.getString("alamat")
                                            , jo.getString("notelp")
                                    ));
                        }

                    }else{

                        if(startIndex == 0) DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKunjungan();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvKunjungan.removeFooterView(footerList);
                isLoading = false;
                if(startIndex == 0) dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKunjungan();
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
