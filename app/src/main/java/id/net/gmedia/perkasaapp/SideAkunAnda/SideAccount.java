package id.net.gmedia.perkasaapp.SideAkunAnda;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import id.net.gmedia.perkasaapp.R;

public class SideAccount extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private SessionManager session;
    private TextView tvNama, tvNikGA, tvMKIOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_account);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Akun Anda");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvNikGA = (TextView) findViewById(R.id.tv_nik_ga);
        tvMKIOS = (TextView) findViewById(R.id.tv_nik_mkios);

        tvNama.setText(session.getNama());
        tvNikGA.setText(session.getNikGa());
        tvMKIOS.setText(session.getNikMkios());
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
