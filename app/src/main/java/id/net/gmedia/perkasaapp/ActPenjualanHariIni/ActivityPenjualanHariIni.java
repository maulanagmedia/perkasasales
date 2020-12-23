package id.net.gmedia.perkasaapp.ActPenjualanHariIni;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActPenjualanHariIni.Adapter.PenjualanHariIniAdapter;
import id.net.gmedia.perkasaapp.ActRiwayatPenjualan.ActivityListSales;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityPenjualanHariIni extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private ListView lvRiwayat;
    private EditText edtSearch;
    private String keyword = "";
    private List<CustomItem> listTransaksi = new ArrayList<>();
    private PenjualanHariIniAdapter adapter;
    private TextView tvTotal, tvTotalTcash, tvTotalRS;
    private LinearLayout llCustom;
    private Button btnPilihSales;
    private TextView tvSales;
    private SessionManager session;
    private String selectedNikGa = "", selectedNikMkios = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_hari_ini);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Penjualan Hari Ini");
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
        lvRiwayat = (ListView) findViewById(R.id.lv_riwayat);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvTotalTcash = (TextView) findViewById(R.id.tv_total_tcash);
        tvTotalRS = (TextView) findViewById(R.id.tv_total_rs);
        keyword  = "";
        listTransaksi = new ArrayList<>();
        adapter = new PenjualanHariIniAdapter(context, listTransaksi);
        lvRiwayat.setAdapter(adapter);

        tvSales.setText(session.getNama());
        if(session.isSuperuser()){
            llCustom.setVisibility(View.VISIBLE);
        }else{
            llCustom.setVisibility(View.GONE);
        }
    }

    private void initEvent() {

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    listTransaksi.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
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
                listTransaksi.clear();
                initData();

            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", "");
            jBody.put("count", "");
            jBody.put("nik_ga", selectedNikGa);
            jBody.put("nik_mkios", selectedNikMkios);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getTransaksiHariIni, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                double totalAll = 0, totalTcash = 0, totalRS = 0;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listTransaksi.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        String lastJenis = "", lastFlag = "";
                        double totalPerNama = 0;

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);

                            //header
                            if(!lastJenis.equals(jo.getString("jenis")) || !lastFlag.equals(jo.getString("flag"))){

                                lastJenis = jo.getString("jenis");
                                lastFlag = jo.getString("flag");
                                String keterangan = lastFlag;
                                if(lastJenis.equals("1")){

                                    keterangan = "Transaksi " + lastFlag;
                                }else if(lastJenis.equals("2")){

                                    keterangan = "Riwayat " + lastFlag;
                                }

                                listTransaksi.add(new CustomItem("H", keterangan));

                                totalPerNama = 0;
                            }

                            if(i < jsonArray.length() - 1){

                                JSONObject jo2 = jsonArray.getJSONObject(i+1);
                                if(jo2.getString("nama").equals(jo.getString("nama")) && lastJenis.equals(jo2.getString("jenis"))){

                                    listTransaksi.add(new CustomItem(
                                            "I"
                                            , jo.getString("nama")
                                            , jo.getString("total")
                                            , ((jo.getString("status").isEmpty()) ? jo.getString("flag") : jo.getString("status")) + " / " + iv.ChangeFormatDateString(jo.getString("usertgl"), FormatItem.formatTimestamp, FormatItem.formatTime)
                                            , jo.getString("is_rs")
                                    ));
                                    totalPerNama += iv.parseNullLong(jo.getString("total"));
                                }else{

                                    listTransaksi.add(new CustomItem(
                                            "I"
                                            , jo.getString("nama")
                                            , jo.getString("total")
                                            , ((jo.getString("status").isEmpty()) ? jo.getString("flag") : jo.getString("status")) + " / " + iv.ChangeFormatDateString(jo.getString("usertgl"), FormatItem.formatTimestamp, FormatItem.formatTime)
                                            , jo.getString("is_rs")
                                    ));
                                    totalPerNama += iv.parseNullLong(jo.getString("total"));
                                    listTransaksi.add(new CustomItem("F", String.valueOf(totalPerNama)));
                                    totalPerNama = 0;
                                }
                            }else{

                                listTransaksi.add(new CustomItem(
                                        "I"
                                        , jo.getString("nama")
                                        , jo.getString("total")
                                        , ((jo.getString("status").isEmpty()) ? jo.getString("flag") : jo.getString("status")) + " / " + iv.ChangeFormatDateString(jo.getString("usertgl"), FormatItem.formatTimestamp, FormatItem.formatTime)
                                        , jo.getString("is_rs")

                                ));
                                totalPerNama += iv.parseNullLong(jo.getString("total"));
                                listTransaksi.add(new CustomItem("F", String.valueOf(totalPerNama)));
                                totalPerNama = 0;
                            }

                            if(!jo.getString("jenis").equals("1") // transaksi
                                    && !jo.getString("crbayar").toUpperCase().equals("K")){ // carabayar tempo

                                if(jo.getString("is_rs").equals("0")){

                                    if(jo.getString("flag").trim().toUpperCase().equals("TCASH")){

                                        totalTcash += iv.parseNullDouble(jo.getString("total"));
                                    }else{
                                        totalAll += iv.parseNullDouble(jo.getString("total"));
                                    }
                                }else{
                                    totalRS += iv.parseNullDouble(jo.getString("total"));
                                }
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

                tvTotal.setText(iv.ChangeToRupiahFormat(totalAll));
                tvTotalTcash.setText(iv.ChangeToRupiahFormat(totalTcash));
                tvTotalRS.setText(iv.ChangeToRupiahFormat(totalRS));
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
