package com.uru.hudba.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.PlayListAdapter;
import com.uru.hudba.adapter.TrackAdapter;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.PlayListModel;
import com.uru.hudba.model.PlayListModel;
import com.uru.hudba.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class LibraryFragment extends BaseFragment implements View.OnClickListener{
    private View rootView;
    private AppBarLayout main_layout;
    private TextView txt_song_cnt, txt_artist_cnt;
    private ImageView btn_profile, img_filter_playlist;
    private RoundedImageView saved_song_image;
    private RecyclerView rv_playlist;
    private RelativeLayout add_playlist;
    private PlayListAdapter playlistAdapter;
    private LinearLayout li_saved, li_playlist, li_list;
    private AutoCompleteTextView searchTxt;
    private ArrayList<PlayListModel> playList = new ArrayList<>();
    private int saved_song_cnt = 0;
    private String tmp_image_path = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "library";
        rootView = inflater.inflate(R.layout.fragment_library, container, false);
        main_layout = rootView.findViewById(R.id.parent);
        btn_profile = rootView.findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(this);
        txt_song_cnt = rootView.findViewById(R.id.song_cnt);
        txt_song_cnt.setText(saved_song_cnt + " songs");
        txt_artist_cnt = rootView.findViewById(R.id.artist_cnt);
        li_saved = rootView.findViewById(R.id.li_saved);
        li_saved.setOnClickListener(this);
        li_playlist = rootView.findViewById(R.id.li_playlist);
        li_list = rootView.findViewById(R.id.li_list);
        img_filter_playlist = rootView.findViewById(R.id.filter_playlist);
        rv_playlist = rootView.findViewById(R.id.rv_playlist);
        add_playlist = rootView.findViewById(R.id.add_playlist);
        saved_song_image = rootView.findViewById(R.id.saved_song_image);
        add_playlist.setOnClickListener(this);

        rv_playlist.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_playlist.setHasFixedSize(true);
        rv_playlist.setLayoutManager(linearLayoutManager);

        searchTxt = rootView.findViewById(R.id.search_txt);
        searchTxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(playlistAdapter != null){
                    playlistAdapter.searchItems(s);
                }


            }
        });
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == 666) {
                    if(playlistAdapter != null){
                        playlistAdapter.searchItems(searchTxt.getText().toString());
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
                        if(playlistAdapter != null){
                            playlistAdapter.searchItems("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        getData();
        return rootView;
    }

    private void getData(){
        playList.clear();
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        if (App.isNetworkAvailable(getContext())) {
            li_list.setVisibility(View.VISIBLE);
            li_playlist.setVisibility(View.VISIBLE);
            Ion.with(getContext())
                    .load(GlobalConstants.MYPLAY_LIST + member_key)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        try {
                                            tmp_image_path = api_result.getString("image_path");
                                            saved_song_cnt = api_result.getInt("count");
                                            txt_song_cnt.setText(saved_song_cnt + " songs");
                                            App.saveSharedSetting("song_cnt", String.valueOf(saved_song_cnt));
                                            App.saveSharedSetting("image_path", tmp_image_path);
                                            if (!TextUtils.isEmpty(tmp_image_path)) {
                                                Glide.with(getContext())
                                                        .load(tmp_image_path)
                                                        .override(180, 180).placeholder(R.drawable.aaa)
                                                        .into(saved_song_image);
                                            }
                                        }catch (Exception e1){
                                            e1.printStackTrace();
                                        }

                                        JSONArray articleArray = api_result.getJSONArray("list");
                                        Gson gson = new Gson();

                                        if (articleArray.length() == 0){
                                            playList.clear();
                                        }else {
                                            playList.clear();
                                            for (int i = 0; i < articleArray.length(); i++) {
                                                PlayListModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), PlayListModel.class);
                                                playList.add(topModel);
                                            }
                                            createRV(playList);
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
            playList.clear();
            li_list.setVisibility(View.GONE);
            li_playlist.setVisibility(View.GONE);
            saved_song_cnt = Integer.valueOf(App.readSharedSetting("song_cnt", ""));
            tmp_image_path = App.readSharedSetting("image_path", "");
            txt_song_cnt.setText(saved_song_cnt + " songs");
            if (!TextUtils.isEmpty(tmp_image_path)) {
                Glide.with(getContext())
                        .load(tmp_image_path)
                        .override(180, 180).placeholder(R.drawable.aaa)
                        .into(saved_song_image);
            }
        }
    }
    private void createRV(ArrayList<PlayListModel> songModels){
        playlistAdapter = new PlayListAdapter(getContext(), songModels);
        playlistAdapter.notifyDataSetChanged();
        rv_playlist.setAdapter(playlistAdapter);
    }
    public void updateAdapter(int pos, PlayListModel model){
        try {
            if(model == null){
                playList.remove(pos);
                playlistAdapter.notifyDataSetChanged();
            }else{
                playlistAdapter.setItem(pos, model);
                playList.set(pos, model);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void addPlayListDlg(){
        View dialogView = getLayoutInflater().inflate(R.layout.dlg_playlist_add, null);

        final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dlg.setCanceledOnTouchOutside(true);
        final TextView ok = dialogView.findViewById(R.id.ok);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final EditText et_title = dialogView.findViewById(R.id.et_title);
        final EditText et_subtitle = dialogView.findViewById(R.id.et_subtitle);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_title.getText().toString().trim()) && !TextUtils.isEmpty(et_subtitle.getText().toString().trim())){
                    String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
                    String user_id = App.readSharedSetting(PRE_USER_ID, "");

                    if (App.isNetworkAvailable(getContext())) {
                        Ion.with(getContext())
                                .load(GlobalConstants.CREATE_PLAYLIST + member_key)
                                .setTimeout(CommonUtils.NETWORK_TIME)
                                .setBodyParameter("user_id", user_id)
                                .setBodyParameter("title", et_title.getText().toString())
                                .setBodyParameter("subtitle", et_subtitle.getText().toString())
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if (e == null) {
                                            try {
                                                JSONObject resultObj = new JSONObject(result);
                                                if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                    ((MainActivity)getActivity()).showMessages("Playlist added successfully");
                                                    getData();
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
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_playlist:
                addPlayListDlg();
                break;
            case R.id.btn_profile:
                ((MainActivity) getActivity()).onClickAccount();
                break;
            case R.id.li_saved:
                Bundle bundle = new Bundle();
                bundle.putString("type", "download");
                SavedFragment frag = new SavedFragment();
                frag.setArguments(bundle);
                pushFragment(frag, true, SavedFragment.class.getName());
                break;
        }
    }
}