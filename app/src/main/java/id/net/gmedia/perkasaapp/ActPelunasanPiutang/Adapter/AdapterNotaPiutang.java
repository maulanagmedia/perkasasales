package id.net.gmedia.perkasaapp.ActPelunasanPiutang.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.ActPelunasanPiutang.DetailPelunasanPiutang;
import id.net.gmedia.perkasaapp.R;

public class AdapterNotaPiutang extends RecyclerView.Adapter<AdapterNotaPiutang.MineViewHolder> {

    private List<CustomItem> listItems;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterNotaPiutang(Context context, List<CustomItem> listItems){
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nota_piutang, parent, false);
        return new MineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MineViewHolder holder, final int position) {

        final CustomItem item = listItems.get(position);

        holder.cbItem1.setText(item.getItem2());
        holder.tvItem1.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getItem3())));
        holder.tvItem2.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatDate, FormatItem.formatDateDisplay));

        holder.cbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    listItems.get(position).setItem4("1");
                }else{
                    listItems.get(position).setItem4("0");
                }

                ((DetailPelunasanPiutang)context).getTotal();
            }
        });

        if(item.getItem4().equals("1")){

            holder.cbItem1.setChecked(true);
        }else{

            holder.cbItem1.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class MineViewHolder extends RecyclerView.ViewHolder{

        private CheckBox cbItem1;
        private TextView tvItem1, tvItem2;

        private MineViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem1 = itemView.findViewById(R.id.cb_item1);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
        }
    }
}
