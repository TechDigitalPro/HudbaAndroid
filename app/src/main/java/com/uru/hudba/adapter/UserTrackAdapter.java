package com.uru.hudba.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uru.hudba.R;
import com.uru.hudba.model.UserTrackModel;

import java.util.ArrayList;
import java.util.Locale;

public class UserTrackAdapter extends RecyclerView.Adapter<UserTrackAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<UserTrackModel> reviewList = new ArrayList<>();
    private ArrayList<UserTrackModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    private int clicked_position = -1;
    private int download_position = -1;
    private boolean checked = false;
    private boolean download_checked = false;
    public UserTrackAdapter(Context context, ArrayList<UserTrackModel> reviewList, FragmentManager fManager) {
        this.reviewList = reviewList;
        this.context = context;
        this.fragmentManager = fManager;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<UserTrackModel> _models){
        reviewList.addAll(_models);
        notifyItemRangeChanged(reviewList.size() - _models.size() - 1, _models.size());
        notifyDataSetChanged();
    }
    public ArrayList<UserTrackModel> getList(){
        return reviewList;
    }
    public void setSelected(int position, String value) {
        if(reviewList != null){
            reviewList.get(position).setCheck_flag(value);
        }
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
    public UserTrackAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_user_track, parent, false);
        itemView.setOnClickListener(this);
        return new UserTrackAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserTrackAdapter.ItemHolder holder, int position) {

        UserTrackModel item = reviewList.get(position);
        if (!TextUtils.isEmpty(item.getTitle())) {
            holder.rm_title.setText(item.getTitle());
        }
        if (!TextUtils.isEmpty(item.getArtist_name())) {
            holder.rm_artist.setText(item.getArtist_name());
        }
        if(!TextUtils.isEmpty(item.getFavorite()) && item.getFavorite().contains("download")){
            holder.rm_num.setTextColor(context.getResources().getColor(R.color.green_dark));
        }else{
            holder.rm_num.setTextColor(context.getResources().getColor(R.color.white_color));
        }
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

        holder.rm_num.setText(String.format(Locale.US, "%02d", holder.getLayoutPosition() + 1));

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

        public TextView rm_title, rm_artist, rm_num;
        public LinearLayout li_title, li_parent;
        public ImageView rm_check;

        public ItemHolder(View view) {
            super(view);
            li_parent = view.findViewById(R.id.li_parent);
            rm_num = view.findViewById(R.id.num_txt);
            rm_title = (TextView) view.findViewById(R.id.title);
            rm_artist = (TextView) view.findViewById(R.id.artist);
            li_title = (LinearLayout) view.findViewById(R.id.li_title);
            rm_check = (ImageView) view.findViewById(R.id.rm_check);

        }
    }
}