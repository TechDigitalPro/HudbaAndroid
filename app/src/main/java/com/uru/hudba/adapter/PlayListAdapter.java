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

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.fragment.DetailBottomFragment;
import com.uru.hudba.fragment.PlayListBottomFragment;
import com.uru.hudba.fragment.PlaylistTrackDetailFragment;
import com.uru.hudba.fragment.TrackDetailFragment;
import com.uru.hudba.model.PlayListModel;

import java.util.ArrayList;
import java.util.Locale;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<PlayListModel> reviewList = new ArrayList<>();
    private ArrayList<PlayListModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    public PlayListAdapter(Context context, ArrayList<PlayListModel> reviewList) {
        this.reviewList = reviewList;
        this.context = context;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<PlayListModel> _models){
        reviewList.addAll(_models);
        notifyItemRangeChanged(reviewList.size() - _models.size() - 1, _models.size());
        notifyDataSetChanged();
    }
    public void setItem(int pos, PlayListModel model){
        reviewList.set(pos, model);
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
    public PlayListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_playlist_item, parent, false);
        itemView.setOnClickListener(this);
        return new PlayListAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.ItemHolder holder, int position) {

        PlayListModel item = reviewList.get(position);
        holder.rm_title.setText(item.getTitle());
        holder.rm_subtitle.setText(item.getSub_title());
        holder.rm_cnt.setText(item.getSong_cnt() + " songs");
        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.bg_round_rect)
                .into(holder.rm_image);
        holder.rm_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayListBottomFragment detailBottomFragment = new PlayListBottomFragment(position, item);
                detailBottomFragment.show(((MainActivity)context).getSupportFragmentManager(),"MyBottomSheet");
            }
        });
        holder.li_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                PlaylistTrackDetailFragment frag = new PlaylistTrackDetailFragment();
                bundle.putString("p_id", item.getId());
                bundle.putString("title", item.getTitle());
                bundle.putString("subtitle", item.getSub_title());
                bundle.putString("song_cnt", item.getSong_cnt());
                bundle.putString("image_path", item.getImage());
                frag.setArguments(bundle);
                ((MainActivity)context).pushFragment(frag, true, PlaylistTrackDetailFragment.class.getName());

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

        public TextView rm_title, rm_subtitle, rm_cnt;
        public ImageView rm_image, rm_more;
        public LinearLayout li_parent;
        public ItemHolder(View view) {
            super(view);
            li_parent = view.findViewById(R.id.li_parent);
            rm_title = (TextView) view.findViewById(R.id.title);
            rm_image = (ImageView) view.findViewById(R.id.cover_img);
            rm_subtitle = (TextView) view.findViewById(R.id.sub_title);
            rm_cnt = (TextView) view.findViewById(R.id.song_cnt);
            rm_more = (ImageView) view.findViewById(R.id.more);
        }
    }
}