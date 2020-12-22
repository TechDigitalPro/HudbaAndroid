package com.uru.hudba.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.SavedTrackAdapter;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class SavedFragment extends Fragment {
    private View rootView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CardView toolbarShuffle, mainShuffle;
    private ImageView content_image, backBtn;
    private TextView tvContentTitle, tvTitle;
    private LinearLayout li_content;
    private RelativeLayout li_search;
    private RecyclerView rv_library;

    private ArrayList<TrackModel> trackList = new ArrayList<>();
    private SavedTrackAdapter homeAdapter;
    private int saved_song_cnt = 0;
    private CollapsingToolbarLayoutState state;
    private SearchView searchTxt;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    public SavedTrackAdapter getAdapter(){
        return homeAdapter;
    }
    private String type = "favorite";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "saved_song";
        rootView = inflater.inflate(R.layout.fragment_saved_song, container, false);
        trackList.clear();

        type = getArguments().getString("type");

        initView();
        getData();
        return rootView;
    }

    private void initView() {
        appBarLayout = rootView.findViewById(R.id.app_bar);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbarShuffle = rootView.findViewById(R.id.toolbar_shuffle);
        mainShuffle = rootView.findViewById(R.id.main_shuffle);
        toolbarShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackList.size() != 0){
                    ((MainActivity)getContext()).setTrackList(trackList);
                    ((MainActivity)getContext()).initPlayer(0);
                    ((MainActivity)getContext()).current_playing_type = "detail";
                }
            }
        });
        mainShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackList.size() != 0){
                    ((MainActivity)getContext()).setTrackList(trackList);
                    ((MainActivity)getContext()).initPlayer(0);
                    ((MainActivity)getContext()).current_playing_type = "detail";
                }
            }
        });
        backBtn = rootView.findViewById(R.id.back_btn);
        tvTitle = rootView.findViewById(R.id.toolbar_title);
        tvContentTitle = rootView.findViewById(R.id.content_title);
        rv_library = rootView.findViewById(R.id.rv_library);
        li_content = rootView.findViewById(R.id.li_content);
        li_search = rootView.findViewById(R.id.li_search);
        content_image = rootView.findViewById(R.id.imageview);




        rv_library.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_library.setHasFixedSize(true);
        rv_library.setLayoutManager(linearLayoutManager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popFragment();
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//Modify the status token to expand
                        tvTitle.setVisibility(View.GONE);
                        toolbarShuffle.setVisibility(View.INVISIBLE);
                        li_content.setVisibility(View.VISIBLE);
                        li_search.setVisibility(View.VISIBLE);
                        mainShuffle.setVisibility(View.VISIBLE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        mainShuffle.setVisibility(View.GONE);//Hide Play Button
                        toolbarShuffle.setVisibility(View.VISIBLE);
                        state = CollapsingToolbarLayoutState.COLLAPSED;//Modified status marked as folded
                        tvTitle.setVisibility(View.VISIBLE);
                        li_content.setVisibility(View.GONE);
                        li_search.setVisibility(View.GONE);
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if(state == CollapsingToolbarLayoutState.COLLAPSED){
                            mainShuffle.setVisibility(View.GONE);//Hide Play Button When Changed from Folding to Intermediate State
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//Modify the status tag to the middle
                    }
                }
            }
        });
        searchTxt = rootView.findViewById(R.id.search_txt);
        searchTxt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(homeAdapter != null){
                    homeAdapter.searchItems(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(homeAdapter != null){
                    homeAdapter.searchItems(newText);
                }
                return false;
            }
        });
        searchTxt.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        searchTxt.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
    }
    private void getData(){
        trackList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");

        if (App.isNetworkAvailable(getContext())) {
            CommonUtils.showLoadingDialog(getActivity());
            Ion.with(getContext())
                    .load(GlobalConstants.SAVED_LIST)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("type", type)
                    .setBodyParameter("memberKey", member_key)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                try {
                                    CommonUtils.hideLoadingDialog();
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        JSONArray articleArray = api_result.getJSONArray("list");
                                        Gson gson = new Gson();
                                        saved_song_cnt = articleArray.length();

                                        if (saved_song_cnt == 0){
                                            trackList.clear();
                                        }else {
                                            for (int i = 0; i < saved_song_cnt; i++) {
                                                TrackModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), TrackModel.class);
                                                trackList.add(topModel);
                                            }
                                            createRV(trackList);
                                        }
                                        setTextTitle();
                                        saveData();
                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_server_error);
                                CommonUtils.hideLoadingDialog();
                            }
                        }
                    });
        } else {
            trackList.clear();
            String home_str = App.readSharedSetting("saved_data", "");
            if(!TextUtils.isEmpty(home_str)){
                try {
                    Gson gson = new Gson();
                    JSONArray download_homedata_array = new JSONArray(home_str);
                    saved_song_cnt = download_homedata_array.length();

                    if (saved_song_cnt == 0){
                        trackList.clear();
                    }else {
                        for (int i = 0; i < saved_song_cnt; i++) {
                            TrackModel home = gson.fromJson(download_homedata_array.getJSONObject(i).toString(), TrackModel.class);
                            trackList.add(home);
                        }
                        createRV(trackList);
                    }
                    setTextTitle();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void createRV(ArrayList<TrackModel> songModels){
        homeAdapter = new SavedTrackAdapter(getContext(), songModels,getFragmentManager());
        homeAdapter.notifyDataSetChanged();
        rv_library.setAdapter(homeAdapter);
    }
    private void setTextTitle(){
        if(type.equalsIgnoreCase("download")){
            tvContentTitle.setText(getResources().getString(R.string.saved_song) + " - " +saved_song_cnt);
            tvTitle.setText(getResources().getString(R.string.saved_song) + " - " +saved_song_cnt);
        }else{
            tvContentTitle.setText(getResources().getString(R.string.favorite_track) + " - " +saved_song_cnt);
            tvTitle.setText(getResources().getString(R.string.favorite_track) + " - " +saved_song_cnt);
        }

    }
    private void saveData(){
        Gson tmp_gson = new Gson();
        String down_home_data = tmp_gson.toJson(trackList);
        JsonArray down_homejsonArray = new JsonParser().parse(down_home_data).getAsJsonArray();
        String tmp_down_home_str = down_homejsonArray.toString();
        App.saveSharedSetting("saved_data", tmp_down_home_str);
    }
    public void removeDownload(int pos, String type){
        try {
            String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
            String user_id = App.readSharedSetting(PRE_USER_ID, "");

            if (App.isNetworkAvailable(getContext())) {
                Ion.with(getContext())
                        .load(GlobalConstants.DELETE_SAVED_SONG + member_key)
                        .setTimeout(CommonUtils.NETWORK_TIME)
                        .setBodyParameter("user_id", user_id)
                        .setBodyParameter("track_id", trackList.get(pos).getTrack_id())
                        .setBodyParameter("type", type)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e == null) {
                                    try {
                                        JSONObject resultObj = new JSONObject(result);
                                        if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                            String img_folder = getActivity().getFilesDir().getAbsolutePath() + File.separator + "image";
                                            File img_file = new File(img_folder, trackList.get(pos).getTrack_id() + ".jpg");
                                            if(img_file.exists()) {
                                                img_file.delete();
                                            }
                                            String mp3_folder = getActivity().getFilesDir().getAbsolutePath() + File.separator + "video";
                                            File mp3_file = new File(mp3_folder, trackList.get(pos).getTrack_id() + ".mp3");
                                            if(mp3_file.exists()) {
                                                mp3_file.delete();
                                            }
                                            trackList.remove(pos);
                                            homeAdapter.notifyDataSetChanged();
                                            saveData();
                                            saved_song_cnt = trackList.size();
                                            setTextTitle();
                                            ((MainActivity)getActivity()).setTrackList(trackList);
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
        } catch (Exception e) {
            ((MainActivity)getActivity()).showErrorMessages(e.getMessage());
        }


    }
    public void renameSong(int pos){
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dlg_playlist_add, null);

            final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            dlg.setCanceledOnTouchOutside(true);
            final TextView ok = dialogView.findViewById(R.id.ok);
            final TextView cancel = dialogView.findViewById(R.id.cancel);
            final TextView title = dialogView.findViewById(R.id.title);
            final EditText et_title = dialogView.findViewById(R.id.et_title);
            final EditText et_subtitle = dialogView.findViewById(R.id.et_subtitle);
            title.setText("Rename song");
            et_title.setText(trackList.get(pos).getTitle());
            et_subtitle.setText(trackList.get(pos).getArtist_name());
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(et_title.getText().toString().trim()) && !TextUtils.isEmpty(et_subtitle.getText().toString().trim())){
                        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
                        String user_id = App.readSharedSetting(PRE_USER_ID, "");

                        if (App.isNetworkAvailable(getContext())) {
                            Ion.with(getContext())
                                    .load(GlobalConstants.RENAME_SONG + member_key)
                                    .setTimeout(CommonUtils.NETWORK_TIME)
                                    .setBodyParameter("user_id", user_id)
                                    .setBodyParameter("track_id", trackList.get(pos).getTrack_id())
                                    .setBodyParameter("title", et_title.getText().toString())
                                    .setBodyParameter("artist", et_subtitle.getText().toString())
                                    .asString()
                                    .setCallback(new FutureCallback<String>() {
                                        @Override
                                        public void onCompleted(Exception e, String result) {
                                            if (e == null) {
                                                try {
                                                    JSONObject resultObj = new JSONObject(result);
                                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                        TrackModel tmp = trackList.get(pos);
                                                        tmp.setTitle(et_title.getText().toString());
                                                        tmp.setArtist_name(et_subtitle.getText().toString());
                                                        trackList.set(pos, tmp);
                                                        homeAdapter.notifyDataSetChanged();
                                                        ((MainActivity)getActivity()).showMessages("Song name changed");
                                                    } else {
                                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                                    }
                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }
                                            } else {
                                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_server_error);
                                            }
                                            dlg.dismiss();
                                        }
                                    });
                        } else {
                            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
                        }
                    }else{
                        ((MainActivity)getContext()).showErrorMessages("Please confirm fields");
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });

            dlg.setCanceledOnTouchOutside(false);
            dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dlg.show();
        }catch (Exception ep){
            ep.printStackTrace();
        }

    }

    public void updateAdapter(int pos, String value){
        try {
            TrackModel tmp = trackList.get(pos);
            tmp.setFavorite(value);
            trackList.set(pos, tmp);
            homeAdapter.notifyDataSetChanged();
            saved_song_cnt = trackList.size();
            setTextTitle();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean popFragment() {
        try {

            boolean isPop = false;
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                isPop = true;
                getActivity().getSupportFragmentManager().popBackStack();
            }
            return isPop;

        } catch (Exception e) {
            return false;
        }
    }
}
