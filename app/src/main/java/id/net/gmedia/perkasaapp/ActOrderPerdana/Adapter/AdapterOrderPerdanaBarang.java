package id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderPerdana.ActivityOrderPerdana3;
import id.net.gmedia.perkasaapp.ModelPerdana;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderPerdanaBarang extends RecyclerView.Adapter<AdapterOrderPerdanaBarang.OrderPerdanaBarangViewHolder> {

    private List<ModelPerdana> listPerdana;
    private String kdcus;
    private ItemValidation iv = new ItemValidation();

    public AdapterOrderPerdanaBarang(List<ModelPerdana> listPerdana, String kdcus){
        this.listPerdana = listPerdana;
        this.kdcus = kdcus;
    }

    @NonNull
    @Override
    public OrderPerdanaBarangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_perdana2, parent, false);
        return new OrderPerdanaBarangViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderPerdanaBarangViewHolder holder, final int position) {
        final ModelPerdana perdana = listPerdana.get(position);

        holder.txt_nama.setText(perdana.getNama());
        holder.txt_surat_jalan.setText(perdana.getSurat_jalan());
        holder.txt_harga.setText(iv.ChangeToRupiahFormat(perdana.getHargaString()));
        holder.txt_stok.setText(iv.ChangeToCurrencyFormat(perdana.getStokString()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityOrderPerdana3.class);
                i.putExtra("kdbrg", perdana.getKodebrg());
                i.putExtra("namabrg", perdana.getNama());
                i.putExtra("harga", perdana.getHargaString());
                i.putExtra("suratjalan", perdana.getSurat_jalan());
                i.putExtra("kdcus", kdcus);
                i.putExtra("jenis_barang", perdana.getTipeProgram());
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPerdana.size();
    }

    class OrderPerdanaBarangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_surat_jalan, txt_harga, txt_stok;

        private OrderPerdanaBarangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_surat_jalan = itemView.findViewById(R.id.txt_surat_jalan);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
