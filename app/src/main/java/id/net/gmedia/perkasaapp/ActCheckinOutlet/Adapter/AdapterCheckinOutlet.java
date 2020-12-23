package id.net.gmedia.perkasaapp.ActCheckinOutlet.Adapter;

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

public class AdapterCheckinOutlet extends RecyclerView.Adapter<AdapterCheckinOutlet.CheckinViewHolder> {

    private List<CustomItem> listData;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterCheckinOutlet(List<CustomItem> listData, Context context){
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkin_outlet, parent, false);
        return new CheckinViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckinViewHolder holder, int position) {

        final CustomItem outlet = listData.get(position);
        holder.tvItem1.setText(outlet.getItem2());
        holder.tvItem2.setText(outlet.getItem3());
        holder.tvItem3.setText(iv.ChangeFormatDateString(outlet.getItem4(), FormatItem.formatTimestamp, FormatItem.formatTime));
        holder.tvItem4.setText(iv.ChangeFormatDateString(outlet.getItem5(), FormatItem.formatTimestamp, FormatItem.formatTime));
        holder.tvItem5.setText(iv.ChangeFormatDateString(outlet.getItem4(), FormatItem.formatTimestamp, FormatItem.formatDateDisplay));

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class CheckinViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
        private CardView cvContainer;

        private CheckinViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
            tvItem3 = itemView.findViewById(R.id.tv_item3);
            tvItem4 = itemView.findViewById(R.id.tv_item4);
            tvItem5 = itemView.findViewById(R.id.tv_item5);
            cvContainer = itemView.findViewById(R.id.cv_container);
        }
    }
}
