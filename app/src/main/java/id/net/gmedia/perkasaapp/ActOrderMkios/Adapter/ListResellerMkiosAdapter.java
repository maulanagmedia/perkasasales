package id.net.gmedia.perkasaapp.ActOrderMkios.Adapter;

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

public class ListResellerMkiosAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListResellerMkiosAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.item_order_mkios, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView txt_nama, txt_no_reseller, txt_tgl_order;
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
            convertView = inflater.inflate(R.layout.item_order_mkios, null);
            holder.txt_nama = convertView.findViewById(R.id.txt_nama);
            holder.txt_no_reseller = convertView.findViewById(R.id.txt_no_reseller);
            holder.txt_tgl_order = convertView.findViewById(R.id.txt_tgl_order);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.txt_nama.setText(itemSelected.getItem2());
        holder.txt_no_reseller.setText(itemSelected.getItem3());
        holder.txt_tgl_order.setText(iv.ChangeFormatDateString(itemSelected.getItem4(), FormatItem.formatTimestamp, FormatItem.formatDateDisplay));

        return convertView;

    }
}
