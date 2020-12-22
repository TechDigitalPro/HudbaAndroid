package com.uru.hudba.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.model.TrackModel;

import java.util.ArrayList;
import java.util.Locale;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<TrackModel> reviewList = new ArrayList<>();
    private ArrayList<TrackModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    private String song_type = "";
    public TrendingAdapter(Context context, ArrayList<TrackModel> reviewList, FragmentManager fManager, String song_type) {
        this.reviewList = reviewList;
        this.context = context;
        this.fragmentManager = fManager;
        this.song_type = song_type;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<TrackModel> _models){
        reviewList.addAll(_models);
        notifyItemRangeChanged(reviewList.size() - _models.size() - 1, _models.size());
        notifyDataSetChanged();
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
    public TrendingAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_trending, parent, false);
        itemView.setOnClickListener(this);
        return new TrendingAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrendingAdapter.ItemHolder holder, int position) {

        TrackModel item = reviewList.get(position);
        if (!TextUtils.isEmpty(item.getTitle())) {
            holder.rm_title.setText(item.getTitle());
        }
        if (!TextUtils.isEmpty(item.getArtist_name())) {
            holder.rm_artist.setText(item.getArtist_name());
        }
        String url = item.getCover_image();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .override(180, 180).placeholder(R.drawable.aaa)
                    .into(holder.rm_image);
        }
        holder.rm_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).setTrackList(reviewList);
                ((MainActivity)context).initPlayer(position);
                ((MainActivity)context).current_playing_type = song_type;
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

        public TextView rm_title, rm_artist, rm_num;
        public ImageView rm_image;
        public ItemHolder(View view) {
            super(view);
            rm_image = view.findViewById(R.id.rm_image);
            rm_title = (TextView) view.findViewById(R.id.rm_name);
            rm_artist = (TextView) view.findViewById(R.id.rm_des);

        }
    }
}