package id.net.gmedia.perkasaapp.ActMarketSurvey.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import id.net.gmedia.perkasaapp.R;

public class AdapterProviderTelkomsel extends RecyclerView.Adapter<AdapterProviderTelkomsel.ViewXHolder> {

    private List<CustomItem> listItem;
    private ItemValidation iv = new ItemValidation();
    private Context context;

    public AdapterProviderTelkomsel(List<CustomItem> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    public List<CustomItem> getItems(){
        return listItem;
    }

    @NonNull
    @Override
    public ViewXHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_provider_tsel, parent, false);
        return new ViewXHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewXHolder holder, final int position) {

        final int current = position;
        final CustomItem item = listItem.get(position);

        holder.tvItem1.setText(item.getItem2());
        holder.edtItem1.setText(item.getItem3());

        holder.edtItem1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                listItem.get(position).setItem3(editable.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class ViewXHolder extends RecyclerView.ViewHolder{

        private TextView tvItem1;
        private EditText edtItem1;

        private ViewXHolder(@NonNull View itemView) {
            super(itemView);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            edtItem1 = itemView.findViewById(R.id.edt_item1);
        }
    }
}
