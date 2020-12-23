package id.net.gmedia.perkasaapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterStokPerdanaNota extends RecyclerView.Adapter<AdapterStokPerdanaNota.StokPerdanaNotaViewHolder> {

    private List<String> listNotaId;
    private  List<Integer> listTerjual;

    AdapterStokPerdanaNota(List<String> listNotaId, List<Integer> listTerjual){
        this.listNotaId = listNotaId;
        this.listTerjual = listTerjual;
    }

    @NonNull
    @Override
    public StokPerdanaNotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stok_perdana_nota, parent, false);
        return new StokPerdanaNotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StokPerdanaNotaViewHolder holder, int position) {
        holder.txt_nama.setText(listNotaId.get(position));
        holder.txt_terjual.setText(String.valueOf(listTerjual.get(position)));
    }

    @Override
    public int getItemCount() {
        return listNotaId.size();
    }


    class StokPerdanaNotaViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_terjual;

        private StokPerdanaNotaViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_terjual = itemView.findViewById(R.id.txt_terjual);
        }
    }
}
