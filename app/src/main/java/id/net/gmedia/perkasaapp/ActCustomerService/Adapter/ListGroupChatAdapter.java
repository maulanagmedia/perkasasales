package id.net.gmedia.perkasaapp.ActCustomerService.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListGroupChatAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListGroupChatAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.item_group_chat, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private ImageView ivProfile;
        private TextView tvNama, tvDeskripsi, tvTime;
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
            convertView = inflater.inflate(R.layout.item_group_chat, null);
            holder.ivProfile = convertView.findViewById(R.id.iv_profile);
            holder.tvNama = convertView.findViewById(R.id.tv_nama);
            holder.tvDeskripsi = convertView.findViewById(R.id.tv_deskripsi);
            holder.tvTime= convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvNama.setText(itemSelected.getItem2());
        holder.tvDeskripsi.setText(itemSelected.getItem3());
        holder.tvTime.setText(iv.ChangeFormatDateString(itemSelected.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        ImageUtils iu = new ImageUtils();
        iu.LoadProfileImage(context, itemSelected.getItem4(), holder.ivProfile);

        return convertView;

    }
}
