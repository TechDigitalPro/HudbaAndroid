package com.uru.hudba.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.model.SongModel;
import com.uru.hudba.model.TrackModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<TrackModel> reviewList = new ArrayList<>();
    private ArrayList<TrackModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    public HomeAdapter(Context context, ArrayList<TrackModel> reviewList, FragmentManager fManager) {
        this.reviewList = reviewList;
        this.context = context;
        this.fragmentManager = fManager;
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
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_home, parent, false);
        itemView.setOnClickListener(this);
        return new HomeAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeAdapter.ItemHolder holder, int position) {

        TrackModel item = reviewList.get(position);
        if (!TextUtils.isEmpty(item.getTitle())) {
            holder.rm_name.setText(item.getTitle());
        }


        String url = item.getCover_image();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .override(110, 110)
                    .placeholder(R.drawable.aaa)
                    .into(holder.rm_image);
        }
        holder.rm_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).setTrackList(reviewList);
                ((MainActivity)context).initPlayer(position);
                ((MainActivity)context).current_playing_type = "home";
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

        public TextView rm_name;
        public ImageView rm_image;

        public ItemHolder(View view) {
            super(view);
            rm_name = (TextView) view.findViewById(R.id.rm_name);
            rm_image = (ImageView) view.findViewById(R.id.rm_image);

        }
    }
}