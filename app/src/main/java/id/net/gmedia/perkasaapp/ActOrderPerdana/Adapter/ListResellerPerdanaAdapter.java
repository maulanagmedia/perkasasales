package id.net.gmedia.perkasaapp.ActOrderPerdana.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListResellerPerdanaAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListResellerPerdanaAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.item_order_perdana1, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView txt_nama, txt_alamat, txt_nomor;
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
            convertView = inflater.inflate(R.layout.item_order_perdana1, null);
            holder.txt_nama = convertView.findViewById(R.id.txt_nama);
            holder.txt_alamat = convertView.findViewById(R.id.txt_alamat);
            holder.txt_nomor = convertView.findViewById(R.id.txt_notelp);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.txt_nama.setText(itemSelected.getItem2());
        holder.txt_alamat.setText(itemSelected.getItem3());
        holder.txt_nomor.setText(itemSelected.getItem4());

        return convertView;

    }
}
