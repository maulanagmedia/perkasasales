package id.net.gmedia.perkasaapp;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ActivityKomplain1 extends AppCompatActivity {

    private List<ModelKomplain> listKomplain = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Komplain");
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityKomplain1.this, ActivityKomplain2.class));
            }
        });

        RecyclerView rcy_komplain = findViewById(R.id.rcy_komplain);
        AdapterKomplain adapter = new AdapterKomplain(listKomplain);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcy_komplain.setLayoutManager(layoutManager);
        rcy_komplain.setItemAnimator(new DefaultItemAnimator());
        rcy_komplain.setAdapter(adapter);

        initKomplain();
    }

    private void initKomplain(){
        //Inisialisasi item Recycler View Komplain
        ModelKomplain komplain = new ModelKomplain("Andi Kusworo", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        listKomplain.add(komplain);
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
