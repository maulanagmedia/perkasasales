package id.net.gmedia.perkasaapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class AdapterLokasiOutlet extends RecyclerView.Adapter<AdapterLokasiOutlet.LokasiOutletViewHolder> {

    private List<ModelOutlet> listOutlet;

    AdapterLokasiOutlet(List<ModelOutlet> listOutlet){
        this.listOutlet = listOutlet;
    }

    @NonNull
    @Override
    public LokasiOutletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lokasi_outlet, parent, false);
        return new LokasiOutletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LokasiOutletViewHolder holder, int position) {
        final ModelOutlet outlet = listOutlet.get(position);

        holder.txt_nama.setText(outlet.getNama());
        holder.txt_alamat.setText(outlet.getAlamat());
        holder.txt_kontak.setText(outlet.getNomor());
        holder.txt_lokasi.setText(String.format(Locale.getDefault(), "%f, %f", outlet.getLatitude(), outlet.getLongitude()));

        holder.btn_lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityLokasiOutlet2.class);
                i.putExtra("outlet", outlet);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOutlet.size();
    }


    class LokasiOutletViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat, txt_kontak, txt_lokasi;
        private TextView btn_lokasi;
        private LokasiOutletViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_kontak = itemView.findViewById(R.id.txt_kontak);
            txt_lokasi = itemView.findViewById(R.id.txt_lokasi);
            btn_lokasi = itemView.findViewById(R.id.btn_lokasi);
        }
    }
}
