package com.uru.hudba.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.AlbumAdapter;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.AlbumModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class SelectAllFragment extends BaseFragment {
    private View rootView;
    private ImageView backBtn;
    private TextView tvTitle;
    private RecyclerView rv_all;
    private AutoCompleteTextView searchTxt;

    private String type = "", data_type = "", keyword = "";
    private ArrayList<AlbumModel> albumList = new ArrayList<>();
    private AlbumAdapter homeAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "select_all";
        rootView = inflater.inflate(R.layout.fragment_selectall, container, false);
        try {
            type = getArguments().getString("type");
        }catch (Exception e){
            e.printStackTrace();
        }
        initView();
        try {
            if(!TextUtils.isEmpty(getArguments().getString("data_type"))){
                data_type = getArguments().getString("data_type");
                keyword = getArguments().getString("keyword");
                type = data_type;
                getSearchData();
                if(data_type.equalsIgnoreCase("album")){
                    tvTitle.setText("Album - " + keyword);
                }else if(data_type.equalsIgnoreCase("artist")){
                    tvTitle.setText("Artist - " + keyword);
                }

            }else if(!TextUtils.isEmpty(getArguments().getString("search_type"))){
                data_type = getArguments().getString("search_type");
                keyword = getArguments().getString("keyword");
                type = data_type;
                getSearchMainData();
                if(data_type.equalsIgnoreCase("album")){
                    tvTitle.setText("Albums");
                }else if(data_type.equalsIgnoreCase("artist")){
                    tvTitle.setText("Artists");
                }else if(data_type.equalsIgnoreCase("playlist")){
                    tvTitle.setText("Playlists");
                }

            }else{
                getData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(){
        backBtn = rootView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popFragment();
            }
        });
        tvTitle = rootView.findViewById(R.id.toolbar_title);
        rv_all = rootView.findViewById(R.id.rv_all);
        rv_all.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if(type.equalsIgnoreCase("top")){
            tvTitle.setText(R.string.txt_top_playlist);
        }else if(type.equalsIgnoreCase("ranking")){
            tvTitle.setText(R.string.ranking);
        }
        searchTxt = rootView.findViewById(R.id.search_txt);
        searchTxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(homeAdapter != null){
                    homeAdapter.searchItems(s);
                }


            }
        });
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == 666) {
                    if(homeAdapter != null){
                        homeAdapter.searchItems(searchTxt.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        searchTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchTxt.getRight() - searchTxt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        searchTxt.setText("");
                        searchTxt.clearFocus();
                        if(homeAdapter != null){
                            homeAdapter.searchItems("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });


    }
    private void getData(){
        albumList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        CommonUtils.showLoadingDialog(getActivity());
        if (App.isNetworkAvailable(getContext())) {
            Ion.with(getContext())
                    .load(GlobalConstants.SELECT_ALL_LIST)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("type", type)
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
                                            albumList.clear();
                                        }else {
                                            if(type.equalsIgnoreCase("top")){
                                                for (int i = 0; i < articleArray.length(); i++) {
                                                    String a = articleArray.getJSONObject(i).toString();
                                                    AlbumModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), AlbumModel.class);
                                                    albumList.add(topModel);
                                                }
                                            }else if(type.equalsIgnoreCase("ranking")){
                                                for (int i = 0; i < articleArray.length(); i++) {
                                                    String list_id = articleArray.getJSONObject(i).getString("playlist_id");
                                                    String title = articleArray.getJSONObject(i).getString("country");
                                                    String cover_image = articleArray.getJSONObject(i).getString("image");
                                                    AlbumModel topModel = new AlbumModel();
                                                    topModel.setList_id(list_id);
                                                    topModel.setTitle(title);
                                                    topModel.setCover_image(cover_image);
                                                    albumList.add(topModel);
                                                }
                                            }
                                            createRV(albumList);
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
            CommonUtils.hideLoadingDialog();
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void getSearchData(){
        albumList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        CommonUtils.showLoadingDialog(getActivity());
        if (App.isNetworkAvailable(getContext())) {
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("keyword", keyword)
                    .setBodyParameter("type", data_type)
                    .setBodyParameter("user_id", user_id)
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
                                        JSONArray articleArray = null;
                                        if(data_type.equalsIgnoreCase("album")){
                                            articleArray = api_result.getJSONArray("album_list");
                                        }else if(data_type.equalsIgnoreCase("artist")){
                                            articleArray = api_result.getJSONArray("artist_list");
                                        }

                                        Gson gson = new Gson();

                                        if (articleArray.length() == 0){
                                            albumList.clear();
                                        }else {
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                AlbumModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), AlbumModel.class);
                                                albumList.add(topModel);
                                            }
                                            createRV(albumList);
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
            CommonUtils.hideLoadingDialog();
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void getSearchMainData(){
        albumList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        CommonUtils.showLoadingDialog(getActivity());
        if (App.isNetworkAvailable(getContext())) {
            Ion.with(getContext())
                    .load(GlobalConstants.SEARCH_MAIN)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("id", keyword)
                    .setBodyParameter("type", data_type)
                    .setBodyParameter("user_id", user_id)
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
                                        JSONArray articleArray = null;
                                        if(data_type.equalsIgnoreCase("album")){
                                            articleArray = api_result.getJSONArray("album_list");
                                        }else if(data_type.equalsIgnoreCase("artist")){
                                            articleArray = api_result.getJSONArray("artist_list");
                                        }else if(data_type.equalsIgnoreCase("playlist")){
                                            articleArray = api_result.getJSONArray("play_list");
                                        }

                                        Gson gson = new Gson();

                                        if (articleArray.length() == 0){
                                            albumList.clear();
                                        }else {
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                AlbumModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), AlbumModel.class);
                                                albumList.add(topModel);
                                            }
                                            createRV(albumList);
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
            CommonUtils.hideLoadingDialog();
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void createRV(ArrayList<AlbumModel> songModels){
        homeAdapter = new AlbumAdapter(getContext(), songModels,getFragmentManager(), type, "select_all");
        homeAdapter.notifyDataSetChanged();
        rv_all.setAdapter(homeAdapter);
        homeAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(getArguments().getString("data_type"))){
                    if(getArguments().getString("data_type").equalsIgnoreCase("album")){
                        String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                        String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();
                        String image = albumList.get(rv_all.getChildAdapterPosition(view)).getCover_image();

                        Bundle bundle = new Bundle();
                        TrackDetailFragment frag = new TrackDetailFragment();
                        bundle.putString("id", id);
                        bundle.putString("title", title);
                        bundle.putString("image", image);
                        bundle.putString("type", "search_album");
                        frag.setArguments(bundle);
                        pushFragment(frag, true, TrackDetailFragment.class.getName());
                    }else if(getArguments().getString("data_type").equalsIgnoreCase("artist")){
                        String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                        String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();
                        String image = albumList.get(rv_all.getChildAdapterPosition(view)).getCover_image();
                        Bundle bundle = new Bundle();
                        TrackDetailFragment frag = new TrackDetailFragment();
                        bundle.putString("id", id);
                        bundle.putString("title", title);
                        bundle.putString("image", image);
                        bundle.putString("type", "search_artist");
                        frag.setArguments(bundle);
                        pushFragment(frag, true, TrackDetailFragment.class.getName());
                    }
                }else if(!TextUtils.isEmpty(getArguments().getString("search_type"))){
                    if(getArguments().getString("search_type").equalsIgnoreCase("album")){
                        String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                        String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();
                        String image = albumList.get(rv_all.getChildAdapterPosition(view)).getCover_image();

                        Bundle bundle = new Bundle();
                        TrackDetailFragment frag = new TrackDetailFragment();
                        bundle.putString("id", id);
                        bundle.putString("title", title);
                        bundle.putString("image", image);
                        bundle.putString("type", "search_album");
                        frag.setArguments(bundle);
                        pushFragment(frag, true, TrackDetailFragment.class.getName());
                    }else if(getArguments().getString("search_type").equalsIgnoreCase("artist")){
                        String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                        String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();
                        String image = albumList.get(rv_all.getChildAdapterPosition(view)).getCover_image();
                        Bundle bundle = new Bundle();
                        TrackDetailFragment frag = new TrackDetailFragment();
                        bundle.putString("id", id);
                        bundle.putString("title", title);
                        bundle.putString("image", image);
                        bundle.putString("type", "search_artist");
                        frag.setArguments(bundle);
                        pushFragment(frag, true, TrackDetailFragment.class.getName());
                    }else if(getArguments().getString("search_type").equalsIgnoreCase("playlist")){
                        String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                        String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();
                        String image = albumList.get(rv_all.getChildAdapterPosition(view)).getCover_image();
                        Bundle bundle = new Bundle();
                        TrackDetailFragment frag = new TrackDetailFragment();
                        bundle.putString("id", id);
                        bundle.putString("title", title);
                        bundle.putString("image", image);
                        bundle.putString("type", "search_playlist");
                        frag.setArguments(bundle);
                        pushFragment(frag, true, TrackDetailFragment.class.getName());
                    }
                }else{
                    String id = albumList.get(rv_all.getChildAdapterPosition(view)).getList_id();
                    String title = albumList.get(rv_all.getChildAdapterPosition(view)).getTitle();

                    Bundle bundle = new Bundle();
                    TrackDetailFragment frag = new TrackDetailFragment();
                    bundle.putString("id", id);
                    bundle.putString("title", title);
                    bundle.putString("type", type);
                    frag.setArguments(bundle);
                    pushFragment(frag, true, TrackDetailFragment.class.getName());
                }

            }
        });
    }
}
