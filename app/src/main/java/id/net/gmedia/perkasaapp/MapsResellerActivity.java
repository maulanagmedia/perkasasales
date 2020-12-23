package id.net.gmedia.perkasaapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maulana.custommodul.ItemValidation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.net.gmedia.perkasaapp.CustomView.CustomMapView;

public class MapsResellerActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private GoogleMap googleMap;
    private ProgressBar pbLoading;
    private String latitude = "", longitude = "", latitudeOutlet = "", longitudeOutlet = "";
    private String nama = "", photo = "";
    private CustomMapView mvMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_reseller);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Peta Reseller");

        context = this;

        initUI();
    }

    private void  initUI(){

        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mvMap = (CustomMapView) findViewById(R.id.mv_map);
        mvMap.onCreate(null);
        mvMap.onResume();
        try {
            MapsInitializer.initialize(MapsResellerActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            latitude = bundle.getString("lat");
            longitude = bundle.getString("long");
            latitudeOutlet = bundle.getString("lat_outlet");
            longitudeOutlet = bundle.getString("long_outlet");
            nama = bundle.getString("nama");
            photo = bundle.getString("photo","");
            getSupportActionBar().setSubtitle(nama);

            setPointMap();
        }
    }

    private void setPointMap(){

        mvMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                googleMap.clear();

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Bitmap bmp = createUserBitmap();

                Marker markerS = googleMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(iv.getBitmapFromVectorDrawable(MapsResellerActivity.this, R.mipmap.ic_pin)))
                        .title("Anda")
                        .position(new LatLng(iv.parseNullDouble(latitude), iv.parseNullDouble(longitude))));

                Marker markerR = googleMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .position(new LatLng(iv.parseNullDouble(latitudeOutlet), iv.parseNullDouble(longitudeOutlet))));

                markerS.showInfoWindow();
                markerR.showInfoWindow();
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Please allow location access from your app permission", Toast.LENGTH_SHORT).show();
                    return;
                }

                //googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                MapsInitializer.initialize(context);
                LatLng position = new LatLng(iv.parseNullDouble(latitudeOutlet), iv.parseNullDouble(longitudeOutlet));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    private Bitmap createUserBitmap() {

        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_bg_maps);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user_black);

            if(!photo.isEmpty()){
                bitmap = bitmapFromUrl(photo);
            }

            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(10), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {}
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private Bitmap bitmapFromUrl(String src) {

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }

        /*Bitmap x;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return x;*/
    }

    private int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
}
