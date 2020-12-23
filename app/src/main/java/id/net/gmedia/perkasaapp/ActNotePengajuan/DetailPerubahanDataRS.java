package id.net.gmedia.perkasaapp.ActNotePengajuan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DetailPerubahanDataRS extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private TextView tvNama, tvAlamat;
    private EditText edtKeterangan;
    private Button btnProses;
    private String kdcus = "", idPengajuan = "";
    public static final String flag = "PERUBAHANRS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_perubahan_data_rs);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Perubahan Data RS");
        }

        context = this;
        dialogBox = new DialogBox(context);

        initUI();
        if(!idPengajuan.isEmpty()) initData();
        initEvent();
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvAlamat = (TextView) findViewById(R.id.tv_alamat);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        btnProses = (Button) findViewById(R.id.btn_proses);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            String nama = bundle.getString("nama", "");
            String alamat = bundle.getString("alamat", "");

            idPengajuan = bundle.getString("id", "");

            tvNama.setText(nama);
            tvAlamat.setText(alamat);

            if(!idPengajuan.isEmpty()){

                //btnProses.setEnabled(false);
            }else{
                //btnProses.setEnabled(true);
            }
        }
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", idPengajuan);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPengajuanReseller, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");

                        edtKeterangan.setText(jo.getString("keterangan"));
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

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi

                if(!idPengajuan.isEmpty()){

                    Toast.makeText(context, "Maaf data yang sudah tersimpan tidak dapat diubah", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtKeterangan.getText().toString().isEmpty()){

                    edtKeterangan.setError("Keterangan harap diisi");
                    edtKeterangan.requestFocus();
                    return;
                }else{

                    edtKeterangan.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan permohonan perubahan data reseller?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData();
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

    private void saveData() {

        btnProses.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdcus);
            jBody.put("keterangan", edtKeterangan.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.savePengajuanReseller, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                btnProses.setEnabled(true);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        Intent intent = new Intent(context, ActivityHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("flag", flag);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(context, "Terjadi kesalahan koneksi, harap ulangi kembali nanti", Toast.LENGTH_SHORT).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                btnProses.setEnabled(true);
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
