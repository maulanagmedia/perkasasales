package id.net.gmedia.perkasaapp.ActOrderMkios.Adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderMkios.ActivityOrderMkios2;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderMkios extends RecyclerView.Adapter<AdapterOrderMkios.MkiosViewHolder> {

    private List<ModelOutlet> mkiosList;

    public AdapterOrderMkios(List<ModelOutlet> mkiosList){
        this.mkiosList = mkiosList;
    }

    @NonNull
    @Override
    public MkiosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_mkios, parent, false);
        return new MkiosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MkiosViewHolder holder, int position) {
        final ModelOutlet mkios = mkiosList.get(position);
        holder.txt_nama.setText(mkios.getNama());
        holder.txt_no_reseller.setText(mkios.getNomor());
        holder.txt_tgl_order.setText("");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ActivityOrderMkios2.class);
                i.putExtra("mkios", mkios);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mkiosList.size();
    }

    class MkiosViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_no_reseller, txt_tgl_order;

        private MkiosViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_no_reseller = itemView.findViewById(R.id.txt_no_reseller);
            txt_tgl_order = itemView.findViewById(R.id.txt_tgl_order);
        }
    }
}
