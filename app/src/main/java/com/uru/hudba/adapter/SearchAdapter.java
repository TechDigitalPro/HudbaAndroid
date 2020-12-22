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
import com.uru.hudba.model.SearchModel;

import java.util.ArrayList;
import java.util.Locale;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> implements View.OnClickListener {
    private ArrayList<SearchModel> reviewList = new ArrayList<>();
    private ArrayList<SearchModel> mSearchList = new ArrayList<>();
    private View.OnClickListener listener;
    private Context context;
    private FragmentManager fragmentManager;
    public SearchAdapter(Context context, ArrayList<SearchModel> reviewList, FragmentManager fManager) {
        this.reviewList = reviewList;
        this.context = context;
        this.fragmentManager = fManager;
        mSearchList.addAll(this.reviewList);
    }

    public void addItems(ArrayList<SearchModel> _models){
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
    public SearchAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_search, parent, false);
        itemView.setOnClickListener(this);
        return new SearchAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ItemHolder holder, int position) {

        SearchModel item = reviewList.get(position);
        if (!TextUtils.isEmpty(item.getTitle())) {
            holder.rm_name.setText(item.getTitle());
        }
        String url = item.getCover_image();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .override(180, 180).placeholder(R.drawable.aaa)
                    .into(holder.rm_image);
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

        public TextView rm_name;
        public ImageView rm_image;

        public ItemHolder(View view) {
            super(view);
            rm_name = (TextView) view.findViewById(R.id.rm_name);
            rm_image = (ImageView) view.findViewById(R.id.rm_image);
        }
    }
}