package com.uru.hudba.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.fragment.PlayListBottomFragment;
import com.uru.hudba.fragment.PlaylistTrackDetailFragment;
import com.uru.hudba.model.MyPlayListModel;
import com.uru.hudba.model.PlayListModel;
import com.uru.hudba.model.UserTrackModel;

import java.util.ArrayList;
import java.util.Locale;


public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<MyPlayListModel> reviewList = new ArrayList<>();
    private ArrayList<MyPlayListModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    public MyPlayListAdapter(Context context, ArrayList<MyPlayListModel> reviewList) {
        this.reviewList = reviewList;
        this.context = context;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<MyPlayListModel> _models){
        reviewList.addAll(_models);
        notifyItemRangeChanged(reviewList.size() - _models.size() - 1, _models.size());
        notifyDataSetChanged();
    }
    public void setItem(int pos, MyPlayListModel model){
        reviewList.set(pos, model);
        notifyDataSetChanged();
    }
    public void setSelected(int position, String value) {
        if(reviewList != null){
            reviewList.get(position).setCheck_flag(value);
        }
    }
    public ArrayList<MyPlayListModel> getList(){
        return reviewList;
    }
    public void searchItems(CharSequence text) {

        reviewList.clear();
        if (text.length() == 0) {
            reviewList.addAll(mSearchList);
        } else {
            for (int i = 0; i < mSearchList.size(); i++) {
                String name = mSearchList.get(i).getTitle();
                if (name.toLowerCase(Locale.getDefault()).contains(text)) {
                    reviewList.add(mSearchList.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public MyPlayListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_myplaylist_item, parent, false);
        itemView.setOnClickListener(this);
        return new MyPlayListAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyPlayListAdapter.ItemHolder holder, int position) {

        MyPlayListModel item = reviewList.get(position);
        holder.rm_title.setText(item.getTitle());
        holder.rm_subtitle.setText(item.getSub_title());
        holder.rm_cnt.setText(item.getSong_cnt() + " songs");
        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.bg_round_rect)
                .into(holder.rm_image);
        if(!TextUtils.isEmpty(item.getCheck_flag()) && item.getCheck_flag().equalsIgnoreCase("2")){
            holder.rm_check.setVisibility(View.VISIBLE);
            holder.rm_check.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black));
            holder.li_parent.setBackgroundColor(context.getResources().getColor(R.color.transparent_color));
        }else if(!TextUtils.isEmpty(item.getCheck_flag()) && item.getCheck_flag().equalsIgnoreCase("1")){
            holder.rm_check.setVisibility(View.VISIBLE);
            holder.rm_check.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_green));
            holder.li_parent.setBackgroundColor(context.getResources().getColor(R.color.grey_search));
        }else{
            holder.rm_check.setVisibility(View.INVISIBLE);
            holder.rm_check.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black));
            holder.li_parent.setBackgroundColor(context.getResources().getColor(R.color.transparent_color));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(item.getCheck_flag()) && !item.getCheck_flag().equalsIgnoreCase("2")){
                    if(item.getCheck_flag().equalsIgnoreCase("0")){
                        setSelected(position, "1");
                    }else{
                        setSelected(position, "0");
                    }
                    notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

        if (listener != null) {
            listener.onClick(v);

        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public LinearLayout li_parent;
        public TextView rm_title, rm_subtitle, rm_cnt;
        public ImageView rm_image;
        public ImageView rm_check;
        public ItemHolder(View view) {
            super(view);
            li_parent = (LinearLayout) view.findViewById(R.id.li_parent);
            rm_title = (TextView) view.findViewById(R.id.title);
            rm_image = (ImageView) view.findViewById(R.id.cover_img);
            rm_subtitle = (TextView) view.findViewById(R.id.sub_title);
            rm_cnt = (TextView) view.findViewById(R.id.song_cnt);
            rm_check = (ImageView) view.findViewById(R.id.rm_check);
        }
    }
}