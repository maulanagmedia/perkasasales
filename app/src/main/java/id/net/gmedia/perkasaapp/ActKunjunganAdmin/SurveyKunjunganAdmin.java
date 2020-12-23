package id.net.gmedia.perkasaapp.ActKunjunganAdmin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.maulana.custommodul.SessionManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class SurveyKunjunganAdmin extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private WebView wvSurvey;
    private String kdcus = "";
    private String latitude = "", longitude = "";

    private static final String TAG = SurveyKunjunganAdmin.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_kunjungan_admin);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getSupportActionBar().setTitle("Survey Kunjungan Admin");
        }

        context = this;
        session = new SessionManager(context);
        initUI();
    }

    private void initUI() {

        wvSurvey = (WebView) findViewById(R.id.wv_survey);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            latitude = bundle.getString("lat", "");
            longitude = bundle.getString("long", "");
            //kdcus = "1004782";

            String url = String.format(ServerURL.getSurveyAdmin, kdcus, session.getNikGa(), latitude, longitude);
            //wvSurvey.getSettings().setJavaScriptEnabled(true);
            //wvSurvey.getSettings().setDomStorageEnabled(true);
            //wvSurvey.loadUrl(url);

            assert wvSurvey != null;
            WebSettings webSettings = wvSurvey.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);

            if(Build.VERSION.SDK_INT >= 21){
                webSettings.setMixedContentMode(0);
                wvSurvey.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }else if(Build.VERSION.SDK_INT >= 19){
                wvSurvey.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }else if(Build.VERSION.SDK_INT < 19){
                wvSurvey.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            try {
                Log.d(TAG, "Enabling HTML5-Features");
                Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                m1.invoke(webSettings, Boolean.TRUE);

                Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE});
                m2.invoke(webSettings, Boolean.TRUE);

                Method m3 = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                m3.invoke(webSettings, "/data/data/" + getPackageName() + "/databases/");

                Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE});
                m4.invoke(webSettings, 1024*1024*8);

                Method m5 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                m5.invoke(webSettings, "/data/data/" + getPackageName() + "/cache/");

                Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE});
                m6.invoke(webSettings, Boolean.TRUE);

                Log.d(TAG, "Enabled HTML5-Features");
            }
            catch (NoSuchMethodException e) {
                Log.e(TAG, "Reflection fail", e);
            }
            catch (InvocationTargetException e) {
                Log.e(TAG, "Reflection fail", e);
            }
            catch (IllegalAccessException e) {
                Log.e(TAG, "Reflection fail", e);
            }

            wvSurvey.setWebViewClient(new Callback());
            wvSurvey.loadUrl(url);
            wvSurvey.setWebChromeClient(new WebChromeClient(){

                //For Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    SurveyKunjunganAdmin.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
                }

                // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                public void openFileChooser(ValueCallback uploadMsg, String acceptType){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    SurveyKunjunganAdmin.this.startActivityForResult(
                            Intent.createChooser(i, "File Browser"),
                            FCR);
                }

                //For Android 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    SurveyKunjunganAdmin.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), SurveyKunjunganAdmin.FCR);
                }

                //For Android 5.0+
                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams){
                    if(mUMA != null){
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(SurveyKunjunganAdmin.this.getPackageManager()) != null){
                        File photoFile = null;
                        try{
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        }catch(IOException ex){
                            Log.e(TAG, "Image file creation failed", ex);
                        }
                        if(photoFile != null){
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }else{
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    Intent[] intentArray;
                    if(takePictureIntent != null){
                        intentArray = new Intent[]{takePictureIntent};
                    }else{
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(data == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = data.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(wvSurvey.canGoBack()){
                        wvSurvey.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
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
