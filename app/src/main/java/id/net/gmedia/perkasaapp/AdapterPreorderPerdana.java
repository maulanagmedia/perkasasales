package id.net.gmedia.perkasaapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterPreorderPerdana extends RecyclerView.Adapter<AdapterPreorderPerdana.PreorderPerdanaViewHolder> {

    private List<ModelPerdana> listPerdana;

    AdapterPreorderPerdana(List<ModelPerdana> listPerdana){
        this.listPerdana = listPerdana;
    }

    @NonNull
    @Override
    public PreorderPerdanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preorder_perdana, parent, false);
        return new PreorderPerdanaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreorderPerdanaViewHolder holder, final int position) {
        final ModelPerdana perdana = listPerdana.get(position);

        holder.txt_nama.setText(perdana.getNama());
        holder.txt_harga.setText(RupiahFormatterUtil.getRupiah(perdana.getHarga()));
        holder.txt_stok.setText(String.valueOf(perdana.getStok()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityPreorderPerdana3.class);
                i.putExtra("perdana", perdana);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPerdana.size();
    }

    class PreorderPerdanaViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_harga, txt_stok;

        private PreorderPerdanaViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
