package id.net.gmedia.perkasaapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityLokasiOutlet2 extends AppCompatActivity implements OnMapReadyCallback{

    private ModelOutlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi_outlet2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Verifikasi Outlet");
        }

        TextView txt_nama, txt_alamat;
        EditText txt_latitude, txt_longitude;
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_latitude = findViewById(R.id.txt_latitude);
        txt_longitude = findViewById(R.id.txt_longitude);

        if(getIntent().hasExtra("outlet")){
            outlet = getIntent().getParcelableExtra("outlet");
            txt_nama.setText(outlet.getNama());
            txt_alamat.setText(outlet.getAlamat());
            txt_latitude.setText(String.valueOf(outlet.getLatitude()));
            txt_longitude.setText(String.valueOf(outlet.getLongitude()));
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
