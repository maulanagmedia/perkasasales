package id.net.gmedia.perkasaapp.ActVerifikasiReseller.Adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ActVerifikasiReseller.ActivityVerifikasiOutlet2;
import id.net.gmedia.perkasaapp.ModelOutlet;
import id.net.gmedia.perkasaapp.R;

public class AdapterVerifikasiOutlet extends RecyclerView.Adapter<AdapterVerifikasiOutlet.VerifikasiOutletViewHolder> {

    private List<ModelOutlet> outletList;

    public AdapterVerifikasiOutlet(List<ModelOutlet> outletList){
        this.outletList = outletList;
    }

    @NonNull
    @Override
    public VerifikasiOutletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_outlet, parent, false);
        return new VerifikasiOutletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifikasiOutletViewHolder holder, int position) {
        final ModelOutlet outlet = outletList.get(position);

        holder.txt_nama.setText(outlet.getNama());
        holder.txt_alamat.setText(outlet.getAlamat());
        holder.txt_telepon.setText(outlet.getNomor());
        holder.txt_handphone.setText(outlet.getNomorHp());
        holder.txt_status.setText(R.string.status_proses);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityVerifikasiOutlet2.class);
                i.putExtra("outlet", outlet);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    class VerifikasiOutletViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat, txt_telepon, txt_handphone, txt_status;

        private VerifikasiOutletViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_telepon = itemView.findViewById(R.id.txt_telepon);
            txt_handphone = itemView.findViewById(R.id.txt_handphone);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }
}
