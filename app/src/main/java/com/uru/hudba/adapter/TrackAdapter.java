package com.uru.hudba.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.constant.Constant;
import com.uru.hudba.fragment.DetailBottomFragment;
import com.uru.hudba.fragment.PlayListBottomFragment;
import com.uru.hudba.model.TrackModel;

import java.util.ArrayList;
import java.util.Locale;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<TrackModel> reviewList = new ArrayList<>();
    private ArrayList<TrackModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    private int clicked_position = -1;
    private int download_position = -1;
    private boolean checked = false;
    private boolean download_checked = false;
    public TrackAdapter(Context context, ArrayList<TrackModel> reviewList, FragmentManager fManager) {
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

    public void setChecked(boolean flag, String type){
        this.checked = flag;
    }



    public void unsetDownloading(String trackid){
        try {
            if(reviewList != null){
                for(int i = 0; i < reviewList.size(); i++){
                    if(reviewList.get(i).getTrack_id().equalsIgnoreCase(trackid)){
                        TrackModel tmp = reviewList.get(i);
                        tmp.setFavorite("download");
                        reviewList.set(i, tmp);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean getChecked(){
        return this.checked;
    }
    public void setPosition(int pos, String type){
        if(type.equalsIgnoreCase("download")){
            this.download_position = pos;
        }else{
            this.clicked_position = pos;
        }
    }
    public int getPosition(String type){
        if(type.equalsIgnoreCase("download")){
            return this.download_position;
        }else{
            return this.clicked_position;
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
    public TrackAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_track, parent, false);
        itemView.setOnClickListener(this);
        return new TrackAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrackAdapter.ItemHolder holder, int position) {

        TrackModel item = reviewList.get(position);
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





        holder.li_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity)context).setTrackList(reviewList);
                ((MainActivity)context).initPlayer(position);
                ((MainActivity)context).current_playing_type = "track";
            }
        });
        holder.rm_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailBottomFragment detailBottomFragment = new DetailBottomFragment(item, "list_view", position);
                detailBottomFragment.show(((MainActivity)context).getSupportFragmentManager(),"MyBottomSheet");
            }
        });
        if(clicked_position!=-1){
            if(getPosition("common") == position) {
                if(checked){
                    holder.rm_title.setTextColor(context.getResources().getColor(R.color.green_dark));
                }else{
                    holder.rm_title.setTextColor(context.getResources().getColor(R.color.white_color));
                }
            }else if(getPosition("common") != position){
                holder.rm_title.setTextColor(context.getResources().getColor(R.color.white_color));
            }
        }
        if(Constant.cur_track_id.equalsIgnoreCase(item.getTrack_id())){
            holder.rm_title.setTextColor(context.getResources().getColor(R.color.green_dark));
        }




//        if(!TextUtils.isEmpty(item.getFavorite()) && item.getFavorite().contains("copy")){
//            holder.rm_download_loading.setVisibility(View.VISIBLE);
//            holder.rm_num.setVisibility(View.GONE);
//        }else{
//            holder.rm_download_loading.setVisibility(View.GONE);
//            holder.rm_num.setVisibility(View.VISIBLE);
//        }
        if(Constant.downloading_list != null && Constant.downloading_list.size()>0){
            for(int i = 0; i< Constant.downloading_list.size(); i++){
                if(Constant.downloading_list.get(i).getTrackModel().getTrack_id().equalsIgnoreCase(item.getTrack_id())){
                    holder.rm_download_loading.setVisibility(View.VISIBLE);
                    holder.rm_num.setVisibility(View.GONE);
                }
            }
        }
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
        public LinearLayout li_title;
        public ImageView rm_more;
        public ProgressBar rm_download_loading;

        public ItemHolder(View view) {
            super(view);
            rm_num = view.findViewById(R.id.num_txt);
            rm_title = (TextView) view.findViewById(R.id.title);
            rm_artist = (TextView) view.findViewById(R.id.artist);
            li_title = (LinearLayout) view.findViewById(R.id.li_title);
            rm_more = (ImageView) view.findViewById(R.id.more);
            rm_download_loading = (ProgressBar) view.findViewById(R.id.download_loading);

        }
    }
}