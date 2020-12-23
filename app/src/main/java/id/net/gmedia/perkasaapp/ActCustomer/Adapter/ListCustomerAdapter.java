package id.net.gmedia.perkasaapp.ActCustomer.Adapter;

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

public class ListCustomerAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListCustomerAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.item_verifikasi_outlet, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView txt_nama, txt_alamat, txt_telepon, txt_handphone, txt_status;
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
            convertView = inflater.inflate(R.layout.item_verifikasi_outlet, null);
            holder.txt_nama = convertView.findViewById(R.id.txt_nama);
            holder.txt_alamat = convertView.findViewById(R.id.txt_alamat);
            holder.txt_telepon = convertView.findViewById(R.id.txt_telepon);
            holder.txt_handphone = convertView.findViewById(R.id.txt_handphone);
            holder.txt_status = convertView.findViewById(R.id.txt_status);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.txt_nama.setText(itemSelected.getItem2());
        holder.txt_alamat.setText(itemSelected.getItem3());
        holder.txt_telepon.setText(itemSelected.getItem4());
        holder.txt_handphone.setText(itemSelected.getItem5());
        holder.txt_status.setText(itemSelected.getItem6());

        return convertView;

    }
}
