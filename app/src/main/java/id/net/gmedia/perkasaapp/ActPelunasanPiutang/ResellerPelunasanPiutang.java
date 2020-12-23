package id.net.gmedia.perkasaapp.ActPelunasanPiutang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter.ListResellerPerdanaAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ResellerPelunasanPiutang extends AppCompatActivity {

    private List<CustomItem> listReller = new ArrayList<>();
    private ListView lvReseller;
    private ListResellerPerdanaAdapter adapterReseller;
    private View footerList;
    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private String keyword = "";
    private int start = 0, count = 10;
    private EditText edtSearch;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller_pelunasan_piutang);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pilih Reseller / POI");
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
        lvReseller = (ListView) findViewById(R.id.lv_reseller);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvReseller.addFooterView(footerList);
        adapterReseller = new ListResellerPerdanaAdapter((Activity) context, listReller);
        lvReseller.removeFooterView(footerList);
        lvReseller.setAdapter(adapterReseller);

        lvReseller.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvReseller.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvReseller.getLastVisiblePosition() >= total - threshold && !isLoading) {

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
    }

    private void initEvent() {
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    listReller.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        lvReseller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(view.getContext(), DetailPelunasanPiutang.class);
                intent.putExtra("kdcus", item.getItem1());
                intent.putExtra("nama", item.getItem2());
                view.getContext().startActivity(intent);
            }
        });
    }

    private void initData() {

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvReseller.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
            jBody.put("nomor", "");
            jBody.put("kdcus", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getResellerPelunasan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvReseller.removeFooterView(footerList);
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
                            listReller.add(new CustomItem(
                                    jo.getString("kdcus")
                                    ,jo.getString("nama")
                                    ,jo.getString("alamat")
                                    /*,jo.getString("latitude")
                                    ,jo.getString("longitude")*/
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

                adapterReseller.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvReseller.removeFooterView(footerList);
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
