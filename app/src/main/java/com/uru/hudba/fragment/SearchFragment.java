package com.uru.hudba.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.AlbumAdapter;
import com.uru.hudba.adapter.SearchAdapter;
import com.uru.hudba.adapter.TrackAdapter;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.AlbumModel;
import com.uru.hudba.model.SearchModel;
import com.uru.hudba.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.os.Looper.getMainLooper;
import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class SearchFragment extends BaseFragment implements View.OnClickListener{
    private View rootView;
    private ImageView btn_profile;
    private RecyclerView rv_search;
    private SearchView searchTxt;

    private ArrayList<SearchModel> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    private LinearLayout li_main, li_search, li_loading, li_empty;

    private RecyclerView rv_track, rv_album, rv_artist;
    private LinearLayout li_track, li_album, li_artist;
    private TextView tv_track_all, tv_album_all, tv_artist_all;

    private ArrayList<TrackModel> track_list = new ArrayList<>();
    private ArrayList<AlbumModel> album_list = new ArrayList<>();
    private ArrayList<AlbumModel> artist_list = new ArrayList<>();


    private TrackAdapter trackAdapter;
    private AlbumAdapter albumAdapter, artistAdapter;
    private String search_str = "";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "search";
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initView();
        getData();
        return rootView;
    }

    private void initView() {
        btn_profile = rootView.findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onClickAccount();
            }
        });
        li_main = rootView.findViewById(R.id.li_main);
        li_search = rootView.findViewById(R.id.li_search);
        li_loading = rootView.findViewById(R.id.li_loading);
        li_empty = rootView.findViewById(R.id.li_empty);

        rv_search = rootView.findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchTxt = rootView.findViewById(R.id.search_txt);

        searchTxt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(TextUtils.isEmpty(query)){
                    li_main.setVisibility(View.VISIBLE);
                    li_search.setVisibility(View.GONE);
                }else{
                    li_main.setVisibility(View.GONE);
                    li_search.setVisibility(View.VISIBLE);
                    li_empty.setVisibility(View.GONE);
                    onSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("query:", newText + "");
                if(TextUtils.isEmpty(newText)){
                    li_main.setVisibility(View.VISIBLE);
                    li_search.setVisibility(View.GONE);
                    li_empty.setVisibility(View.GONE);
                    li_loading.setVisibility(View.GONE);
                }
                else{
                    li_main.setVisibility(View.GONE);
                    li_search.setVisibility(View.VISIBLE);
                    li_empty.setVisibility(View.GONE);
                    li_loading.setVisibility(View.VISIBLE);
                    onSearch(newText);
                }
//                new Handler(getMainLooper()).postDelayed(()->{
//                    onSearch(newText);
//                }, 1000);

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(TextUtils.isEmpty(newText)){
//                            li_main.setVisibility(View.VISIBLE);
//                            li_search.setVisibility(View.GONE);
//                        }else{
//                            li_main.setVisibility(View.GONE);
//                            li_search.setVisibility(View.VISIBLE);
//                            li_loading.setVisibility(View.VISIBLE);
//                            onSearch(search_str);
//                        }
//                    }
//                }, 500);

                return false;
            }
        });
        searchTxt.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("search:", searchTxt.getQuery().toString());
            }
        });
        searchTxt.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });


        /******************Tracks**************/
        li_track = rootView.findViewById(R.id.li_track);
        tv_track_all = rootView.findViewById(R.id.tv_track_all);
        tv_track_all.setOnClickListener(this);
        rv_track = rootView.findViewById(R.id.rv_track);
        rv_track.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_top = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_track.setHasFixedSize(true);
        rv_track.setLayoutManager(linearLayoutManager_top);
        /******************Albums**************/
        li_album = rootView.findViewById(R.id.li_album);
        tv_album_all = rootView.findViewById(R.id.tv_album_all);
        tv_album_all.setOnClickListener(this);
        rv_album = rootView.findViewById(R.id.rv_album);
        rv_album.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_daily = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_album.setHasFixedSize(true);
        rv_album.setLayoutManager(linearLayoutManager_daily);
        /******************Artists**************/
        li_artist = rootView.findViewById(R.id.li_artist);
        tv_artist_all = rootView.findViewById(R.id.tv_artist_all);
        tv_artist_all.setOnClickListener(this);
        rv_artist = rootView.findViewById(R.id.rv_artist);
        rv_artist.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_rank = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_artist.setHasFixedSize(true);
        rv_artist.setLayoutManager(linearLayoutManager_rank);
    }
    private void getData(){
        searchList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        if (App.isNetworkAvailable(getContext())) {
            CommonUtils.showLoadingDialog(getActivity());
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH_LIST)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("memberKey", member_key)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            CommonUtils.hideLoadingDialog();
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        JSONArray articleArray = api_result.getJSONArray("list");
                                        Gson gson = new Gson();

                                        if (articleArray.length() == 0){
                                            searchList.clear();
                                        }else {
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                SearchModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), SearchModel.class);
                                                searchList.add(topModel);
                                            }
                                            createRV(searchList);
                                        }

                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void createRV(ArrayList<SearchModel> songModels){
        searchAdapter = new SearchAdapter(getContext(), songModels,getFragmentManager());
        searchAdapter.notifyDataSetChanged();
        rv_search.setAdapter(searchAdapter);
        searchAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = searchList.get(rv_search.getChildAdapterPosition(view)).getSearch_id();
                String title = searchList.get(rv_search.getChildAdapterPosition(view)).getTitle();
                String image = searchList.get(rv_search.getChildAdapterPosition(view)).getCover_image();

                Bundle bundle = new Bundle();
                SearchDetailFragment frag = new SearchDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("image", image);
                frag.setArguments(bundle);
                pushFragment(frag, true, SearchDetailFragment.class.getName());
            }
        });
    }
    public TrackAdapter getAdapter(){
        return trackAdapter;
    }
    private void onSearch(String query){
        track_list.clear();
        album_list.clear();
        artist_list.clear();
        li_track.setVisibility(View.GONE);
        li_album.setVisibility(View.GONE);
        li_artist.setVisibility(View.GONE);
        if(trackAdapter != null){
            trackAdapter.notifyDataSetChanged();
        }
        if(albumAdapter != null){
            albumAdapter.notifyDataSetChanged();
        }
        if(artistAdapter != null){
            artistAdapter.notifyDataSetChanged();
        }

        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        Log.e("member_key:", member_key);
        if (App.isNetworkAvailable(getContext())) {
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("memberKey", member_key)
                    .setBodyParameter("keyword", query)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("type", "all")
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");


                                        JSONArray trackArray = api_result.getJSONArray("track_list");
                                        Gson gson = new Gson();
                                        track_list.clear();

                                        if (trackArray.length() == 0){
                                            li_track.setVisibility(View.GONE);
                                        }else {
                                            li_track.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < trackArray.length(); i++) {
                                                TrackModel trackModel = gson.fromJson(trackArray.getJSONObject(i).toString(), TrackModel.class);
                                                track_list.add(trackModel);
                                            }
//                                            if(trackAdapter == null){
//                                                createTrackRV(track_list);
//                                            }else{
//                                                trackAdapter.notifyDataSetChanged();
//                                                rv_track.setVisibility(View.VISIBLE);
//                                            }
                                            createTrackRV(track_list);

                                        }


                                        JSONArray albumArray = api_result.getJSONArray("album_list");
                                        album_list.clear();

                                        if (albumArray.length() == 0){
                                            li_album.setVisibility(View.GONE);
                                        }else {
                                            li_album.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < albumArray.length(); i++) {
                                                String p_id = albumArray.getJSONObject(i).getString("list_id");
                                                String title = albumArray.getJSONObject(i).getString("title");
                                                String tracklist = albumArray.getJSONObject(i).getString("tracklist");
                                                String cover_image = albumArray.getJSONObject(i).getString("cover_image");
                                                String description = albumArray.getJSONObject(i).getString("user_name");
                                                AlbumModel albumModel = new AlbumModel();
                                                albumModel.setList_id(p_id);
                                                albumModel.setTitle(title);
                                                albumModel.setTracklist(tracklist);
                                                albumModel.setDescription(description);
                                                albumModel.setCover_image(cover_image);
                                                album_list.add(albumModel);
                                            }
//                                            if(albumAdapter == null){
//                                                createAlbumRV(album_list);
//                                            }else{
//                                                albumAdapter.notifyDataSetChanged();
//                                                rv_album.setVisibility(View.VISIBLE);
//                                            }
                                            createAlbumRV(album_list);

                                        }


                                        artist_list.clear();

                                        JSONArray artistArray = api_result.getJSONArray("artist_list");
                                        if (artistArray.length() == 0){
                                            li_artist.setVisibility(View.GONE);
                                        }else {
                                            li_artist.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < artistArray.length(); i++) {
                                                String list_id = artistArray.getJSONObject(i).getString("list_id");
                                                String title = artistArray.getJSONObject(i).getString("title");
                                                String cover_image = artistArray.getJSONObject(i).getString("cover_image");
                                                String track_list = artistArray.getJSONObject(i).getString("tracklist");
                                                AlbumModel topModel = new AlbumModel();
                                                topModel.setList_id(list_id);
                                                topModel.setTitle(title);
                                                topModel.setCover_image(cover_image);
                                                topModel.setTracklist(track_list);
                                                artist_list.add(topModel);
                                            }
