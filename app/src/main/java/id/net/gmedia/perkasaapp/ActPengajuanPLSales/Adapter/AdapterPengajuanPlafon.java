package id.net.gmedia.perkasaapp.ActPengajuanPLSales.Adapter;

import android.content.Context;

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

import id.net.gmedia.perkasaapp.R;

public class AdapterPengajuanPlafon extends RecyclerView.Adapter<AdapterPengajuanPlafon.PiutangViewHolder> {

    private List<CustomItem> listOutlet;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterPengajuanPlafon(List<CustomItem> listOutlet, Context context){
        this.listOutlet = listOutlet;
        this.context = context;
    }

    @NonNull
    @Override
    public PiutangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajuan_pl, parent, false);
        return new PiutangViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PiutangViewHolder holder, int position) {

        final CustomItem outlet = listOutlet.get(position);

        holder.txt_nama.setText(outlet.getItem4());
        holder.txt_alamat.setText(iv.ChangeFormatDateString(outlet.getItem3(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay) + " "+ outlet.getItem2());
        holder.txt_piutang.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(outlet.getItem5())));
        holder.txtStatus.setText(outlet.getItem6());

    }

    @Override
    public int getItemCount() {
        return listOutlet.size();
    }


    class PiutangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_alamat, txt_piutang, txtStatus;
        private CardView cvContainer;

        private PiutangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txt_piutang = itemView.findViewById(R.id.txt_pengajuan);
            cvContainer = itemView.findViewById(R.id.cv_container);
        }
    }
}
