package com.uru.hudba.fragment;

import android.os.Bundle;
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
import com.uru.hudba.adapter.TrackAdapter;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class TrackDetailFragment extends Fragment {
    private View rootView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CardView toolbarShuffle, mainShuffle;
    private ImageView content_image, backBtn;
    private TextView tvTitle, tvContent, txt_song_count;
    private LinearLayout li_content;
    private RelativeLayout li_search;
    private RecyclerView rv_library;

    private String p_listId = "", title = "", type = "", image_url = "";
    private ArrayList<TrackModel> trackList = new ArrayList<>();
    private TrackAdapter homeAdapter;
    private SearchView searchTxt;

    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    public TrackAdapter getAdapter(){
        return homeAdapter;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "track_detail";
        rootView = inflater.inflate(R.layout.fragment_track_detail, container, false);
        try {
            p_listId = getArguments().getString("id");
            title = getArguments().getString("title");
            type = getArguments().getString("type");
            image_url = getArguments().getString("image");
        }catch (Exception e){
            e.printStackTrace();
        }
        trackList.clear();
        initView();
        if(!TextUtils.isEmpty(image_url) && image_url.equalsIgnoreCase("search_track")){
            getSearchTrackAll();
        }else{
            getData();
        }



        return rootView;
    }

    private void initView() {
        appBarLayout = rootView.findViewById(R.id.app_bar);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbarShuffle = rootView.findViewById(R.id.toolbar_shuffle);
        mainShuffle = rootView.findViewById(R.id.main_shuffle);
        li_search = rootView.findViewById(R.id.li_search);
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
        tvContent = rootView.findViewById(R.id.content_title);
        tvTitle = rootView.findViewById(R.id.toolbar_title);
        rv_library = rootView.findViewById(R.id.rv_library);
        txt_song_count = rootView.findViewById(R.id.txt_song_count);
        li_content = rootView.findViewById(R.id.li_content);
        content_image = rootView.findViewById(R.id.imageview);

        try {
            tvTitle.setText(title);
            tvContent.setText(title);
        }catch (Exception e){
            e.printStackTrace();
        }

        String url = image_url;

        Glide.with(getActivity())
                .load(url)
                .placeholder(R.drawable.aaa)
                .into(content_image);

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

        searchTxt = rootView.findViewById(R.id.search_et);
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
                    .load(GlobalConstants.TRACK_DETAIL_LIST)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("list_id", p_listId)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("type", type)
                    .setBodyParameter("title", title)
                    .setBodyParameter("image", image_url)
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

                                        if (articleArray.length() == 0){
                                            trackList.clear();
                                        }else {
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                TrackModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), TrackModel.class);
                                                trackList.add(topModel);
                                            }
                                            createRV(trackList);
                                        }
                                        txt_song_count.setText(String.format(getResources().getString(R.string.song_count), trackList.size()));
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
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void getSearchTrackAll(){
        trackList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");

        if (App.isNetworkAvailable(getContext())) {
            CommonUtils.showLoadingDialog(getActivity());
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("keyword", p_listId)
                    .setBodyParameter("type", type)
                    .setBodyParameter("user_id", user_id)
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
                                        JSONArray articleArray = null;
                                        if(type.equalsIgnoreCase("track")){
                                           articleArray = api_result.getJSONArray("track_list");
                                        }else if(type.equalsIgnoreCase("album")){
                                            articleArray = api_result.getJSONArray("album_list");
                                        }else if(type.equalsIgnoreCase("artist")){
                                            articleArray = api_result.getJSONArray("artist_list");
                                        }

                                        Gson gson = new Gson();

                                        if (articleArray.length() == 0){
                                            trackList.clear();
                                        }else {
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                TrackModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), TrackModel.class);
                                                trackList.add(topModel);
                                            }
                                            createRV(trackList);
                                        }
                                        txt_song_count.setText(String.format(getResources().getString(R.string.song_count), trackList.size()));
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
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void createRV(ArrayList<TrackModel> songModels){
        homeAdapter = new TrackAdapter(getContext(), songModels,getFragmentManager());
        homeAdapter.notifyDataSetChanged();
        rv_library.setAdapter(homeAdapter);
    }
    public void updateAdapter(int pos, String value){
        TrackModel tmp = trackList.get(pos);
        tmp.setFavorite(value);
        trackList.set(pos, tmp);
        homeAdapter.notifyDataSetChanged();
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
