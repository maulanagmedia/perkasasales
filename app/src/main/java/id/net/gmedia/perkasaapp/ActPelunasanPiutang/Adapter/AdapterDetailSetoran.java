package id.net.gmedia.perkasaapp.ActPelunasanPiutang.Adapter;

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

public class AdapterDetailSetoran extends RecyclerView.Adapter<AdapterDetailSetoran.SetoranViewHolder> {

    private List<CustomItem> listItems;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterDetailSetoran(List<CustomItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public SetoranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_setoran, parent, false);
        return new SetoranViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SetoranViewHolder holder, int position) {

        final CustomItem setoran = listItems.get(position);
        holder.tvItem1.setText(iv.ChangeFormatDateString(setoran.getItem2(), FormatItem.formatDate, FormatItem.formatDateDisplay) + " " + (setoran.getItem4().isEmpty() ? "": "(" + setoran.getItem4() + ")"));
        holder.tvItem2.setText(setoran.getItem3());
        holder.tvItem3.setText(iv.ChangeToRupiahFormat(setoran.getItem5()));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    class SetoranViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem1, tvItem2, tvItem3;
        private CardView cvContainer;

        private SetoranViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
            tvItem3 = itemView.findViewById(R.id.tv_item3);
            cvContainer = (CardView) itemView.findViewById(R.id.cv_container);
        }
    }
}
