package id.net.gmedia.perkasaapp.ActPelunasanPiutang.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActPelunasanPiutang.DetailPelunasanPiutang;
import id.net.gmedia.perkasaapp.R;

public class AdapterPiutang extends RecyclerView.Adapter<AdapterPiutang.PiutangViewHolder> {

    private List<CustomItem> listOutlet;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterPiutang(List<CustomItem> listOutlet, Context context){
        this.listOutlet = listOutlet;
        this.context = context;
    }

    @NonNull
    @Override
    public PiutangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_piutang, parent, false);
        return new PiutangViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PiutangViewHolder holder, int position) {

        final CustomItem outlet = listOutlet.get(position);
        holder.txt_nama.setText(outlet.getItem2());
        holder.txt_alamat.setText(iv.ChangeFormatDateString(outlet.getItem3(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        holder.txt_piutang.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(outlet.getItem5())));

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailPelunasanPiutang.class);
                intent.putExtra("id", outlet.getItem1());
                intent.putExtra("kdcus", outlet.getItem6());
                intent.putExtra("nama", outlet.getItem2());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOutlet.size();
    }


    class PiutangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat, txt_piutang;
        private CardView cvContainer;

        private PiutangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_piutang = itemView.findViewById(R.id.txt_piutang);
            cvContainer = itemView.findViewById(R.id.cv_container);
        }
    }
}
