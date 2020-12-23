package id.net.gmedia.perkasaapp.ActPengajuanPLSales;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class DetailApprovalPL extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private TextView tvNama;
    private EditText edtKeterangan;
    private Button btnSetujui, btnTolak;
    private String nik = "";
    private EditText edtNominal;
    private String currentJmlString = "";
    public static final String flag = "APPROVALPLAFONSALES";
    private List<OptionItem> listJenis = new ArrayList<>();
    private ArrayAdapter adapter;
    private TextView tvStart, tvEnd;
    private LinearLayout btnKalenderStart, btnKalenderEnd;
    private String dateStart = "", dateEnd = "";
    public static CustomItem item = new CustomItem();
    private TextView tvJenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_approval_pl);

        if(getSupportActionBar() != null){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Pengajuan Plafon");
        }

        context = this;
        dialogBox = new DialogBox(context);

        initUI();
        initEvent();
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        btnSetujui = (Button) findViewById(R.id.btn_setujui);
        btnTolak = (Button) findViewById(R.id.btn_tolak);
        tvJenis = (TextView) findViewById(R.id.tv_jenis);
        btnKalenderStart = findViewById(R.id.btn_kalender_start);
        btnKalenderEnd = findViewById(R.id.btn_kalender_end);
        dateStart = iv.getCurrentDate(FormatItem.formatDate);
        dateEnd = iv.getCurrentDate(FormatItem.formatDate);
        tvStart = (TextView) findViewById(R.id.tv_start);
        tvEnd = (TextView) findViewById(R.id.tv_end);
        tvStart.setText(iv.ChangeFormatDateString(dateStart, FormatItem.formatDate, FormatItem.formatDateDisplay));
        tvEnd.setText(iv.ChangeFormatDateString(dateEnd, FormatItem.formatDate, FormatItem.formatDateDisplay));

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nik = bundle.getString("nik", "");
            String nama = bundle.getString("nama", "");

            tvNama.setText(nama);

        }

        listJenis.add(new OptionItem("mkios","Mkios"));
        listJenis.add(new OptionItem("perdana","Perdana"));

        if(item.getItem1() != null && !item.getItem1().equals(null)){

            tvNama.setText(item.getItem4());
            tvJenis.setText(item.getItem2());
            edtNominal.setText(iv.ChangeToCurrencyFormat(item.getItem5()));
            tvStart.setText(iv.ChangeFormatDateString(item.getItem8(), FormatItem.formatDate, FormatItem.formatDateDisplay));
            tvEnd.setText(iv.ChangeFormatDateString(item.getItem9(), FormatItem.formatDate, FormatItem.formatDateDisplay));
            edtKeterangan.setText(item.getItem10());
            btnSetujui.setEnabled(true);
            btnTolak.setEnabled(true);
        }else{

            btnSetujui.setEnabled(false);
            btnTolak.setEnabled(false);
        }


    }

    private void initEvent() {

        /*btnKalenderStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);
                Date dateValue = null;
                final Calendar customDate;

                try {
                    dateValue = sdf.parse(iv.ChangeFormatDateString(dateStart, FormatItem.formatDate, FormatItem.formatDateDisplay));
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
                        dateStart = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), FormatItem.formatDateDisplay, FormatItem.formatDate);
                        tvStart.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        btnKalenderEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);
                Date dateValue = null;
                final Calendar customDate;

                try {
                    dateValue = sdf.parse(iv.ChangeFormatDateString(dateEnd, FormatItem.formatDate, FormatItem.formatDateDisplay));
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
                        dateEnd = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), FormatItem.formatDateDisplay, FormatItem.formatDate);
                        tvEnd.setText(sdFormat.format(customDate.getTime()));
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });*/

        edtNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentJmlString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtNominal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);
                    cleanString = formatted;
                    edtNominal.setText(formatted);
                    edtNominal.setSelection(formatted.length());
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        btnSetujui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(iv.parseNullDouble(edtNominal.getText().toString().replaceAll("[,.]", "")) <= 0){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else{

                    edtNominal.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyetujui permohonan ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData("2");
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

        btnTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(iv.parseNullDouble(edtNominal.getText().toString().replaceAll("[,.]", "")) <= 0){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else{

                    edtNominal.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menolak permohonan ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData("9");
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

    private void saveData(String approve) {

        btnSetujui.setEnabled(false);
        btnTolak.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Default_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", item.getItem1());
            jBody.put("approval", approve);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveApprovalPlafonSales, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                btnTolak.setEnabled(true);
                btnSetujui.setEnabled(true);

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
                btnTolak.setEnabled(true);
                btnSetujui.setEnabled(true);
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
