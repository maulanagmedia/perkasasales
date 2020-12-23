package id.net.gmedia.perkasaapp;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ActivityPreorderPerdana1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorder_perdana1);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Riwayat Preorder Perdana");
        }

        FloatingActionButton float_tambah = findViewById(R.id.float_tambah);
        float_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPreorderPerdana1.this, ActivityPreorderPerdana2.class));
            }
        });

        EditText txt_cari = findViewById(R.id.txt_cari);
        txt_cari.setHint(R.string.nama_barang);
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
