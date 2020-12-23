package id.net.gmedia.perkasaapp.ActPerdanaCustom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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

import id.net.gmedia.perkasaapp.ActPerdanaCustom.Adapter.ListBarangPerdanaCustomAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class BarangPerdanaCustom extends AppCompatActivity {

    private List<CustomItem> masterList = new ArrayList<>();
    private ListView lvBarang;
    private ListBarangPerdanaCustomAdapter adapter;
    private View footerList;
    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private String keyword = "";
    private int start = 0, count = 10;
    private EditText edtSearch;
    private boolean isLoading = false;
    private String namaCus = "", kdcus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_perdana_custom);

        if(getSupportActionBar() != null){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("List Barang");
        }

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvBarang.addFooterView(footerList);
        adapter = new ListBarangPerdanaCustomAdapter((Activity) context, masterList);
        lvBarang.removeFooterView(footerList);
        lvBarang.setAdapter(adapter);

        lvBarang.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvBarang.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvBarang.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            namaCus = bundle.getString("nama", "");
            kdcus = bundle.getString("kdcus", "");

        }
    }

    private void initEvent() {

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    masterList.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        lvBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(view.getContext(), DetailJualPerdanaCustom.class);
                intent.putExtra("kodebrg", item.getItem1());
                intent.putExtra("namabrg", item.getItem2());
                intent.putExtra("harga", item.getItem3());
                intent.putExtra("stok", item.getItem4());
                intent.putExtra("kdcus", kdcus);
                intent.putExtra("namacus", namaCus);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void initData() {

        /*masterList.add(new CustomItem("1","Telkomsel 1 GB", "5000", "2"));
        masterList.add(new CustomItem("2","Telkomsel AS", "10000", "21"));
        masterList.add(new CustomItem("3","Telkomsel 4 GB", "20000", "23"));
        masterList.add(new CustomItem("4","Telkomsel 5 Mid", "10000", "12"));
        adapter.notifyDataSetChanged();*/
        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvBarang.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getBarangDealing, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvBarang.removeFooterView(footerList);
                if(start == 0) dialogBox.dismissDialog();
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
                            masterList.add(new CustomItem(
                                    jo.getString("kodebrg")
                                    ,jo.getString("namabrg")
                                    ,jo.getString("hargajual")
                                    ,jo.getString("stok")
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

                lvBarang.removeFooterView(footerList);
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
