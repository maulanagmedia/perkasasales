package id.net.gmedia.perkasaapp;

import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class ActivityPreorderPerdana3 extends AppCompatActivity {

    private Calendar now;
    private DatePickerDialog.OnDateSetListener dateListener;
    private TextView txt_tanggal, txt_jumlah;
    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorder_perdana3);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Preorder Perdana");
        }

        ImageView btn_tanggal;
        Button btn_proses;
        TextView txt_nama, txt_harga;
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_tanggal = findViewById(R.id.txt_tanggal);
        btn_tanggal = findViewById(R.id.btn_tanggal);
        btn_proses = findViewById(R.id.btn_proses);

        if(getIntent().hasExtra("perdana")){
            ModelPerdana perdana = getIntent().getParcelableExtra("perdana");
            txt_nama.setText(perdana.getNama());
            txt_harga.setText(RupiahFormatterUtil.getRupiah(perdana.getHarga()));
        }

        now = Calendar.getInstance();
        day = now.get(Calendar.DATE);
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        txt_tanggal.setText(String.format(Locale.getDefault(), "%d/%d/%d", day, month, year));

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ActivityPreorderPerdana3.this.day = dayOfMonth;
                ActivityPreorderPerdana3.this.month = month;
                ActivityPreorderPerdana3.this.year = year;
                txt_tanggal.setText(String.format(Locale.getDefault(), "%d/%d/%d", day, month, year));
            }
        };

        btn_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityPreorderPerdana3.this, dateListener, year, month, day).show();
            }
        });

        btn_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = false;
                if(now.get(Calendar.YEAR) == year){
                    if(now.get(Calendar.MONTH) == month){
                        if(now.get(Calendar.DATE) <= day){
                            valid = true;
                        }
                    }
                    else if(now.get(Calendar.MONTH) < month){
                        valid = true;
                    }
                }
                else if(now.get(Calendar.YEAR) < year){
                    valid = true;
                }

                if(!valid){
                    Toast.makeText(ActivityPreorderPerdana3.this, "Tanggal tidak valid", Toast.LENGTH_SHORT).show();
                }
                else{
                    String jumlah_string = txt_jumlah.getText().toString();
                    if(!jumlah_string.equals("")){
                        int jumlah = Integer.parseInt(jumlah_string);
                    }
                    else{
                        Toast.makeText(ActivityPreorderPerdana3.this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                }
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
