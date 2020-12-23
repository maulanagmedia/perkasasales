package id.net.gmedia.perkasaapp.ActVerifikasiReseller;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import id.net.gmedia.perkasaapp.CustomMapView;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;

public class ActivityVerifikasiOutlet2 extends AppCompatActivity implements OnMapReadyCallback {

    private ModelOutlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_outlet2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Verifikasi Outlet");
        }

        TextView txt_nama, txt_alamat, txt_nomor, txt_nomorhp;
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_nomor = findViewById(R.id.txt_nomor);
        txt_nomorhp = findViewById(R.id.txt_nomorhp);

        if(getIntent().hasExtra("outlet")){
            outlet = getIntent().getParcelableExtra("outlet");
            txt_nama.setText(outlet.getNama());
            txt_alamat.setText(outlet.getAlamat());
            txt_nomor.setText(outlet.getNomor());
            txt_nomorhp.setText(outlet.getNomorHp());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final ScrollView layout_scroll = findViewById(R.id.layout_scroll);
        ((CustomMapView) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new CustomMapView.OnTouchListener() {
            @Override
            public void onTouch() {
                layout_scroll.requestDisallowInterceptTouchEvent(true);
            }
        });

        LatLng roominc = new LatLng(outlet.getLatitude(),outlet.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(roominc).title("Posisi Outlet"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(roominc));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);
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
