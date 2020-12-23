package id.net.gmedia.perkasaapp;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.perkasaapp.ActCustomer.ActivityTambahCustomer2;

public class AdapterTambahCustomer extends RecyclerView.Adapter<AdapterTambahCustomer.TambahCustomerViewHolder> {

    private List<ModelOutlet> outletList;

    AdapterTambahCustomer(List<ModelOutlet> outletList){
        this.outletList = outletList;
    }

    @NonNull
    @Override
    public TambahCustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_outlet, parent, false);
        return new TambahCustomerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TambahCustomerViewHolder holder, int position) {
        final ModelOutlet outlet = outletList.get(position);

        holder.txt_nama.setText(outlet.getNama());
        holder.txt_alamat.setText(outlet.getAlamat());
        holder.txt_telepon.setText(outlet.getNomor());
        holder.txt_handphone.setText(outlet.getNomorHp());
        holder.txt_status.setText(R.string.status_aktif);
        holder.txt_status.setTextColor(Color.BLUE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityTambahCustomer2.class);
                i.putExtra("outlet", outlet);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    class TambahCustomerViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat, txt_telepon, txt_handphone, txt_status;

        private TambahCustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_telepon = itemView.findViewById(R.id.txt_telepon);
            txt_handphone = itemView.findViewById(R.id.txt_handphone);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }
}
