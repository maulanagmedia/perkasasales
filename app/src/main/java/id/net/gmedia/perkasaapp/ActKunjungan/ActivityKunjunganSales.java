package id.net.gmedia.perkasaapp.ActKunjungan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import id.net.gmedia.perkasaapp.ActRiwayatPenjualan.Adapter.ListSalesAdapter;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActivityKunjunganSales extends AppCompatActivity {

    private List<CustomItem> listReller = new ArrayList<>();
    private ListView lvSales;
    private ListSalesAdapter adapterSales;
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
        setContentView(R.layout.activity_kunjungan_sales);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pilih Sales");
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
        lvSales = (ListView) findViewById(R.id.lv_sales);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvSales.addFooterView(footerList);
        adapterSales = new ListSalesAdapter((Activity) context, listReller);
        lvSales.removeFooterView(footerList);
        lvSales.setAdapter(adapterSales);

        lvSales.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvSales.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvSales.getLastVisiblePosition() >= total - threshold && !isLoading) {

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

        lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda memilih " + item.getItem2() + " ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("nik_ga",item.getItem1());
                                returnIntent.putExtra("nama",item.getItem2());
                                returnIntent.putExtra("nik_mkios",item.getItem2());
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    private void initData() {

        /*listReller.add(new CustomItem(
                "1"
                ,"Tetew"
                ,"Jangli"
        ));*/

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvSales.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSales, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvSales.removeFooterView(footerList);
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
                                    jo.getString("nik_ga")
                                    ,jo.getString("nama")
                                    ,jo.getString("nik_mkios")
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

                adapterSales.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvSales.removeFooterView(footerList);
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
