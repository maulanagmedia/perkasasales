package id.net.gmedia.perkasaapp.ActOrderTcash.Adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderTcash.ActivityOrderTcash3;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderTcash extends RecyclerView.Adapter<AdapterOrderTcash.TcashViewHolder> {

    private List<ModelOutlet> outletList;

    public AdapterOrderTcash(List<ModelOutlet> outletList){
        this.outletList = outletList;
    }

    @NonNull
    @Override
    public AdapterOrderTcash.TcashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_mkios, parent, false);
        return new AdapterOrderTcash.TcashViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderTcash.TcashViewHolder holder, int position) {
        final ModelOutlet tcash = outletList.get(position);
        holder.txt_nama.setText(tcash.getNama());
        holder.txt_no_reseller.setText(tcash.getNomor());
        holder.txt_tgl_order.setText("");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ActivityOrderTcash3.class);
                i.putExtra("tcash", tcash);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    class TcashViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_no_reseller, txt_tgl_order;

        private TcashViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_no_reseller = itemView.findViewById(R.id.txt_no_reseller);
            txt_tgl_order = itemView.findViewById(R.id.txt_tgl_order);
        }
    }
}
