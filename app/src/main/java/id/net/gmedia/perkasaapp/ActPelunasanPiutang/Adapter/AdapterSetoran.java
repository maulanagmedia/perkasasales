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
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActPelunasanPiutang.DetailNotaTotalSetoran;
import id.net.gmedia.perkasaapp.R;

public class AdapterSetoran extends RecyclerView.Adapter<AdapterSetoran.SetoranViewHolder> {

    private List<CustomItem> listItems;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterSetoran(List<CustomItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public SetoranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setoran, parent, false);
        return new SetoranViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SetoranViewHolder holder, int position) {

        final CustomItem setoran = listItems.get(position);
        holder.tvItem1.setText(setoran.getItem2() + " " + (setoran.getItem3().isEmpty() ? "": "- " + setoran.getItem3()));
        holder.tvItem2.setText(iv.ChangeToRupiahFormat(setoran.getItem4()));

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailNotaTotalSetoran.class);
                intent.putExtra("kodeakun", setoran.getItem1());
                intent.putExtra("start", setoran.getItem5());
                intent.putExtra("end", setoran.getItem6());
                intent.putExtra("title", setoran.getItem2());
                intent.putExtra("subtitle", setoran.getItem3());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    class SetoranViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem1, tvItem2;
        private CardView cvContainer;

        private SetoranViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
            cvContainer = (CardView) itemView.findViewById(R.id.cv_container);
        }
    }
}
