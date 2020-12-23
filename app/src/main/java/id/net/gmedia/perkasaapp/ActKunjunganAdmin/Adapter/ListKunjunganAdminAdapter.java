package id.net.gmedia.perkasaapp.ActKunjunganAdmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class ListKunjunganAdminAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListKunjunganAdminAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_kunjungan, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private LinearLayout llTime;
        private ImageView ivTime;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_kunjungan, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.llTime = (LinearLayout) convertView.findViewById(R.id.ll_time);
            holder.ivTime= (ImageView) convertView.findViewById(R.id.iv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        if(!itemSelected.getItem2().equals("")){ //terkunjungi

            holder.llTime.setBackgroundColor(context.getResources().getColor(R.color.color_pjp_beli));
            holder.ivTime.setBackground(context.getResources().getDrawable(R.drawable.bg_round_green));
        }else{ // pjp belum terkunjungi
            holder.llTime.setBackgroundColor(context.getResources().getColor(R.color.color_pjp_belum));
            holder.ivTime.setBackground(context.getResources().getDrawable(R.drawable.bg_round_red));
        }

        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4() + " ("+ itemSelected.getItem5() + ")");

        if(itemSelected.getItem2().equals("")){

            holder.tvItem1.setText("");

        }else{

            holder.tvItem1.setText(iv.ChangeFormatDateString(itemSelected.getItem2(), FormatItem.formatTimestamp, FormatItem.formatTime));
        }

        return convertView;

    }
}
