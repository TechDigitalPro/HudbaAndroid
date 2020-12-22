package com.uru.hudba.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uru.hudba.R;
import com.uru.hudba.model.AlbumModel;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<AlbumModel> reviewList = new ArrayList<>();
    private ArrayList<AlbumModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    private String cur_type;
    private String view_type;
    public AlbumAdapter(Context context, ArrayList<AlbumModel> reviewList, FragmentManager fManager, String cur_type, String view_type) {
        this.reviewList = reviewList;
        this.context = context;
        this.fragmentManager = fManager;
        this.cur_type = cur_type;
        this.view_type = view_type;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<AlbumModel> _models){
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
    public AlbumAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(view_type.equalsIgnoreCase("select_all")){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_item_selectall, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_item_album, parent, false);
        }
        itemView.setOnClickListener(this);
        return new AlbumAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ItemHolder holder, int position) {

        AlbumModel item = reviewList.get(position);
        if(cur_type.equalsIgnoreCase("top")){
            try {
                if (!TextUtils.isEmpty(item.getTitle())) {
                    holder.rm_name.setText(item.getTitle());
                    holder.rm_content.setText(item.getTitle());
                }
                if (!TextUtils.isEmpty(item.getTrack_cnt())) {
                    holder.rm_des.setText(item.getTrack_cnt() + " songs");
                }
                Glide.with(context)
                        .load(item.getCover_image())
                        .override(180, 180).placeholder(R.drawable.aaa)
                        .into(holder.rm_image);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(cur_type.equalsIgnoreCase("album")){
            try {
                holder.rm_name.setText(item.getTitle());
                holder.rm_des.setText(item.getDescription());
                String url = item.getCover_image();
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .override(180, 180).placeholder(R.drawable.aaa)
                            .into(holder.rm_image);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(cur_type.equalsIgnoreCase("artist")){
            try {
                if (!TextUtils.isEmpty(item.getTitle())) {
                    holder.rm_name.setText(item.getTitle());
                }
                holder.rm_des.setText("");

                String url = item.getCover_image();
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .override(180, 180).placeholder(R.drawable.aaa)
                            .into(holder.rm_image);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            try {
                if (!TextUtils.isEmpty(item.getTitle())) {
                    holder.rm_name.setText(item.getTitle());
                    holder.rm_des.setText("Top viral songs from " +item.getTitle());
                }
                String url = item.getCover_image() ;
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .override(180, 180).placeholder(R.drawable.aaa)
                            .into(holder.rm_image);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

//        holder.rm_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                TrackDetailFragment frag = new TrackDetailFragment();
//                bundle.putString("id", item.getList_id());
//                bundle.putString("title", item.getTitle());
//                frag.setArguments(bundle);
//                (((BaseActivity)context).getCurrentFragment()).pushFragment(frag, true, TrackDetailFragment.class.getName());
//            }
//        });
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

        public TextView rm_name, rm_content, rm_des;
        public ImageView rm_image;

        public ItemHolder(View view) {
            super(view);
            rm_name = (TextView) view.findViewById(R.id.rm_name);
            rm_image = (ImageView) view.findViewById(R.id.rm_image);
            rm_content = (TextView) view.findViewById(R.id.rm_content);
            rm_des = (TextView) view.findViewById(R.id.rm_des);
        }
    }
}