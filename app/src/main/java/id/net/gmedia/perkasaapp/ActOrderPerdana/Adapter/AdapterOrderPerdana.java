package id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderPerdana.ActivityOrderPerdana2;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderPerdana extends RecyclerView.Adapter<AdapterOrderPerdana.PerdanaViewHolder> {

    private List<ModelOutlet> perdanaList;

    public AdapterOrderPerdana(List<ModelOutlet> mkiosList){
        this.perdanaList = mkiosList;
    }

    @NonNull
    @Override
    public PerdanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_perdana1, parent, false);
        return new PerdanaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PerdanaViewHolder holder, int position) {
        final ModelOutlet perdana = perdanaList.get(position);
        holder.txt_nama.setText(perdana.getNama());
        holder.txt_alamat.setText(perdana.getAlamat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ActivityOrderPerdana2.class);
                i.putExtra("perdana", perdana);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return perdanaList.size();
    }

    class PerdanaViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat;

        private PerdanaViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
        }
    }
}
