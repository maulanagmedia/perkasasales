package id.net.gmedia.perkasaapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ActivityStokPerdana extends AppCompatActivity {

    private List<ModelPerdana> listPerdana = new ArrayList<>();
    private List<ModelNota> listNota = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_perdana);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Stok Perdana");
        }

        RecyclerView rcy_stok = findViewById(R.id.rcy_stok);
        AdapterStokPerdana adapter = new AdapterStokPerdana(this, listPerdana, listNota);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcy_stok.setLayoutManager(layoutManager);
        rcy_stok.setItemAnimator(new DefaultItemAnimator());
        rcy_stok.setAdapter(adapter);

        initPerdana();
        initTransaksi();
    }

    private void initTransaksi(){
        ModelNota nota = new ModelNota("PD001");
        nota.addTransaksi(new ModelTransaksi("128k 4G LTE 2FF/3FF USIM MIGRATION (S097)", 20));
        nota.addTransaksi(new ModelTransaksi("SIMPATI PREMIUM 50K (S087)", 22));
        listNota.add(nota);

        nota = new ModelNota("PD002");
        nota.addTransaksi(new ModelTransaksi("PERDANA KARTU AS PANTURA 5000 (A082)", 50));
        nota.addTransaksi(new ModelTransaksi("PERDANA KARTU AS 5000 NEW (A069)", 40));
        listNota.add(nota);

        nota = new ModelNota("PD003");
        nota.addTransaksi(new ModelTransaksi("PERDANA KARTU AS 5000 (A067)", 20));
        nota.addTransaksi(new ModelTransaksi("SIMPATI PREMIUM 50K (S087)", 100));
        listNota.add(nota);

        nota = new ModelNota("PD004");
        nota.addTransaksi(new ModelTransaksi("PERDANA KARTU AS 5000 (A067)", 60));
        nota.addTransaksi(new ModelTransaksi("PERDANA KARTU AS 5000 NEW (A069)", 50));
        nota.addTransaksi(new ModelTransaksi("128k 4G LTE 2FF/3FF USIM MIGRATION (S097)", 80));
        listNota.add(nota);

        nota = new ModelNota("PD005");
        nota.addTransaksi(new ModelTransaksi("SIMPATI PREMIUM 50K (S087)", 75));
        nota.addTransaksi(new ModelTransaksi("LOOP 2GB 1 BLN", 120));
        listNota.add(nota);

        nota = new ModelNota("PD006");
        nota.addTransaksi(new ModelTransaksi("LOOP 2GB 1 BLN", 100));
        listNota.add(nota);
    }

    private void initPerdana(){
        ModelPerdana perdana = new ModelPerdana("128k 4G LTE 2FF/3FF USIM MIGRATION (S097)", 4200);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SPV 32K SIMPATI 10000 NEW (S087)", 2300);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SIMPATI PREMIUM 50K (S087)", 1220);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("SIMPATI PREMIUM 50K NEW (S089)", 1750);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS 5000 (A067)", 2120);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS PANTURA 5000 (A082)", 1010);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("PERDANA KARTU AS 5000 NEW (A069)", 2450);
        listPerdana.add(perdana);

        perdana = new ModelPerdana("LOOP 2GB 1 BLN", 1520);
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
