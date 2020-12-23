package id.net.gmedia.perkasaapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ActivityPreorderPerdana2 extends AppCompatActivity {

    List<ModelPerdana> listPerdana = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorder_perdana2);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("List Preorder Perdana");
        }

        RecyclerView rcy_stok = findViewById(R.id.rcy_perdana);
        AdapterPreorderPerdana adapter = new AdapterPreorderPerdana(listPerdana);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcy_stok.setLayoutManager(layoutManager);
        rcy_stok.setItemAnimator(new DefaultItemAnimator());
        rcy_stok.setAdapter(adapter);

        initPerdana();
    }

    private void initPerdana(){
        ModelPerdana perdana = new ModelPerdana("128k 4G LTE 2FF/3FF USIM MIGRATION (S097)", 52000, 4200);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SPV 32K SIMPATI 10000 NEW (S087)", 12000, 2300);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SIMPATI PREMIUM 50K (S087)", 51000, 1220);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SIMPATI PREMIUM 50K NEW (S089)", 51000, 1750);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS 5000 (A067)", 6500, 2120);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS PANTURA 5000 (A082)", 6500, 1010);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS 5000 NEW (A069)", 6500, 2450);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("LOOP 2GB 1 BLN", 61000, 1520);
        listPerdana.add(perdana);
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
