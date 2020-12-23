package id.net.gmedia.perkasaapp.ActOrderMkios.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActOrderMkios.ActivityOrderMkios2;
import id.net.gmedia.perkasaapp.ModelPulsa;
import id.net.gmedia.perkasaapp.R;

public class AdapterOrderMkios2 extends RecyclerView.Adapter<AdapterOrderMkios2.OrderMkios2ViewHolder> {

    private List<ModelPulsa> listPulsa;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterOrderMkios2(List<ModelPulsa> listPulsa, Context context) {
        this.listPulsa = listPulsa;
        this.context = context;
    }

    public List<ModelPulsa> getItems(){
        return listPulsa;
    }

    @NonNull
    @Override
    public OrderMkios2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_mkios2, parent, false);
        return new OrderMkios2ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderMkios2ViewHolder holder, final int position) {
        final int current = position;
        final ModelPulsa pulsa = listPulsa.get(position);

        if(pulsa.getPulsaString().equals("Bulk")){
            holder.txt_nama_large.setText(R.string.bulk);
            holder.txt_harga.setText(iv.doubleToString(100 - iv.parseNullDouble(pulsa.getHargaString())) + "%");
        }
        else
        {
            holder.txt_nama_large.setText(pulsa.getPulsaString());
            holder.txt_harga.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(pulsa.getHargaString())));
        }

        holder.txt_jumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals("")){

                    double totalHarga = 0;
                    if(pulsa.getPulsaString().equals("Bulk")){

                        totalHarga = (100 - iv.parseNullDouble(pulsa.getHargaString())) / 100 * iv.parseNullDouble(s.toString());
                    }else{

                        totalHarga = iv.parseNullDouble(s.toString()) * iv.parseNullDouble(pulsa.getHargaString());
                    }

                    holder.txt_total.setText(iv.ChangeToRupiahFormat(totalHarga));
                    listPulsa.get(position).setJumlah(s.toString());
                    listPulsa.get(position).setTotalHarga(iv.doubleToString(totalHarga));
                }
                else{
                    holder.txt_total.setText(iv.ChangeToRupiahFormat((double) 0));
                    listPulsa.get(position).setJumlah("0");
                    listPulsa.get(position).setTotalHarga("0");
                }

                ((ActivityOrderMkios2) context).updateHarga();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPulsa.size();
    }

    class OrderMkios2ViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama_large, txt_jumlah, txt_harga, txt_total;

        private OrderMkios2ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama_large = itemView.findViewById(R.id.txt_nama_large);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }
}
