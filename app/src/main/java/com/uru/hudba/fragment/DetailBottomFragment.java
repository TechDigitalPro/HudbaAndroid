package com.uru.hudba.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.uru.hudba.adapter.MyPlayListAdapter;
import com.uru.hudba.adapter.UserTrackAdapter;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.Constant;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.DownloadModel;
import com.uru.hudba.model.MyPlayListModel;
import com.uru.hudba.model.PlayListModel;
import com.uru.hudba.model.TrackModel;
import com.uru.hudba.model.UserTrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class DetailBottomFragment extends SuperBottomSheetFragment implements View.OnClickListener {
    public DetailBottomFragment(TrackModel trackModel, String type, int list_pos){
        this.trackModel = trackModel;
        this.type = type;
        this.list_pos = list_pos;

    }

    private View rootView;
    private ImageView img_content, img_download, img_favorite;
    private TextView txt_title, txt_artist, txt_add_favorite, txt_download, txt_playlist;
    private LinearLayout li_download, li_favorite, li_add_playlist, li_rename, li_fix, li_downloading;

    private TrackModel trackModel;
    private String type = "";
    private int list_pos = 0;
    private String content_image_url, title, artist;
    private String cur_favorite = "";

    private ArrayList<MyPlayListModel> mPlaylist = new ArrayList<>();
    private int mPlaylist_cnt = 0;
    private RecyclerView rv_list;
    private MyPlayListAdapter mPlaylistAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_detail_bottom,container,false);
        img_content = rootView.findViewById(R.id.image);
        txt_artist = rootView.findViewById(R.id.artist);
        txt_title = rootView.findViewById(R.id.title);
        txt_add_favorite = rootView.findViewById(R.id.txt_add_favorite);
        txt_download = rootView.findViewById(R.id.txt_download);
        txt_playlist = rootView.findViewById(R.id.txt_playlist);
        img_download = rootView.findViewById(R.id.download);
        img_favorite = rootView.findViewById(R.id.favorite);
        img_favorite.setOnClickListener(this);
        li_download = rootView.findViewById(R.id.li_download);
        li_download.setOnClickListener(this);
        li_favorite = rootView.findViewById(R.id.li_favorite);
        li_favorite.setOnClickListener(this);
        li_add_playlist = rootView.findViewById(R.id.add_playlist);
        li_add_playlist.setOnClickListener(this);
        li_rename = rootView.findViewById(R.id.rename);
        li_rename.setOnClickListener(this);
        li_fix = rootView.findViewById(R.id.fix);
        li_fix.setOnClickListener(this);
        li_downloading = rootView.findViewById(R.id.li_downloading);


        if(!((MainActivity)getActivity()).current_page.equalsIgnoreCase("playlist_track_detail")){
            li_add_playlist.setVisibility(View.GONE);
        }
        mPlaylist.clear();

        if(((MainActivity)getActivity()).current_page.equalsIgnoreCase("playlist_track_detail")){
            txt_playlist.setText(R.string.remove_track_playlist);
        }else{
            txt_playlist.setText(R.string.add_playlist);
        }

        if(Constant.downloading_list != null && Constant.downloading_list.size()>0){
            for(int i = 0; i< Constant.downloading_list.size(); i++){
                if(Constant.downloading_list.get(i).getTrackModel().getTrack_id().equalsIgnoreCase(trackModel.getTrack_id())){
                    li_download.setVisibility(View.GONE);
                    li_downloading.setVisibility(View.VISIBLE);
                }
            }
        }

        try {
            content_image_url = trackModel.getCover_image();
            title = trackModel.getTitle();
            artist = trackModel.getArtist_name();
            if(!TextUtils.isEmpty(trackModel.getFavorite())){
                if(trackModel.getFavorite().equalsIgnoreCase("favorite") || trackModel.getFavorite().equalsIgnoreCase("fdownload")){
                    img_favorite.setImageResource(R.drawable.favorite_heart);
                    txt_add_favorite.setText(R.string.remove_favorite);
                    cur_favorite = "favorite";
                    li_favorite.setVisibility(View.VISIBLE);
                    txt_download.setText(R.string.download);
                }else{
                    cur_favorite = "download";
                    li_favorite.setVisibility(View.VISIBLE);
                    txt_download.setText(R.string.remove_library);
                }
//                li_rename.setVisibility(View.VISIBLE);
//                li_fix.setVisibility(View.VISIBLE);
            }else{
                li_favorite.setVisibility(View.VISIBLE);
                img_favorite.setImageResource(R.drawable.unfavorite_heart);
                txt_add_favorite.setText(R.string.add_favorite);
                cur_favorite = "";
                li_rename.setVisibility(View.GONE);
                li_fix.setVisibility(View.GONE);
            }
            Glide.with(getActivity())
                    .load(content_image_url)
                    .placeholder(R.drawable.aaa)
                    .into(img_content);
            txt_title.setText(title);
            txt_artist.setText(artist);
        }catch (Exception e){
            e.printStackTrace();
        }

        return rootView;
    }

    private void getMyPlaylist(){
        mPlaylist.clear();
        if (App.isNetworkAvailable(getContext())) {
            String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
            String user_id = App.readSharedSetting(PRE_USER_ID, "");
            Ion.with(getContext())
                    .load(GlobalConstants.AVAILABLE_PLAYLIST_TRACKS)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("track_id", trackModel.getTrack_id())
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
                                        mPlaylist_cnt = articleArray.length();

                                        if (articleArray.length() == 0){
                                            mPlaylist.clear();
                                        }else {
                                            for (int i = 0; i < mPlaylist_cnt; i++) {
                                                MyPlayListModel topModel = gson.fromJson(articleArray.getJSONObject(i).toString(), MyPlayListModel.class);
                                                mPlaylist.add(topModel);
                                            }
                                        }
                                        showPlayListDlg(mPlaylist);
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
    private void showPlayListDlg(ArrayList<MyPlayListModel> playListModelArrayList){
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
        if(playListModelArrayList.size() == 0){
            li_rv.setVisibility(View.GONE);
            li_empty.setVisibility(View.VISIBLE);
        }else{
            li_rv.setVisibility(View.VISIBLE);
            li_empty.setVisibility(View.GONE);
            mPlaylistAdapter =  new MyPlayListAdapter(getContext(), playListModelArrayList);
            mPlaylistAdapter.notifyDataSetChanged();
            rv_list.setAdapter(mPlaylistAdapter);
        }


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clicked_list_ids = "";
                if(mPlaylistAdapter != null){
                    if(mPlaylistAdapter.getList() != null){
                        for(int i = 0; i < mPlaylistAdapter.getList().size(); i++){
                            if(mPlaylistAdapter.getList().get(i).getCheck_flag().equalsIgnoreCase("1")){
                                clicked_list_ids += mPlaylistAdapter.getList().get(i).getId() + ",";
                            }
                        }
                    }
                }
                if(TextUtils.isEmpty(clicked_list_ids)){
                    ((MainActivity)getActivity()).showErrorMessages("Please select playlist");
                }else{
                    if (App.isNetworkAvailable(getContext())) {
                        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
                        String user_id = App.readSharedSetting(PRE_USER_ID, "");
                        Ion.with(getContext())
                                .load(GlobalConstants.ADD_TRACKS_PLAYLIST)
                                .setTimeout(CommonUtils.NETWORK_TIME)
                                .setBodyParameter("user_id", user_id)
//                                .setBodyParameter("p_id", m_playlist.getId())
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
//                                                    PlayListModel model = m_playlist;
//                                                    model.setSong_cnt(song_cnt);
//                                                    model.setImage(image_path);
//                                                    fragment.updateAdapter(position, model);
                                                    ((MainActivity)getActivity()).showMessages("Track added successfully");
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.li_download:
                if(TextUtils.isEmpty(trackModel.getVideoId())){

                    if(!TextUtils.isEmpty(trackModel.getFavorite()) && trackModel.getFavorite().contains("download")){
                        ((MainActivity)getActivity()).getVID_download(trackModel, type, list_pos, "remove");
                    }else{
                        ((MainActivity)getActivity()).getVID_download(trackModel, type, list_pos, "add");

                    }
                }else{
                    if(!TextUtils.isEmpty(trackModel.getFavorite()) && trackModel.getFavorite().contains("download")){
                        ((MainActivity)getActivity()).add_download(trackModel, trackModel.getVideoId(), type, list_pos, "remove");
                    }else{
                        ((MainActivity)getActivity()).add_download(trackModel, trackModel.getVideoId(), type, list_pos, "add");

                    }

                }
                dismiss();
                break;
            case R.id.li_favorite:
                Log.e("track_id", trackModel.getId() +":"+trackModel.getTrack_id());
                ((MainActivity)getActivity()).add_favorite(trackModel, type, list_pos);
                dismiss();
                break;
            case R.id.add_playlist:
                if(((MainActivity)getActivity()).current_page.equalsIgnoreCase("playlist_track_detail")){
                    delete_playlist();
                }else{
                    getMyPlaylist();
                }
                break;
            case R.id.rename:
                if(((MainActivity)getActivity()).current_page.equalsIgnoreCase("playlist_track_detail")){
                    renamePlaylist();
                }else{

                }
                break;
            case R.id.fix:
                dismiss();
                break;

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
        et_title.setText(trackModel.getTitle());
        et_subtitle.setText(trackModel.getArtist_name());

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
                                .setBodyParameter("track_id", trackModel.getTrack_id())
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

                                                    PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) ((MainActivity)getActivity()).getCurrentFragment();
                                                    TrackModel model = trackModel;
                                                    model.setTitle(et_title.getText().toString());
                                                    model.setArtist_name(et_subtitle.getText().toString());
                                                    fragment.updateAdapter(list_pos, model, "add");
                                                    ((MainActivity)getActivity()).showMessages("Track name changed successfully");
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
                            .load(GlobalConstants.REMOVE_PLAYLIST_TRACK + member_key)
                            .setTimeout(CommonUtils.NETWORK_TIME)
                            .setBodyParameter("playlist_id", trackModel.getP_listid())
                            .setBodyParameter("track_id", trackModel.getTrack_id())
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null) {
                                        try {
                                            JSONObject resultObj = new JSONObject(result);
                                            if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) ((MainActivity)getActivity()).getCurrentFragment();
                                                fragment.updateAdapter(list_pos, null, "remove");
                                                ((MainActivity)getActivity()).showMessages("Track removed from Playlist successfully");
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
    public float getCornerRadius(){
        return  getActivity().getResources().getDimension(R.dimen.padding_20);
    }

//    @Override
//    public int getPeekHeight(){
//        if(!TextUtils.isEmpty(cur_favorite) && cur_favorite.contains("download")){
//            Log.e("screen1" , "a");
//            return 1410;
//        }else if(cur_favorite.equalsIgnoreCase("favorite")){
//            Log.e("screen2" , "b");
//            return 1420;
//        }else{
//            Log.e("screen3" , "c");
//            return 1100;
//        }
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
