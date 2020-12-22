package com.uru.hudba.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uru.hudba.R;
import com.uru.hudba.model.ContactModel;

import java.util.ArrayList;

public class ContactusAdapter extends RecyclerView.Adapter<ContactusAdapter.ViewHolder>{
    private Context context;
    ArrayList<ContactModel> mList = new ArrayList<>();

    public ContactusAdapter(Context contexty, ArrayList<ContactModel> list) {
        this.context = contexty;
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ContactModel ContactModel = mList.get(position);
        holder.tvTitleNo.setText(ContactModel.getTitle());
        holder.tvTitleYes.setText(ContactModel.getTitle());
        holder.tvDescription.setText(ContactModel.getDescription());
        holder.lvClickedNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lvClickedNo.setVisibility(View.GONE);
                holder.lvClickedYes.setVisibility(View.VISIBLE);
                holder.tvDescription.setVisibility(View.VISIBLE);
            }
        });
        holder.lvClickedYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lvClickedYes.setVisibility(View.GONE);
                holder.lvClickedNo.setVisibility(View.VISIBLE);
                holder.tvDescription.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitleNo;
        private TextView tvTitleYes;
        private TextView tvDescription;
        private LinearLayout lvClickedNo;
        private LinearLayout lvClickedYes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleNo = itemView.findViewById(R.id.item_faq_title_no);
            tvTitleYes = itemView.findViewById(R.id.item_faq_title_yes);
            tvDescription = itemView.findViewById(R.id.item_faq_tv_description);
            lvClickedNo = itemView.findViewById(R.id.item_faq_lv_clicked_no);
            lvClickedYes = itemView.findViewById(R.id.item_faq_lv_clicked_yes);
        }
    }
}
