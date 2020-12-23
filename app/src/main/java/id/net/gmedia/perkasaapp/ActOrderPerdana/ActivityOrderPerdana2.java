package id.net.gmedia.perkasaapp.ActOrderPerdana;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.CustomView.EndlessScroll;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter.AdapterOrderPerdanaBarang;
import id.net.gmedia.perkasaapp.ModelPerdana;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityOrderPerdana2 extends AppCompatActivity {

    private List<ModelPerdana> listPerdana = new ArrayList<>();
    private TextView txt_nama;
    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private AdapterOrderPerdanaBarang adapter;
    private SlidingUpPanelLayout suplContainer;
    private ImageView ivIcon;
    private String nama = "", kdcus = "";
    private RecyclerView rcy_barang;
    private View footerList;
    private DialogBox dialogBox;
    private String keyword = "";
    private int start = 0, count = 10;
    private EditText edtSearch;
    private boolean isLoading = false;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_perdana2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Perdana");
        }

        context = this;

        initUI();
        initEvent();
        initData();
        //initPerdana();
    }

    private void initUI() {

        txt_nama = (TextView) findViewById(R.id.txt_nama);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        suplContainer = (SlidingUpPanelLayout) findViewById(R.id.supl_container);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;
        dialogBox = new DialogBox(context);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            nama = bundle.getString("nama", "");

            txt_nama.setText(nama);
        }

        adapter = new AdapterOrderPerdanaBarang(listPerdana, kdcus);

        rcy_barang = findViewById(R.id.rcy_barang);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcy_barang.setLayoutManager(layoutManager);
        rcy_barang.setAdapter(adapter);

        EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if(!isLoading){

                    start += count;
                    initData();
                }
            }
        };

        rcy_barang.addOnScrollListener(scrollListener);
    }

    private void initEvent() {

        suplContainer.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){

                    ivIcon.setImageResource(R.mipmap.ic_up);
                }else{

                    ivIcon.setImageResource(R.mipmap.ic_down);
                }
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    listPerdana.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });
    }

    private void initData() {

        /*listPerdana.add(new ModelPerdana(
                "Barang"
                ,"29j/okejek"
                ,"20000"
                ,"5"
                ,"2018-08-30"
                ,"1101"
                ,"3001"
        ));*/
        pbLoading.setVisibility(View.VISIBLE);
        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("keyword", keyword);
            jBody.put("kdcus", kdcus);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSuratJalan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(start == 0) dialogBox.dismissDialog();
                String message = "";
                isLoading = false;
                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listPerdana.add(new ModelPerdana(
                                    jo.getString("namabrg")
                                    ,jo.getString("nobukti")
                                    ,jo.getString("harga")
                                    ,jo.getString("jml")
                                    ,jo.getString("tgl")
                                    ,jo.getString("kodegudang")
                                    ,jo.getString("kodebrg")
                                    ,jo.getString("tipeprogram")
                            ));
                        }

                    }else{

                        if(start == 0) DialogBox.showDialog(context, 3, message);
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

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
                if(start == 0) dialogBox.dismissDialog();
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

    private void initPerdana(){
        //Tempat inisialisasi Perdana
        ModelPerdana perdana = new ModelPerdana("128k 4G LTE 2FF/3FF/4FF USIM MIGRATION (S100)", "SJ/MG/1806/0350", 3000, 1520);
        listPerdana.add(perdana);
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
