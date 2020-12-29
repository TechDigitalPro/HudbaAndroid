package com.uru.hudba.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.TrackAdapter;
import com.uru.hudba.adapter.UserTrackAdapter;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.PlayListModel;
import com.uru.hudba.model.TrackModel;
import com.uru.hudba.model.UserTrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class PlayListBottomFragment extends SuperBottomSheetFragment implements View.OnClickListener {
    public PlayListBottomFragment(int pos, PlayListModel item){
        this.position = pos;
        this.m_playlist = item;
    }

    private View rootView;
    private ImageView img_content, img_add, img_remove, img_filter, img_rename;
    private TextView txt_title, txt_subtitle;

    private int position;
    private PlayListModel m_playlist;
    private String playlist_id;
    private String content_image_url, title, subtitle;
    private LinearLayout li_add, li_rename, li_filter, li_remove;

    private RecyclerView rv_list;
    private ArrayList<UserTrackModel> trackList = new ArrayList<>();
    private int track_list_cnt = 0;
    private UserTrackAdapter userTrackAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_playlist_bottom,container,false);
        img_content = rootView.findViewById(R.id.image);
        txt_subtitle = rootView.findViewById(R.id.artist);
        txt_title = rootView.findViewById(R.id.title);


        img_add = rootView.findViewById(R.id.add);
        li_add = rootView.findViewById(R.id.li_add);
        li_add.setOnClickListener(this);
        li_rename = rootView.findViewById(R.id.li_rename);
        li_rename.setOnClickListener(this);
        img_remove = rootView.findViewById(R.id.remove);
        li_remove = rootView.findViewById(R.id.li_remove);
        li_remove.setOnClickListener(this);
        img_filter = rootView.findViewById(R.id.filter);
        li_filter = rootView.findViewById(R.id.li_filter);
        li_filter.setOnClickListener(this);
        img_rename = rootView.findViewById(R.id.rename);

        try {
            Glide.with(getActivity())
                    .load(m_playlist.getImage())
                    .placeholder(R.drawable.bg_round_rect)
                    .into(img_content);
            txt_title.setText(m_playlist.getTitle());
            txt_subtitle.setText(m_playlist.getSub_title());
        }catch (Exception e){
            e.printStackTrace();
        }

        return rootView;
    }

    private void showTrackListDlg(ArrayList<UserTrackModel> modelArrayList){
        View dialogView = getLayoutInflater().inflate(R.layout.dlg_add_song, null);

        final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dlg.setCanceledOnTouchOutside(true);
        final ImageView cancel = dialogView.findViewById(R.id.btn_close);
        final Button add_btn = dialogView.findViewById(R.id.add_btn);
        final LinearLayout li_rv = dialogView.findViewById(R.id.li_rv);
        final LinearLayout li_empty = dialogView.findViewById(R.id.li_empty);
        rv_list = dialogView.findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager_rank = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager_rank);
        if(modelArrayList.size() == 0){
            li_rv.setVisibility(View.GONE);
            li_empty.setVisibility(View.VISIBLE);
        }else{
            li_rv.setVisibility(View.VISIBLE);
            li_empty.setVisibility(View.GONE);
            userTrackAdapter =  new UserTrackAdapter(getContext(), modelArrayList,getFragmentManager());
            userTrackAdapter.notifyDataSetChanged();
            rv_list.setAdapter(userTrackAdapter);
        }


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clicked_list_ids = "";
                if(userTrackAdapter != null){
                    if(userTrackAdapter.getList() != null){
                        for(int i = 0; i < userTrackAdapter.getList().size(); i++){
                            if(userTrackAdapter.getList().get(i).getCheck_flag().equalsIgnoreCase("1")){
                                clicked_list_ids += userTrackAdapter.getList().get(i).getId() + ",";
                            }
                        }
                    }
                }
                if(TextUtils.isEmpty(clicked_list_ids)){
                    ((MainActivity)getActivity()).showErrorMessages("Please select track");
                }else{
                    if (App.isNetworkAvailable(getContext())) {
                        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
                        String user_id = App.readSharedSetting(PRE_USER_ID, "");
                        Ion.with(getContext())
                                .load(GlobalConstants.ADD_TRACKS_PLAYLIST)
                                .setTimeout(CommonUtils.NETWORK_TIME)
                                .setBodyParameter("user_id", user_id)
                                .setBodyParameter("playlist_id", m_playlist.getId())
                                .setBodyParameter("track_ids", clicked_list_ids)
                                .setBodyParameter("memberKey", member_key)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if (e == null) {
                                            try {
                                                JSONObject resultObj = new JSONObject(result);
                                                if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                    JSONObject api_result = resultObj.getJSONObject("api_result");
                                                    String song_cnt = api_result.getString("song_cnt");
                                                    String image_path = api_result.getString("image_path");
                                                    LibraryFragment fragment = (LibraryFragment) ((MainActivity)getActivity()).getCurrentFragment();
                                                    PlayListModel model = m_playlist;
                                                    model.setSong_cnt(song_cnt);
                                                    model.setImage(image_path);
                                                    fragment.updateAdapter(position, model);
                                                    ((MainActivity)getActivity()).showMessages("Tracks added successfully");
                                                    dlg.dismiss();
                                                    dismiss();
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
                        ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                dismiss();
            }
        });

        dlg.setCanceledOnTouchOutside(false);
        dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dlg.show();
    }
    private void getTrackLists(){
        trackList.clear();


        if (App.isNetworkAvailable(getContext())) {
            String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
            String user_id = App.readSharedSetting(PRE_USER_ID, "");
            Ion.with(getContext())
                    .load(GlobalConstants.AVAILABLE_PLAYLIST_TRACKS)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("playlist_id", m_playlist.getId())
                    .setBodyParameter("memberKey", member_key)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        JSONArray articleArray = api_result.getJSONArray("list");
                                        Gson gson = new Gson();
                                        track_list_cnt = articleArray.length();

                                        if (articleArray.length() == 0){
                                            trackList.clear();
                                        }else {
                                            for (int i = 0; i < track_list_cnt; i++) {
                                                UserTrackModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), UserTrackModel.class);
                                                trackList.add(topModel);
                                            }
                                        }
                                        showTrackListDlg(trackList);
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
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }
    private void renamePlaylist(){
        View dialogView = getLayoutInflater().inflate(R.layout.dlg_playlist_add, null);

        final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dlg.setCanceledOnTouchOutside(true);
        final TextView ok = dialogView.findViewById(R.id.ok);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final TextView txt_title = dialogView.findViewById(R.id.title);
        final EditText et_title = dialogView.findViewById(R.id.et_title);
        final EditText et_subtitle = dialogView.findViewById(R.id.et_subtitle);
        txt_title.setText("Rename Playlist");
        et_title.setText(m_playlist.getTitle());
        et_subtitle.setText(m_playlist.getSub_title());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_title.getText().toString().trim()) && !TextUtils.isEmpty(et_subtitle.getText().toString().trim())){
                    String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");

                    if (App.isNetworkAvailable(getContext())) {
                        Ion.with(getContext())
                                .load(GlobalConstants.RENAME_PLAYLIST + member_key)
                                .setTimeout(CommonUtils.NETWORK_TIME)
                                .setBodyParameter("p_id", m_playlist.getId())
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

                                                    LibraryFragment fragment = (LibraryFragment) ((MainActivity)getActivity()).getCurrentFragment();
                                                    PlayListModel model = m_playlist;
                                                    model.setTitle(et_title.getText().toString());
                                                    model.setSub_title(et_subtitle.getText().toString());
                                                    fragment.updateAdapter(position, model);
                                                    ((MainActivity)getActivity()).showMessages("Playlist name changed successfully");
                                                    dlg.dismiss();
                                                    dismiss();
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
    private void delete_playlist(){
        View dialogView = getLayoutInflater().inflate(R.layout.dlg_exit, null);

        final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dlg.setCanceledOnTouchOutside(true);
        final TextView ok = dialogView.findViewById(R.id.ok);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final TextView confirm_txt = dialogView.findViewById(R.id.confirm_txt);
        confirm_txt.setText("Are you sure you want to remove?");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");

                if (App.isNetworkAvailable(getContext())) {
                    Ion.with(getContext())
                            .load(GlobalConstants.REMOVE_PLAYLIST + member_key)
                            .setTimeout(CommonUtils.NETWORK_TIME)
                            .setBodyParameter("p_id", m_playlist.getId())
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null) {
                                        try {
                                            JSONObject resultObj = new JSONObject(result);
                                            if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                LibraryFragment fragment = (LibraryFragment) ((MainActivity)getActivity()).getCurrentFragment();
                                                PlayListModel model = null;
                                                fragment.updateAdapter(position, model);
                                                ((MainActivity)getActivity()).showMessages("Playlist removed successfully");
                                                dlg.dismiss();
                                                dismiss();
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
            case R.id.li_add:
                getTrackLists();
                break;
            case R.id.li_remove:
                delete_playlist();
                break;
            case R.id.li_filter:
                break;
            case R.id.li_rename:
                renamePlaylist();
                break;
        }
    }
    @Override
    public float getCornerRadius(){
        return  getActivity().getResources().getDimension(R.dimen.padding_20);
    }

//    @Override
//    public int getPeekHeight(){
//        Log.e("screen111" , "a");
//        return 1200;
//    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
