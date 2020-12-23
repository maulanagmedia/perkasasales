package id.net.gmedia.perkasaapp.ActPengajuanRKP.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListPengajuanRKPAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();
    private int flag = 1;

    public ListPengajuanRKPAdapter(Activity context, List<CustomItem> items, int flag) {
        super(context, R.layout.adapter_pengajuan_rkp, items);
        this.context = context;
        this.items = items;
        this.flag = flag;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7, tvItem8;
        private LinearLayout llItem1;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_pengajuan_rkp, null);
            holder.llItem1 = (LinearLayout) convertView.findViewById(R.id.ll_item1);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = convertView.findViewById(R.id.tv_item6);
            holder.tvItem7 = convertView.findViewById(R.id.tv_item7);
            holder.tvItem8 = convertView.findViewById(R.id.tv_item8);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(flag == 0){

            holder.llItem1.setVisibility(View.GONE);
        }else{

            holder.llItem1.setVisibility(View.VISIBLE);
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem1());
        holder.tvItem2.setText(iv.ChangeFormatDateString(itemSelected.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        holder.tvItem3.setText(itemSelected.getItem4());
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem3()));
        holder.tvItem5.setText(itemSelected.getItem2());
        holder.tvItem6.setText(itemSelected.getItem6());
        holder.tvItem7.setText(itemSelected.getItem7());
        holder.tvItem8.setText(iv.ChangeFormatDateString(itemSelected.getItem8(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));

        return convertView;

    }
}
