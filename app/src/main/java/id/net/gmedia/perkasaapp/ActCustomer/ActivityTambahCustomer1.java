package id.net.gmedia.perkasaapp.ActCustomer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActCustomer.Adapter.ListCustomerAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityTambahCustomer1 extends AppCompatActivity{

    private EditText edtKeyword;
    private List<CustomItem> listItems = new ArrayList<>();
    private ListView lvReseller;
    private ListCustomerAdapter adapter;
    private View footerList;
    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private String keyword = "";
    private int start = 0, count = 10;
    private boolean isLoading = false;
    private FloatingActionButton fabTambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_customer1);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Data Reseller");
        }

        context = this;
        iv.hideSoftKey(context);
        initUI();
        initOutlet();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);
        fabTambah = (FloatingActionButton) findViewById(R.id.float_tambah);
        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityTambahCustomer1.this, ActivityTambahCustomer2.class));
            }
        });

        edtKeyword = (EditText) findViewById(R.id.txt_cari);
        edtKeyword.setHint(R.string.nama_customer);

        lvReseller = (ListView) findViewById(R.id.lv_reseller);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvReseller.addFooterView(footerList);
        adapter = new ListCustomerAdapter((Activity) context, listItems);
        lvReseller.removeFooterView(footerList);
        lvReseller.setAdapter(adapter);

        lvReseller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(context, ActivityTambahCustomer2.class);
                intent.putExtra("kdcus", item.getItem1());
                startActivity(intent);
            }
        });

        lvReseller.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvReseller.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvReseller.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initOutlet();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void initOutlet(){

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvReseller.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getResellerInfo, new ApiVolley.VolleyCallback() {
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
                            listItems.add(new CustomItem(
                                    jo.getString("kdcus")
                                    ,jo.getString("nama")
                                    ,jo.getString("alamat")
                                    ,jo.getString("notelp")
                                    ,jo.getString("nohp")
                                    ,jo.getString("status")
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
                            initOutlet();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
                }

                adapter.notifyDataSetChanged();
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
                        initOutlet();
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
