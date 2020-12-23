package id.net.gmedia.perkasaapp.ActPengajuanPLSales.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActPengajuanPLSales.DetailApprovalPL;
import id.net.gmedia.perkasaapp.R;

public class AdapterApprovalPlafon extends RecyclerView.Adapter<AdapterApprovalPlafon.PiutangViewHolder> {

    private List<CustomItem> listOutlet;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterApprovalPlafon(List<CustomItem> listOutlet, Context context){
        this.listOutlet = listOutlet;
        this.context = context;
    }

    @NonNull
    @Override
    public PiutangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval_pl, parent, false);
        return new PiutangViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PiutangViewHolder holder, int position) {

        final CustomItem item = listOutlet.get(position);

        holder.txtNama.setText(item.getItem4());
        holder.txtTgl.setText(iv.ChangeFormatDateString(item.getItem8(), FormatItem.formatDate, FormatItem.formatDateDisplay) + " s/d "+ iv.ChangeFormatDateString(item.getItem9(), FormatItem.formatDate, FormatItem.formatDateDisplay));
        holder.txtPengajuan.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getItem5())));
        holder.txtStatus.setText(item.getItem6());
        holder.txtPengaju.setText(item.getItem7());

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(item.getItem11().equals("1")){

                    Intent intent = new Intent(context, DetailApprovalPL.class);
                    //intent.putExtra("id", outlet.getItem1());
                    DetailApprovalPL.item = item;
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Data yang telah diupdate tidak dapat diubah", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOutlet.size();
    }


    class PiutangViewHolder extends RecyclerView.ViewHolder{

        private TextView txtNama, txtTgl, txtPengajuan, txtPengaju, txtStatus;
        private CardView cvContainer;

        private PiutangViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txt_nama);
            txtTgl = itemView.findViewById(R.id.txt_tgl);
            txtPengaju = itemView.findViewById(R.id.txt_pengaju);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtPengajuan = itemView.findViewById(R.id.txt_pengajuan);
            cvContainer = itemView.findViewById(R.id.cv_container);
        }
    }
}
