package com.uru.hudba.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.AlbumAdapter;
import com.uru.hudba.adapter.HomeAdapter;
import com.uru.hudba.adapter.TrendingAdapter;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.AlbumModel;
import com.uru.hudba.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ImageView btn_profile;
    private RecyclerView rv_home, rv_top, rv_daily, rv_favorite, rv_rank, rv_trending;
    private LinearLayout li_top, li_daily, li_favorite, li_rank, li_trending;
    private TextView tv_top, tv_favorite, tv_ranking;

    private ArrayList<TrackModel> home_list = new ArrayList<>();
    private ArrayList<AlbumModel> top_list = new ArrayList<>();
    private ArrayList<AlbumModel> daily_list = new ArrayList<>();
    private ArrayList<TrackModel> favorite_list = new ArrayList<>();
    private ArrayList<AlbumModel> ranking_list = new ArrayList<>();
    private ArrayList<TrackModel> trending_list = new ArrayList<>();


    private HomeAdapter homeAdapter;
    private AlbumAdapter topAdapter, dailyAdapter, rankAdapter;
    private TrendingAdapter trendingAdapter, favoriteAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "home";
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btn_profile = rootView.findViewById(R.id.btn_profile);

        /******************HOME**************/
        rv_home = rootView.findViewById(R.id.rv_home);
        rv_home.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_home.setHasFixedSize(true);
        rv_home.setLayoutManager(linearLayoutManager);
        /******************TOP Playlist**************/
        li_top = rootView.findViewById(R.id.ll_top);
        tv_top = rootView.findViewById(R.id.tv_top_all);
        tv_top.setOnClickListener(this);
        rv_top = rootView.findViewById(R.id.rv_top);
        rv_top.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_top = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_top.setHasFixedSize(true);
        rv_top.setLayoutManager(linearLayoutManager_top);
        /******************Daily Mixes**************/
        li_daily = rootView.findViewById(R.id.ll_daily);
        rv_daily = rootView.findViewById(R.id.rv_daily);
        rv_daily.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_daily = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_daily.setHasFixedSize(true);
        rv_daily.setLayoutManager(linearLayoutManager_daily);
        /******************Favorite Tracks**************/
        li_favorite = rootView.findViewById(R.id.ll_favorite);
        tv_favorite = rootView.findViewById(R.id.tv_favorite_all);
        tv_favorite.setOnClickListener(this);
        rv_favorite = rootView.findViewById(R.id.rv_favorite);
        rv_favorite.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_favorite = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_favorite.setHasFixedSize(true);
        rv_favorite.setLayoutManager(linearLayoutManager_favorite);
        /******************Ranking**************/
        li_rank = rootView.findViewById(R.id.ll_rank);
        tv_ranking = rootView.findViewById(R.id.tv_rank_all);
        tv_ranking.setOnClickListener(this);
        rv_rank = rootView.findViewById(R.id.rv_rank);
        rv_rank.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_rank = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_rank.setHasFixedSize(true);
        rv_rank.setLayoutManager(linearLayoutManager_rank);
        /******************Trending**************/
        li_trending = rootView.findViewById(R.id.ll_trend);
        rv_trending = rootView.findViewById(R.id.rv_trend);
        rv_trending.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_trend = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_trending.setHasFixedSize(true);
        rv_trending.setLayoutManager(linearLayoutManager_trend);


        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onClickAccount();
            }
        });
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    private void getData(){
        home_list.clear();
        top_list.clear();
        daily_list.clear();
        favorite_list.clear();
        ranking_list.clear();
        trending_list.clear();

        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        Log.e("member_key:", member_key);
        if (App.isNetworkAvailable(getContext())) {
            ((MainActivity)getActivity()).showProgressDialog(getActivity());
            Ion.with(getContext())
                    .load(GlobalConstants.HOME_LIST)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("memberKey", member_key)
                    .setBodyParameter("user_id", user_id)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                ((MainActivity)getActivity()).hideProgressDialog();
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        Gson gson = new Gson();


                                        JSONArray homeArray = api_result.getJSONArray("home_list");
                                        if (homeArray.length() == 0){
                                            home_list.clear();
                                            rv_home.setVisibility(View.GONE);
                                        }else {
                                            rv_home.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < homeArray.length(); i++) {
                                                TrackModel home = gson.fromJson(homeArray.getJSONObject(i).toString(), TrackModel.class);
                                                home_list.add(home);
                                            }
                                            createHomeRV(home_list);
                                        }
                                        Gson tmp_gson = new Gson();
                                        String down_home_data = tmp_gson.toJson(home_list);
                                        JsonArray down_homejsonArray = new JsonParser().parse(down_home_data).getAsJsonArray();
                                        String tmp_down_home_str = down_homejsonArray.toString();
                                        App.saveSharedSetting("home_data", tmp_down_home_str);

                                        JSONArray favoriteArray = api_result.getJSONArray("favorite_list");
                                        if (favoriteArray.length() == 0){
                                            favorite_list.clear();
                                            li_favorite.setVisibility(View.GONE);
                                        }else {
                                            li_favorite.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < favoriteArray.length(); i++) {
                                                TrackModel favorite = gson.fromJson(favoriteArray.getJSONObject(i).toString(), TrackModel.class);
                                                favorite_list.add(favorite);
                                            }
                                            createFavoriteRV(favorite_list);
                                        }


                                        Gson tmp_fgson = new Gson();
                                        String down_favorite_data = tmp_fgson.toJson(favorite_list);
                                        JsonArray down_fjsonArray = new JsonParser().parse(down_favorite_data).getAsJsonArray();
                                        String tmp_down_fav_str = down_fjsonArray.toString();
                                        App.saveSharedSetting("favorite_data", tmp_down_fav_str);

                                        JSONArray topArray = api_result.getJSONArray("top_list");

                                        if (topArray.length() == 0){
                                            top_list.clear();
                                            li_top.setVisibility(View.GONE);
                                        }else {
                                            li_top.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < topArray.length(); i++) {
                                                AlbumModel topModel = gson.fromJson(topArray.getJSONObject(i).toString(), AlbumModel.class);
                                                top_list.add(topModel);
                                            }
                                            createTopRV(top_list);
                                        }
                                        JSONArray rankArray = api_result.getJSONArray("rank_list");
                                        if (rankArray.length() == 0){
                                            ranking_list.clear();
                                            li_rank.setVisibility(View.GONE);
                                        }else {
                                            li_rank.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < rankArray.length(); i++) {
                                                String list_id = rankArray.getJSONObject(i).getString("playlist_id");
                                                String title = rankArray.getJSONObject(i).getString("country");
                                                String cover_image = rankArray.getJSONObject(i).getString("image");
                                                AlbumModel topModel = new AlbumModel();
                                                topModel.setList_id(list_id);
                                                topModel.setTitle(title);
                                                topModel.setCover_image(cover_image);
                                                ranking_list.add(topModel);
                                            }
                                            createRankRV(ranking_list);
                                        }

                                        JSONArray trendingArray = api_result.getJSONArray("trend_list");
                                        if (trendingArray.length() == 0){
                                            trending_list.clear();
                                            li_trending.setVisibility(View.GONE);
                                        }else {
                                            li_trending.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < trendingArray.length(); i++) {
                                                TrackModel trending = gson.fromJson(trendingArray.getJSONObject(i).toString(), TrackModel.class);
                                                trending_list.add(trending);

                                            }
                                            createTrendingRV(trending_list);
                                        }


                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                    ((MainActivity)getActivity()).showErrorMessages(ex.getMessage());
                                }
                            } else {
                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            home_list.clear();
            String home_str = App.readSharedSetting("home_data", "");
            if(!TextUtils.isEmpty(home_str)){
                try {
                    Gson gson = new Gson();
                    JSONArray download_homedata_array = new JSONArray(home_str);
                    if (download_homedata_array.length() == 0){
                        home_list.clear();
                        rv_home.setVisibility(View.GONE);
                    }else {
                        rv_home.setVisibility(View.VISIBLE);
                        for (int i = 0; i < download_homedata_array.length(); i++) {
                            TrackModel home = gson.fromJson(download_homedata_array.getJSONObject(i).toString(), TrackModel.class);
                            home_list.add(home);
                        }
                        createHomeRV(home_list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            favorite_list.clear();
            String fav_str = App.readSharedSetting("favorite_data", "");
            if(!TextUtils.isEmpty(fav_str)){
                try {
                    Gson gson = new Gson();
                    JSONArray download_favdata_array = new JSONArray(fav_str);
                    if (download_favdata_array.length() == 0){
                        favorite_list.clear();
                        li_favorite.setVisibility(View.GONE);
                    }else {
                        li_favorite.setVisibility(View.VISIBLE);
                        for (int i = 0; i < download_favdata_array.length(); i++) {
                            TrackModel fav = gson.fromJson(download_favdata_array.getJSONObject(i).toString(), TrackModel.class);
                            favorite_list.add(fav);
                        }
                        createFavoriteRV(favorite_list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ranking_list.clear();
            li_rank.setVisibility(View.GONE);
            top_list.clear();
            li_top.setVisibility(View.GONE);
            trending_list.clear();
            li_trending.setVisibility(View.GONE);
        }

    }
    private void createHomeRV(ArrayList<TrackModel> songModels){
        homeAdapter = new HomeAdapter(getContext(), songModels,getFragmentManager());
        homeAdapter.notifyDataSetChanged();
        rv_home.setAdapter(homeAdapter);
    }
    private void createFavoriteRV(ArrayList<TrackModel> album_list){
        favoriteAdapter = new TrendingAdapter(getContext(), album_list,getFragmentManager(), "favorite");
        favoriteAdapter.notifyDataSetChanged();
        rv_favorite.setAdapter(favoriteAdapter);
    }

    private void createTopRV(ArrayList<AlbumModel> album_list){
        topAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "top", "common");
        topAdapter.notifyDataSetChanged();
        rv_top.setAdapter(topAdapter);
        topAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = top_list.get(rv_top.getChildAdapterPosition(view)).getList_id();
                String title = top_list.get(rv_top.getChildAdapterPosition(view)).getTitle();
                String image = top_list.get(rv_top.getChildAdapterPosition(view)).getCover_image();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("type", "top");
                bundle.putString("image", image);
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }
    private void createDailyRV(ArrayList<AlbumModel> daily_list){
        dailyAdapter = new AlbumAdapter(getContext(), daily_list,getFragmentManager(), "daily", "common");
        dailyAdapter.notifyDataSetChanged();
        rv_daily.setAdapter(dailyAdapter);
        dailyAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = daily_list.get(rv_daily.getChildAdapterPosition(view)).getList_id();
                String title = daily_list.get(rv_daily.getChildAdapterPosition(view)).getTitle();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }
    private void createRankRV(ArrayList<AlbumModel> album_list){
        rankAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "ranking", "common");
        rankAdapter.notifyDataSetChanged();
        rv_rank.setAdapter(rankAdapter);
        rankAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ranking_list.get(rv_rank.getChildAdapterPosition(view)).getList_id();
                String title = ranking_list.get(rv_rank.getChildAdapterPosition(view)).getTitle();
                String image = ranking_list.get(rv_rank.getChildAdapterPosition(view)).getCover_image();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("type", "ranking");
                bundle.putString("image", "");
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }
    private void createTrendingRV(ArrayList<TrackModel> album_list){
        trendingAdapter = new TrendingAdapter(getContext(), album_list,getFragmentManager(), "trending");
        trendingAdapter.notifyDataSetChanged();
        rv_trending.setAdapter(trendingAdapter);
    }
    public void update_trackList(String type, int pos, TrackModel trackModel){
        if(type.equalsIgnoreCase("home")){
            home_list.remove(pos);
            homeAdapter.notifyDataSetChanged();
        }else if(type.equalsIgnoreCase("favorite")){
            favorite_list.remove(pos);
            favoriteAdapter.notifyDataSetChanged();
        }else if(type.equalsIgnoreCase("trending")){
            trending_list.set(pos, trackModel);
            trendingAdapter.notifyDataSetChanged();
        }
    }
    private void ReplaceFragment(String type){
        Fragment frag = null;
        frag = new SelectAllFragment();
        Bundle param = new Bundle();
        param.putString("type", type);
        frag.setArguments(param);
        pushFragment(frag, true, SelectAllFragment.class.getName());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_top_all:
                ReplaceFragment("top");
                break;
            case R.id.tv_favorite_all:
                if(App.isNetworkAvailable(getContext())){
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "favorite");
                    SavedFragment frag = new SavedFragment();
                    frag.setArguments(bundle);
                    pushFragment(frag, true, SavedFragment.class.getName());
                }

                break;
            case R.id.tv_rank_all:
                ReplaceFragment("ranking");
                break;
        }
    }
}
