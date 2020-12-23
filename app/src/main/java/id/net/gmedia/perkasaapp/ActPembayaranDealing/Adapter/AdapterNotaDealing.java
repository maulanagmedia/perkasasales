package id.net.gmedia.perkasaapp.ActPembayaranDealing.Adapter;

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

import id.net.gmedia.perkasaapp.ActPembayaranDealing.DetailPembayaranDealing;
import id.net.gmedia.perkasaapp.R;

public class AdapterNotaDealing extends RecyclerView.Adapter<AdapterNotaDealing.MineViewHolder> {

    private List<CustomItem> listItems;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterNotaDealing(Context context, List<CustomItem> listItems){
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nota_dealing, parent, false);
        return new MineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MineViewHolder holder, final int position) {

        final CustomItem item = listItems.get(position);

        holder.cbItem1.setText(item.getItem2());
        holder.tvItem1.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getItem3())));
        holder.tvItem2.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatDate, FormatItem.formatDateDisplay));

        holder.cbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                double totalBayar = iv.parseNullDouble(((DetailPembayaranDealing)context).edtPembayaran.getText().toString().replaceAll("[,.]","" ));

                if(totalBayar > 0){

                    double sisa = ((DetailPembayaranDealing)context).sisa;

                    // kalau sudah tidak ada sisa
                    if(sisa == 0){

                        if(b){

                            holder.cbItem1.setSelected(false);
                            ((DetailPembayaranDealing) context).getTotal();
                        }else{

                            String terbayar = "0";
                            listItems.get(position).setItem4("0");
                            listItems.get(position).setItem6(terbayar);
                            holder.tvItem3.setText(iv.ChangeToCurrencyFormat(terbayar));
                            ((DetailPembayaranDealing)context).getTotal();
                        }
                    }else{

                        String terbayar =  "0";

                        if(b){

                            if(sisa > iv.parseNullDouble(item.getItem3())){

                                terbayar = item.getItem3();

                            }else{
                                terbayar = iv.doubleToString(sisa);
                            }

                            listItems.get(position).setItem4("1");
                        }else{

                            terbayar =  "0";
                            listItems.get(position).setItem4("0");
                        }

                        listItems.get(position).setItem6(terbayar);
                        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(terbayar));
                        ((DetailPembayaranDealing)context).getTotal();
                    }
                }else{

                    listItems.get(position).setItem4("0");
                    holder.cbItem1.setSelected(false);
                    ((DetailPembayaranDealing) context).getTotal();
                }
            }
        });

        if(item.getItem4().equals("1")){

            holder.cbItem1.setChecked(true);
        }else{

            holder.cbItem1.setChecked(false);
        }

        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(item.getItem6()));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class MineViewHolder extends RecyclerView.ViewHolder{

        private CheckBox cbItem1;
        private TextView tvItem1, tvItem2, tvItem3;

        private MineViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem1 = itemView.findViewById(R.id.cb_item1);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
            tvItem3 = itemView.findViewById(R.id.tv_item3);
        }
    }
}
