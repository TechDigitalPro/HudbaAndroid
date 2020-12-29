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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.AlbumAdapter;
import com.uru.hudba.adapter.HomeAdapter;
import com.uru.hudba.adapter.TrackAdapter;
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

public class SearchDetailFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;

    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CardView main_shuffle, toolbar_shuffle;
    private LinearLayout li_content;
    private TextView toolbar_title, content_title;
    private ImageView content_image, back_btn;

    private RecyclerView rv_track, rv_album, rv_playlist, rv_artist;
    private LinearLayout li_track, li_album, li_playlist, li_artist;
    private TextView tv_track_all, tv_album_all, tv_playlist_all, tv_artist_all;

    private ArrayList<TrackModel> track_list = new ArrayList<>();
    private ArrayList<AlbumModel> album_list = new ArrayList<>();
    private ArrayList<AlbumModel> play_list = new ArrayList<>();
    private ArrayList<AlbumModel> artist_list = new ArrayList<>();


    private TrackAdapter trackAdapter;
    private AlbumAdapter albumAdapter, playlistAdapter, artistAdapter;


    
    private String search_id, title, content_img_url;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "search_detail";
        try {
            search_id = getArguments().getString("id");
            title = getArguments().getString("title");
            content_img_url = getArguments().getString("image");
        }catch (Exception e){
            e.printStackTrace();
        }

        rootView = inflater.inflate(R.layout.fragment_search_detail, container, false);
        main_shuffle = rootView.findViewById(R.id.main_shuffle);
        main_shuffle.setOnClickListener(this);
        toolbar_shuffle = rootView.findViewById(R.id.toolbar_shuffle);
        toolbar_shuffle.setOnClickListener(this);
        li_content = rootView.findViewById(R.id.li_content);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        content_title = rootView.findViewById(R.id.content_title);
        content_image = rootView.findViewById(R.id.imageview);
        back_btn = rootView.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        String url = content_img_url;
        Glide.with(getActivity())
                .load(url)
                .placeholder(R.drawable.aaa)
                .into(content_image);


        content_title.setText(title);
        toolbar_title.setText(title);

        AppBarLayout app_bar=(AppBarLayout) rootView.findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//Modify the status token to expand
                        toolbar_title.setVisibility(View.GONE);
                        toolbar_shuffle.setVisibility(View.INVISIBLE);
                        li_content.setVisibility(View.VISIBLE);
                        main_shuffle.setVisibility(View.VISIBLE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        main_shuffle.setVisibility(View.GONE);//Hide Play Button
                        toolbar_shuffle.setVisibility(View.VISIBLE);
                        state = CollapsingToolbarLayoutState.COLLAPSED;//Modified status marked as folded
                        toolbar_title.setVisibility(View.VISIBLE);
                        li_content.setVisibility(View.GONE);
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if(state == CollapsingToolbarLayoutState.COLLAPSED){
                            main_shuffle.setVisibility(View.GONE);//Hide Play Button When Changed from Folding to Intermediate State
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//Modify the status tag to the middle
                    }
                }
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
        /******************Playlists**************/
        li_playlist = rootView.findViewById(R.id.li_playlist);
        tv_playlist_all = rootView.findViewById(R.id.tv_playlist_all);
        tv_playlist_all.setOnClickListener(this);
        rv_playlist = rootView.findViewById(R.id.rv_playlist);
        rv_playlist.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_favorite = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_playlist.setHasFixedSize(true);
        rv_playlist.setLayoutManager(linearLayoutManager_favorite);
        /******************Artists**************/
        li_artist = rootView.findViewById(R.id.li_artist);
        tv_artist_all = rootView.findViewById(R.id.tv_artist_all);
        tv_artist_all.setOnClickListener(this);
        rv_artist = rootView.findViewById(R.id.rv_artist);
        rv_artist.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager_rank = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_artist.setHasFixedSize(true);
        rv_artist.setLayoutManager(linearLayoutManager_rank);
        
        getData();

        return rootView;
    }
    public void updateAdapter(int pos, String value){
        TrackModel tmp = track_list.get(pos);
        tmp.setFavorite(value);
        track_list.set(pos, tmp);
        trackAdapter.notifyDataSetChanged();
    }
    public TrackAdapter getAdapter(){
        return trackAdapter;
    }
    private void getData(){
        track_list.clear();
        album_list.clear();
        play_list.clear();
        artist_list.clear();

        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        Log.e("member_key:", member_key);
        if (App.isNetworkAvailable(getContext())) {
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH_MAIN)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("memberKey", member_key)
                    .setBodyParameter("id", search_id)
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

                                        if (trackArray.length() == 0){
                                            track_list.clear();
                                            li_track.setVisibility(View.GONE);
                                        }else {
                                            li_track.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < trackArray.length(); i++) {
                                                TrackModel trackModel = gson.fromJson(trackArray.getJSONObject(i).toString(), TrackModel.class);
                                                track_list.add(trackModel);
                                            }
                                            createTrackRV(track_list);
                                        }
                                        

                                        JSONArray albumArray = api_result.getJSONArray("album_list");

                                        if (albumArray.length() == 0){
                                            album_list.clear();
                                            li_album.setVisibility(View.GONE);
                                        }else {
                                            li_album.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < albumArray.length(); i++) {
                                                String p_id = albumArray.getJSONObject(i).getString("list_id");
                                                String title = albumArray.getJSONObject(i).getString("title");
                                                String tracklist = albumArray.getJSONObject(i).getString("tracklist");
                                                String cover_image = albumArray.getJSONObject(i).getString("cover_image");
                                                String description = albumArray.getJSONObject(i).getString("artist_name");
                                                AlbumModel albumModel = new AlbumModel();
                                                albumModel.setList_id(p_id);
                                                albumModel.setTitle(title);
                                                albumModel.setTracklist(tracklist);
                                                albumModel.setDescription(description);
                                                albumModel.setCover_image(cover_image);
                                                album_list.add(albumModel);
                                            }
                                            createAlbumRV(album_list);
                                        }
                                        
                                        
                                        JSONArray playlistArray = api_result.getJSONArray("play_list");
                                        if (playlistArray.length() == 0){
                                            play_list.clear();
                                            li_playlist.setVisibility(View.GONE);
                                        }else {
                                            li_playlist.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < playlistArray.length(); i++) {
                                                String list_id = playlistArray.getJSONObject(i).getString("list_id");
                                                String title = playlistArray.getJSONObject(i).getString("title");
                                                String cover_image = playlistArray.getJSONObject(i).getString("cover_image");
                                                String description = playlistArray.getJSONObject(i).getString("user_name");
                                                String track_list = playlistArray.getJSONObject(i).getString("tracklist");
                                                AlbumModel topModel = new AlbumModel();
                                                topModel.setList_id(list_id);
                                                topModel.setTitle(title);
                                                topModel.setCover_image(cover_image);
                                                topModel.setDescription(description);
                                                topModel.setTracklist(track_list);
                                                play_list.add(topModel);
                                            }
                                            createPlaylistRV(play_list);
                                        }


                                        JSONArray artistArray = api_result.getJSONArray("artist_list");
                                        if (artistArray.length() == 0){
                                            artist_list.clear();
                                            li_artist.setVisibility(View.GONE);
                                        }else {
                                            li_artist.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < artistArray.length(); i++) {
                                                String list_id = artistArray.getJSONObject(i).getString("artist_id");
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
                                            createArtistRV(artist_list);
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
    private void createTrackRV(ArrayList<TrackModel> songModels){
        trackAdapter = new TrackAdapter(getContext(), songModels,getFragmentManager());
        trackAdapter.notifyDataSetChanged();
        rv_track.setAdapter(trackAdapter);
    }
    private void createAlbumRV(ArrayList<AlbumModel> album_list){
        albumAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "album", "common");
        albumAdapter.notifyDataSetChanged();
        rv_album.setAdapter(albumAdapter);
        albumAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "top", "common");
        albumAdapter.notifyDataSetChanged();
        rv_album.setAdapter(albumAdapter);
        albumAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = album_list.get(rv_album.getChildAdapterPosition(view)).getList_id();
                String title = album_list.get(rv_album.getChildAdapterPosition(view)).getTitle();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("type", "search_album");
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }
    private void createPlaylistRV(ArrayList<AlbumModel> album_list){
        playlistAdapter = new AlbumAdapter(getContext(), album_list,getFragmentManager(), "top", "common");
        playlistAdapter.notifyDataSetChanged();
        rv_playlist.setAdapter(playlistAdapter);
        playlistAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = play_list.get(rv_playlist.getChildAdapterPosition(view)).getList_id();
                String title = play_list.get(rv_playlist.getChildAdapterPosition(view)).getTitle();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
                bundle.putString("type", "search_playlist");
                frag.setArguments(bundle);
                pushFragment(frag, true, TrackDetailFragment.class.getName());
            }
        });
    }
    private void createArtistRV(ArrayList<AlbumModel> play_list){
        artistAdapter = new AlbumAdapter(getContext(), play_list,getFragmentManager(), "artist", "common");
        artistAdapter.notifyDataSetChanged();
        rv_artist.setAdapter(artistAdapter);
        rv_artist.setAdapter(artistAdapter);
        artistAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = artist_list.get(rv_artist.getChildAdapterPosition(view)).getList_id();
                String title = artist_list.get(rv_artist.getChildAdapterPosition(view)).getTitle();

                Bundle bundle = new Bundle();
                TrackDetailFragment frag = new TrackDetailFragment();
                bundle.putString("id", id);
                bundle.putString("title", title);
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
                bundle.putString("id", search_id);
                bundle.putString("type", "track");
                bundle.putString("image", "search_main");
                frag0.setArguments(bundle);
                pushFragment(frag0, true, TrackDetailFragment.class.getName());
                break;
            case R.id.tv_playlist_all:
                Fragment frag2 = null;
                frag2 = new SelectAllFragment();
                Bundle param2 = new Bundle();
                param2.putString("type", "search");
                param2.putString("search_type", "playlist");
                param2.putString("keyword", search_id);
                frag2.setArguments(param2);
                pushFragment(frag2, true, SelectAllFragment.class.getName());
                break;
            case R.id.tv_album_all:
                Fragment frag = null;
                frag = new SelectAllFragment();
                Bundle param = new Bundle();
                param.putString("type", "search");
                param.putString("search_type", "album");
                param.putString("keyword", search_id);
                frag.setArguments(param);
                pushFragment(frag, true, SelectAllFragment.class.getName());
                break;
            case R.id.tv_artist_all:
                Fragment frag1 = null;
                frag1 = new SelectAllFragment();
                Bundle param1 = new Bundle();
                param1.putString("type", "search");
                param1.putString("search_type", "artist");
                param1.putString("keyword", search_id);
                frag1.setArguments(param1);
                pushFragment(frag1, true, SelectAllFragment.class.getName());
                break;
            case R.id.back_btn:
                popFragment();
                break;
            case R.id.main_shuffle:
                if(track_list != null){
                    ((MainActivity)getContext()).setTrackList(track_list);
                    ((MainActivity)getContext()).initPlayer(0);
                    ((MainActivity)getContext()).current_playing_type = "detail";
                }
                break;
            case R.id.toolbar_shuffle:
                if(track_list != null){
                    ((MainActivity)getContext()).setTrackList(track_list);
                    ((MainActivity)getContext()).initPlayer(0);
                    ((MainActivity)getContext()).current_playing_type = "detail";
                }
                break;
        }
    }
}