//                                            if(artistAdapter == null){
//                                                createArtistRV(artist_list);
//                                            }else{
//                                                artistAdapter.notifyDataSetChanged();
//                                                rv_artist.setVisibility(View.VISIBLE);
//                                            }
                                            createArtistRV(artist_list);
                                        }
                                        li_loading.setVisibility(View.GONE);
                                        if(trackArray.length() == 0 && albumArray.length() == 0 && artistArray.length() == 0){
                                            li_search.setVisibility(View.GONE);
                                            li_empty.setVisibility(View.VISIBLE);
                                        }else{
                                            li_search.setVisibility(View.VISIBLE);
                                            li_empty.setVisibility(View.GONE);
                                        }

                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_server_error);
                            }

                        }
                    });
        } else {
            li_loading.setVisibility(View.GONE);
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void createTrackRV(ArrayList<TrackModel> songModels){
        trackAdapter = new TrackAdapter(getContext(), songModels,getFragmentManager());
        trackAdapter.notifyDataSetChanged();
        rv_track.setAdapter(trackAdapter);
    }
    private void createAlbumRV(ArrayList<AlbumModel> album_list){
        albumAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "album", "common");
        albumAdapter.notifyDataSetChanged();
        rv_album.setAdapter(albumAdapter);

        albumAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = album_list.get(rv_album.getChildAdapterPosition(view)).getList_id();
                String title = album_list.get(rv_album.getChildAdapterPosition(view)).getTitle();
                String image = album_list.get(rv_album.getChildAdapterPosition(view)).getCover_image();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("image", image);
                bundle.putString("type", "search_album");
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });

    }
    private void createArtistRV(ArrayList<AlbumModel> play_list){
        artistAdapter = new AlbumAdapter(getContext(), play_list,getFragmentManager(), "artist", "common");
        artistAdapter.notifyDataSetChanged();
        rv_artist.setAdapter(artistAdapter);
        artistAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = artist_list.get(rv_artist.getChildAdapterPosition(view)).getList_id();
                String title = artist_list.get(rv_artist.getChildAdapterPosition(view)).getTitle();
                String image = artist_list.get(rv_artist.getChildAdapterPosition(view)).getCover_image();
                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("image", image);
                bundle.putString("type", "search_artist");
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_track_all:
                Bundle bundle = new Bundle();
                TrackDetailFragment frag0 = new TrackDetailFragment();
                bundle.putString("id", searchTxt.getQuery().toString());
                bundle.putString("title", searchTxt.getQuery().toString());
                bundle.putString("type", "track");
                bundle.putString("image", "search_track");
                frag0.setArguments(bundle);
                pushFragment(frag0, true, TrackDetailFragment.class.getName());
                break;
            case R.id.tv_album_all:
                Fragment frag = null;
                frag = new SelectAllFragment();
                Bundle param = new Bundle();
                param.putString("type", "search");
                param.putString("data_type", "album");
                param.putString("keyword", searchTxt.getQuery().toString());
                frag.setArguments(param);
                pushFragment(frag, true, SelectAllFragment.class.getName());

                break;
            case R.id.tv_artist_all:
                Fragment frag1 = null;
                frag1 = new SelectAllFragment();
                Bundle param1 = new Bundle();
                param1.putString("type", "search");
                param1.putString("data_type", "artist");
                param1.putString("keyword", searchTxt.getQuery().toString());
                frag1.setArguments(param1);
                pushFragment(frag1, true, SelectAllFragment.class.getName());
                break;
            case R.id.back_btn:
                popFragment();
                break;
        }
    }
}
