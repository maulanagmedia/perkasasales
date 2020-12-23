package id.net.gmedia.perkasaapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLokasiOutletBelumVerifikasi extends Fragment {

    private List<ModelOutlet> listOutlet = new ArrayList<>();

    public FragmentLokasiOutletBelumVerifikasi() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lokasi_outlet_belum_verifikasi, container, false);

        RecyclerView rcy_outlet = view.findViewById(R.id.rcy_outlet);
        AdapterLokasiOutlet adapter = new AdapterLokasiOutlet(listOutlet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        rcy_outlet.setLayoutManager(layoutManager);
        rcy_outlet.setItemAnimator(new DefaultItemAnimator());
        rcy_outlet.setAdapter(adapter);

        initOutlet();

        return view;
    }

    private void initOutlet(){
        ModelOutlet outlet = new ModelOutlet("REDBERRI CELL", "jl. duren", "81215297768", "81215297768", -7.0213463,110.4270008);
        listOutlet.add(outlet);

        outlet = new ModelOutlet("DOI CELL", "lendoh", "81215297781", "81215297781", -7.0213463,110.4270008);
        listOutlet.add(outlet);
    }
}
