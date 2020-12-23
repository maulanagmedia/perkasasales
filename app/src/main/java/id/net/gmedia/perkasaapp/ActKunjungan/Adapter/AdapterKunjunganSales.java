package id.net.gmedia.perkasaapp.ActKunjungan.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.perkasaapp.ModelSales;
import id.net.gmedia.perkasaapp.R;

public class AdapterKunjunganSales extends RecyclerView.Adapter<AdapterKunjunganSales.KunjunganSalesViewHolder> {

    private List<ModelSales> listSales;

    public AdapterKunjunganSales(List<ModelSales> listSales){
        this.listSales = listSales;
    }

    @NonNull
    @Override
    public KunjunganSalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kunjungan_sales, parent, false);
        return new KunjunganSalesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KunjunganSalesViewHolder holder, int position) {
        ModelSales sales = listSales.get(position);

        holder.txt_nama.setText(sales.getNama());
        holder.txt_jumlah.setText(String.format(Locale.getDefault(), "%d outlet yang telah dikunjungi", sales.getJumlah()));
    }

    @Override
    public int getItemCount() {
        return listSales.size();
    }

    class KunjunganSalesViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_jumlah;

        private KunjunganSalesViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
        }
    }
}
