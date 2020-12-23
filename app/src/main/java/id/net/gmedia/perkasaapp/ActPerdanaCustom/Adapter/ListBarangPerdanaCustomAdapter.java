package id.net.gmedia.perkasaapp.ActPerdanaCustom.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListBarangPerdanaCustomAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListBarangPerdanaCustomAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_perdana_custom, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
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
            convertView = inflater.inflate(R.layout.adapter_perdana_custom, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem3()));
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem4()));

        return convertView;

    }
}
