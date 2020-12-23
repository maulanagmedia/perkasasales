package id.net.gmedia.perkasaapp.ActKonsinyasi;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import id.net.gmedia.perkasaapp.ActKonsinyasi.InformasiBarang.OutletInfoBarang;
import id.net.gmedia.perkasaapp.ActKonsinyasi.MutasiKonsinyasi.DetailMutasiKonsinyasi;
import id.net.gmedia.perkasaapp.ActKonsinyasi.MutasiKonsinyasi.MutasiKonsinyasi;
import id.net.gmedia.perkasaapp.ActKonsinyasi.Rekonsinyasi.DetailRekonsinyasi;
import id.net.gmedia.perkasaapp.ActKonsinyasi.Rekonsinyasi.Rekonsinyasi;
import id.net.gmedia.perkasaapp.ActKonsinyasi.Retur.ActReturKonsinyasi;
import id.net.gmedia.perkasaapp.ActKonsinyasi.Retur.DetailReturKonsinyasi;
import id.net.gmedia.perkasaapp.R;

public class ActKonsinyasi extends AppCompatActivity {

    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private RelativeLayout rlMutasi, rlRekonsinyasi, rlInfoBarang, rlRetur;
    private String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_konsinyasi);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Konsinyasi");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initUI();
        initEvent();
    }



    private void initUI() {

        rlMutasi = (RelativeLayout) findViewById(R.id.rl_mutasi);
        rlRekonsinyasi = (RelativeLayout) findViewById(R.id.rl_rekonsinyasi);
        rlInfoBarang = (RelativeLayout) findViewById(R.id.rl_info_barang);
        rlRetur = (RelativeLayout) findViewById(R.id.rl_retur);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            flag = bundle.getString("flag", "");

            if(!flag.isEmpty()){

                if(flag.equals(DetailMutasiKonsinyasi.flag)){

                    startActivity(new Intent(context, MutasiKonsinyasi.class));
                }else if(flag.equals(DetailRekonsinyasi.flag)){
                    startActivity(new Intent(context, Rekonsinyasi.class));
                }else if(flag.equals(DetailReturKonsinyasi.flag)){
                    startActivity(new Intent(context, ActReturKonsinyasi.class));
                }
            }
        }
    }

    private void initEvent() {

        rlMutasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, MutasiKonsinyasi.class));
            }
        });

        rlRekonsinyasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, Rekonsinyasi.class));
            }
        });

        rlInfoBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, OutletInfoBarang.class));
            }
        });

        rlRetur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, ActReturKonsinyasi.class));
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
