package id.net.gmedia.perkasaapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterStokPerdana extends RecyclerView.Adapter<AdapterStokPerdana.PerdanaViewHolder> {

    private List<ModelPerdana> listPerdana;
    private List<ModelNota> listNota;
    private Context context;

    AdapterStokPerdana(Context context, List<ModelPerdana> listPerdana, List<ModelNota> listNota){
        this.context = context;
        this.listPerdana = listPerdana;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public PerdanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stok_perdana, parent, false);
        return new PerdanaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PerdanaViewHolder holder, int position) {
        ModelPerdana perdana = listPerdana.get(position);
        holder.txt_nama.setText(perdana.getNama());
        holder.txt_sisa.setText(String.valueOf(perdana.getStok()));

        List<String> listNotaId = new ArrayList<>();
        List<Integer> listTerjual = new ArrayList<>();

        AdapterStokPerdanaNota adapter = new AdapterStokPerdanaNota(listNotaId, listTerjual);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.rcy_nota.setLayoutManager(layoutManager);
        holder.rcy_nota.setItemAnimator(new DefaultItemAnimator());
        holder.rcy_nota.setAdapter(adapter);

        //Recycler View inisialisasi transaksi yang terkait perdana
        for(ModelNota n : listNota){
            for(ModelTransaksi t : n.getListTransaksi()){
                if(t.getNama().equals(perdana.getNama())){
                    listNotaId.add(n.getId());
                    listTerjual.add(t.getJumlah());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listPerdana.size();
    }

    class PerdanaViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_sisa;
        private RecyclerView rcy_nota;

        private PerdanaViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_sisa = itemView.findViewById(R.id.txt_sisa);
            rcy_nota = itemView.findViewById(R.id.rcy_nota);
        }
    }
}
