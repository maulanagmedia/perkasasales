package id.net.gmedia.perkasaapp.ActPelunasanPiutang;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActPelunasanPiutang.Adapter.AdapterDetailSetoran;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DetailNotaTotalSetoran extends AppCompatActivity {

    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private RecyclerView rvSetoran;
    private String kodeAkun = "", dateStart = "", dateEnd = "";
    private List<CustomItem> listSetoran = new ArrayList<>();
    private TextView tvTotal;
    private AdapterDetailSetoran adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nota_total_setoran);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Total Setoran");
        }

        context = this;
        dialogBox = new DialogBox(context);
        initUI();
        initEvent();
        initSetoran();
    }

    private void initUI() {

        rvSetoran = (RecyclerView) findViewById(R.id.rv_setoran);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        kodeAkun = "";

        adapter = new AdapterDetailSetoran(listSetoran, context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSetoran.setLayoutManager(layoutManager);
        rvSetoran.setItemAnimator(new DefaultItemAnimator());
        rvSetoran.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            kodeAkun = bundle.getString("kodeakun", "");
            dateStart = bundle.getString("start", "");
            dateEnd = bundle.getString("end", "");
            String title = bundle.getString("title", "");
            String subTitle = bundle.getString("subtitle", "");

            setTitle(title);
            getSupportActionBar().setSubtitle(subTitle);
        }
    }

    private void initEvent() {


    }

    private void initSetoran(){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("datestart", dateStart);
            jBody.put("dateend", dateEnd);
            jBody.put("kodeakun", kodeAkun);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDetailRekapSetoran, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";
                double total = 0;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    listSetoran.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");

                        for(int i = 0; i < ja.length();i++){

                            JSONObject jo = ja.getJSONObject(i);
                            listSetoran.add(new CustomItem(
                                    jo.getString("id")
                                    ,jo.getString("tgl")
                                    ,jo.getString("customer")
                                    ,jo.getString("nonota")
                                    ,jo.getString("jumlah")
                            ));

                            total += iv.parseNullDouble(jo.getString("jumlah"));
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
                            initSetoran();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                tvTotal.setText(iv.ChangeToRupiahFormat(total));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initSetoran();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
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
