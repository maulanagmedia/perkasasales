package id.net.gmedia.perkasaapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterKomplain extends RecyclerView.Adapter<AdapterKomplain.KomplainViewHolder> {

    private List<ModelKomplain> listKomplain;

    AdapterKomplain(List<ModelKomplain> listKomplain){
        this.listKomplain = listKomplain;
    }

    @NonNull
    @Override
    public KomplainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_komplain, parent, false);
        return new KomplainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KomplainViewHolder holder, int position) {
        ModelKomplain komplain = listKomplain.get(position);

        holder.txt_nama.setText(komplain.getNama());
        holder.txt_komplain.setText(komplain.getKomplain());
    }

    @Override
    public int getItemCount() {
        return listKomplain.size();
    }


    class KomplainViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_nama, txt_komplain;

        private KomplainViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_komplain = itemView.findViewById(R.id.txt_komplain);
        }
    }
}
