package id.net.gmedia.perkasaapp.ActChangePassword;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

public class ActChangePassword extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private SessionManager session;
    private EditText edtPasswordLama, edtPasswordBaru, edtRePassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_change_password);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ubah Password");
        }

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        initUI();
        initEvent();
    }

    private void initUI() {

        edtPasswordLama = (EditText) findViewById(R.id.edt_password_lama);
        edtPasswordBaru = (EditText) findViewById(R.id.edt_password_baru);
        edtRePassword = (EditText) findViewById(R.id.edt_ulangi_password_baru);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);
    }

    private void initEvent() {

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtPasswordLama.getText().toString().isEmpty()){

                    edtPasswordLama.setError("Mohon diisi");
                    edtPasswordLama.requestFocus();
                    return;
                }else{

                    edtPasswordLama.setError(null);
                }

                if(edtPasswordBaru.getText().toString().isEmpty()){

                    edtPasswordBaru.setError("Mohon diisi");
                    edtPasswordBaru.requestFocus();
                    return;
                }else{

                    edtPasswordBaru.setError(null);
                }

                if(edtRePassword.getText().toString().isEmpty()){

                    edtRePassword.setError("Mohon diisi");
                    edtRePassword.requestFocus();
                    return;
                }else{

                    edtRePassword.setError(null);
                }

                if(!edtRePassword.getText().toString().equals(edtPasswordBaru.getText().toString())){

                    edtRePassword.setError("Password baru tidak sama");
                    edtRePassword.requestFocus();
                    return;
                }else{

                    edtRePassword.setError(null);
                }

                if(edtPasswordLama.getText().toString().equals(edtPasswordBaru.getText().toString())){

                    edtPasswordBaru.setError("Harap jangan menggunakan password yang sama");
                    edtPasswordBaru.requestFocus();
                    return;
                }else{

                    edtPasswordBaru.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin mengubah password?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                savePassword();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    private void savePassword() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("password_lama", edtPasswordLama.getText().toString());
            jBody.put("password_baru", edtPasswordBaru.getText().toString());
            jBody.put("re_password", edtRePassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.savePassword, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, harap ulangi";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        session.savePassword(iv.encodeBase64(edtPasswordBaru.getText().toString()));
                        DialogBox.showDialog(context, DialogBox.TAG_SUCCESS, message);
                    }else{

                        DialogBox.showDialog(context, DialogBox.TAG_WARNING, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            savePassword();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        savePassword();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
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
