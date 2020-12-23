package id.net.gmedia.perkasaapp.ActOrderTcash;

import android.app.DatePickerDialog;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import id.net.gmedia.perkasaapp.R;

public class ActivityOrderTcash1 extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener start_dateListener,end_dateListener;
    int start_day, start_month, start_year, end_day, end_month, end_year;
    TextView txt_start, txt_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tcash1);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Tcash");
        }

        txt_start = findViewById(R.id.txt_start);
        txt_end = findViewById(R.id.txt_end);

        LinearLayout btn_kalender_start, btn_kalender_end;
        ImageView btn_confirm = findViewById(R.id.btn_confirm);
        btn_kalender_start = findViewById(R.id.btn_kalender_start);
        btn_kalender_end = findViewById(R.id.btn_kalender_end);

        FloatingActionButton float_tambah = findViewById(R.id.float_tambah);
        float_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityOrderTcash1.this, ActivityOrderTcash2.class));
            }
        });

        //INISIALISASI DATE PICKER
        Calendar now = Calendar.getInstance();
        start_day = end_day = now.get(Calendar.DATE);
        start_month = end_month = now.get(Calendar.MONTH);
        start_year = end_year = now.get(Calendar.YEAR);
        txt_start.setText(String.format(Locale.getDefault(), "%d/%d/%d", start_day, start_month, start_year));
        txt_end.setText(String.format(Locale.getDefault(), "%d/%d/%d", end_day, end_month, end_year));

        start_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                start_day = dayOfMonth;
                start_month = month;
                start_year = year;
                txt_start.setText(String.format(Locale.getDefault(), "%d/%d/%d", start_day, start_month, start_year));
            }
        };

        end_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                end_day = dayOfMonth;
                end_month = month;
                end_year = year;
                txt_end.setText(String.format(Locale.getDefault(), "%d/%d/%d", end_day, end_month, end_year));
            }
        };

        btn_kalender_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityOrderTcash1.this, start_dateListener, start_year, start_month, start_day).show();
                txt_start.setText(String.format(Locale.getDefault(), "%d/%d/%d", start_day, start_month, start_year));
            }
        });

        btn_kalender_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityOrderTcash1.this, end_dateListener, end_year, end_month, end_day).show();
                txt_end.setText(String.format(Locale.getDefault(), "%d/%d/%d", end_day, end_month, end_year));
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = false;
                if(end_year == start_year){
                    if(end_month == start_month){
                        if(end_day >= start_day){
                            valid = true;
                        }
                    }
                    else if(end_month > start_month){
                        valid = true;
                    }
                }
                else if(end_year > start_year){
                    valid = true;
                }

                if(!valid){
                    Toast.makeText(ActivityOrderTcash1.this, "Tanggal mulai tidak boleh melebihi tanggal akhir", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ActivityOrderTcash1.this, "Tanggal valid", Toast.LENGTH_SHORT).show();
                    //Tunjukkan histori penjualan
                    //Update total
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
