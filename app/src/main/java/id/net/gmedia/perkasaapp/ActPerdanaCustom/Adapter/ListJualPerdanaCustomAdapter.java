package id.net.gmedia.perkasaapp.ActPerdanaCustom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.net.gmedia.perkasaapp.ActPerdanaCustom.ListJualPerdanaCustom;
import id.net.gmedia.perkasaapp.R;
import id.net.gmedia.perkasaapp.Utils.ServerURL;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListJualPerdanaCustomAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListJualPerdanaCustomAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_jual_perdana_custom, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7;
        private ImageView imgItem;
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
            convertView = inflater.inflate(R.layout.adapter_list_jual_perdana_custom, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = convertView.findViewById(R.id.tv_item6);
            holder.tvItem7 = convertView.findViewById(R.id.tv_item7);
            holder.imgItem = convertView.findViewById(R.id.img_delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(iv.ChangeFormatDateString(itemSelected.getItem2(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem4()));
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem5()));
        holder.tvItem5.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem6()));
        holder.tvItem6.setText(itemSelected.getItem7());
        holder.tvItem7.setText(itemSelected.getItem8());

        holder.imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menghapus order milik "+itemSelected.getItem3()+"?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                JSONObject jBody = new JSONObject();
                                try {
                                    jBody.put("nobukti", itemSelected.getItem8());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.batalkanDealing, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        try {
                                            JSONObject response = new JSONObject(result);
                                            String status = response.getJSONObject("metadata").getString("status");
                                            String message = response.getJSONObject("metadata").getString("message");

                                            if(iv.parseNullInteger(status) == 200){
                                                Intent intent = new Intent(context, ListJualPerdanaCustom.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("flag", "DEALING");
                                                context.startActivity(intent);
                                                context.finish();

                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(String result) {
                                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        return convertView;

    }
}
