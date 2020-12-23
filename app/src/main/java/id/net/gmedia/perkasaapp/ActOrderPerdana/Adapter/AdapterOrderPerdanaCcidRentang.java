package id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ModelCcid;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderPerdanaCcidRentang extends RecyclerView.Adapter<AdapterOrderPerdanaCcidRentang.OrderPerdanaCcidRentangViewHolder> {

    private List<ModelCcid> listCcid;

    public AdapterOrderPerdanaCcidRentang(List<ModelCcid> listCcid){
        this.listCcid = listCcid;
    }

    @NonNull
    @Override
    public OrderPerdanaCcidRentangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_perdana3_rentang, parent, false);
        return new OrderPerdanaCcidRentangViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderPerdanaCcidRentangViewHolder holder, int position) {
        final ModelCcid ccid = listCcid.get(position);

        holder.txt_no_ccid.setText(String.valueOf(position + 1));
        holder.txt_nama_ccid.setText(ccid.getCcid());
    }

    @Override
    public int getItemCount() {
        return listCcid.size();
    }

    class OrderPerdanaCcidRentangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_no_ccid, txt_nama_ccid;
        private OrderPerdanaCcidRentangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_no_ccid = itemView.findViewById(R.id.txt_no_ccid);
            txt_nama_ccid = itemView.findViewById(R.id.txt_nama_ccid);
        }
    }
}
