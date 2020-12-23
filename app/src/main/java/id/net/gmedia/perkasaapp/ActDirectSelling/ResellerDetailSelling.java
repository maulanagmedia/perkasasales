package id.net.gmedia.perkasaapp.ActDirectSelling;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.net.gmedia.perkasaapp.ActDirectSelling.Adapter.AutocompleteAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ResellerDetailSelling extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtNomor;
    private EditText edtLokasi;
    private AutoCompleteTextView actvNama;
    private EditText edtAlamat;
    private LinearLayout llBeliPulsa, llBeliPerdana;
    private ProgressBar pbLoading;
    private List<HashMap<String,String >> masterList;
    private SimpleAdapter adapter;
    private String nomorEvent = "", lokasi = "", lastKdcus = "", lastCus = "", selectedKdcus = "";
    private boolean isEvent = false;
    private LinearLayout llNomor, llLokasi;
    private AutoCompleteTextView actvPoi;
    private LinearLayout llPOI;
    private List<CustomItem> listPOI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller_detail_selling);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Detail Pelanggan Event");
        context = this;

        initUI();
    }

    private void initUI() {

        llNomor = (LinearLayout) findViewById(R.id.ll_nomor);
        llLokasi = (LinearLayout) findViewById(R.id.ll_lokasi);
        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        edtLokasi = (EditText) findViewById(R.id.edt_lokasi);
        actvPoi = (AutoCompleteTextView) findViewById(R.id.actv_poi);
        actvNama = (AutoCompleteTextView) findViewById(R.id.actv_nama);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        llBeliPulsa = (LinearLayout) findViewById(R.id.ll_beli_pulsa);
        llBeliPerdana = (LinearLayout) findViewById(R.id.ll_beli_perdana);
        llPOI = (LinearLayout) findViewById(R.id.ll_poi);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        session = new SessionManager(context);
        isEvent = false;
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            nomorEvent = bundle.getString("nomorevent","");
            lokasi = bundle.getString("lokasi","");

            if(nomorEvent.length() > 0){

                llNomor.setVisibility(View.VISIBLE);
                llLokasi.setVisibility(View.VISIBLE);

                edtNomor.setText(nomorEvent);
                edtLokasi.setText(lokasi);
                isEvent = true;
            }

        }

        if(!isEvent){
            setTitle("Detail Direct Selling");
            llPOI.setVisibility(View.VISIBLE);
        }

        initEvent();

        getDataOutlet();
    }

    private void getDataOutlet() {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getResellerPerdana, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();
                    listPOI = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            listPOI.add(new CustomItem(
                                    jo.getString("kdcus"),
                                    jo.getString("nama"),
                                    jo.getString("alamat")
                            ));
                        }
                    }

                    setAutocomplete(listPOI);

                } catch (JSONException e) {
                    e.printStackTrace();
                    setAutocomplete(null);
                }
            }

            @Override
            public void onError(String result) {

                setAutocomplete(null);
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setAutocomplete(List<CustomItem> listItem) {

        actvPoi.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            AutocompleteAdapter adapterACTV = new AutocompleteAdapter(context, listItem, "");
            actvPoi.setAdapter(adapterACTV);
            actvPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                    lastKdcus = item.getItem1();
                    lastCus = item.getItem2();
                    String alamat =item.getItem3();

                    /*actvNama.setText(lastCus);
                    edtAlamat.setText(alamat);*/
                    //actvPoi.setText(lastCus);
                    //if(actvPoi.getText().length() > 0) actvPoi.setSelection(actvPoi.getText().length());

                }
            });
        }
    }

    private void initEvent() {

        llBeliPulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                redirectToDetail(true);
            }
        });

        llBeliPerdana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                redirectToDetail(false);
            }
        });
    }

    private void redirectToDetail(boolean isPulsa){

        //Validasi

        if(actvPoi.getText().length() > 0 && !lastCus.equals(actvPoi.getText().toString())){
            actvPoi.setError("POI tidak ditemukan");
            actvPoi.requestFocus();
            return;

        }

        if(isEvent && edtNomor.getText().toString().length() == 0){

            Toast.makeText(context, "Data event tidak termuat, silahkan ulangi proses", Toast.LENGTH_LONG).show();
            return;
        }

        if(actvPoi.getText().toString().length() > 0) {
            selectedKdcus = actvPoi.getText().toString().equals(lastCus) ? lastKdcus: "";
        }else{
            selectedKdcus = "";
        }

        if(actvNama.getText().toString().length() == 0){

            actvNama.setError("Nama harap diisi");
            actvNama.requestFocus();
            return;
        }else{
            actvNama.setError(null);
            //selectedKdcus = actvNama.getText().toString().equals(lastCus) ? lastKdcus: "";
        }

        if(edtAlamat.getText().toString().length() == 0){

            edtAlamat.setError("Alamat harap diisi");
            edtAlamat.requestFocus();
            return;
        }else{

            edtAlamat.setError(null);
        }

        Intent intent = new Intent(context, DirectSellingPulsa.class);
        if(!isPulsa) intent = new Intent(context, DirectSellingPerdana.class);

        intent.putExtra("nama", actvNama.getText().toString());
        intent.putExtra("alamat", edtAlamat.getText().toString());
        intent.putExtra("nomorevent", nomorEvent);
        if(lastCus.equals(actvPoi.getText().toString()) && !actvPoi.getText().toString().equals("")){
            intent.putExtra("kdcus", lastKdcus);
            intent.putExtra("namapoi", lastCus);
        }
        startActivity(intent);
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
