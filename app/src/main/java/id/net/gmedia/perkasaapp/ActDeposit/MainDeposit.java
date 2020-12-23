package id.net.gmedia.perkasaapp.ActDeposit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import id.net.gmedia.perkasaapp.R;

public class MainDeposit extends AppCompatActivity {

private RelativeLayout pengajuan, pembelian, history;
private  Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_deposit);
        setTitle("Pengajuan Deposit");
        initUI();
         context = this;
    }

    private void initUI() {

        pengajuan = (RelativeLayout) findViewById(R.id.ll_pengajuan);
        pembelian = (RelativeLayout) findViewById(R.id.ll_pembelian);
        history = (RelativeLayout) findViewById(R.id.ll_history);

        initIvent();


    }

    private void initIvent() {
        pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActPengajuanDeposit.class);
                intent.putExtra("perdana","1");
                startActivity(intent);
            }
        });






                }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
        onBackPressed();
        return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
