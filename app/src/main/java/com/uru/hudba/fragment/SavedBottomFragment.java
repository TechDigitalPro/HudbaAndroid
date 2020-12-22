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
import android.widget.TextView;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.Constant;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.TrackModel;

import org.json.JSONException;
import org.json.JSONObject;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;

public class SavedBottomFragment extends SuperBottomSheetFragment implements View.OnClickListener {
    public SavedBottomFragment(TrackModel trackModel, String type, int list_pos){
        this.trackModel = trackModel;
        this.type = type;
        this.list_pos = list_pos;

    }

    private View rootView;
    private ImageView img_content, img_download, img_favorite;
    private TextView txt_title, txt_artist, txt_add_favorite, txt_download;
    private LinearLayout li_download, li_favorite, li_add_playlist, li_rename, li_fix, li_downloading;

    private TrackModel trackModel;
    private String type = "";
    private int list_pos = 0;
    private String content_image_url, title, artist;
    private String cur_favorite = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_detail_bottom,container,false);
        img_content = rootView.findViewById(R.id.image);
        txt_artist = rootView.findViewById(R.id.artist);
        txt_title = rootView.findViewById(R.id.title);
        txt_add_favorite = rootView.findViewById(R.id.txt_add_favorite);
        txt_download = rootView.findViewById(R.id.txt_download);
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
        if(Constant.downloading_list != null && Constant.downloading_list.size()>0){
            for(int i = 0; i< Constant.downloading_list.size(); i++){
                if(Constant.downloading_list.get(i).getTrackModel().getTrack_id().equalsIgnoreCase(trackModel.getTrack_id())){
                    li_download.setVisibility(View.GONE);
                    li_downloading.setVisibility(View.VISIBLE);
                }else{
                    li_download.setVisibility(View.VISIBLE);
                    li_downloading.setVisibility(View.GONE);
                }
            }
        }

        try {
            content_image_url = trackModel.getCover_image();
            title = trackModel.getTitle();
            artist = trackModel.getArtist_name();
            if(!TextUtils.isEmpty(trackModel.getFavorite())){
                if(trackModel.getFavorite().equalsIgnoreCase("favorite")){
                    img_favorite.setImageResource(R.drawable.favorite_heart);
                    txt_add_favorite.setText(R.string.remove_favorite);
                    cur_favorite = "favorite";
                    li_favorite.setVisibility(View.VISIBLE);
                    txt_download.setText(R.string.download);
                }else{
                    cur_favorite = "download";
                    li_favorite.setVisibility(View.GONE);
                    txt_download.setText(R.string.remove);
                }
            }else{
                li_favorite.setVisibility(View.VISIBLE);
                img_favorite.setImageResource(R.drawable.unfavorite_heart);
                txt_add_favorite.setText(R.string.add_favorite);
                cur_favorite = "";
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.li_download:
                if(!TextUtils.isEmpty(trackModel.getFavorite()) && trackModel.getFavorite().contains("download")){
                    SavedFragment fragment = (SavedFragment)((MainActivity)getActivity()).getCurrentFragment();
                    fragment.removeDownload(list_pos, trackModel.getFavorite());
                }else{
                    if(!TextUtils.isEmpty(trackModel.getFavorite())){
                        if(trackModel.getFavorite().equalsIgnoreCase("favorite")){
                            if(TextUtils.isEmpty(trackModel.getVideoId())){
                                ((MainActivity)getActivity()).getVID_download(trackModel, type, list_pos, "add");
                            }else{
                                ((MainActivity)getActivity()).add_download(trackModel, trackModel.getVideoId(), type, list_pos, "add");
                            }
                        }else{
                            ((MainActivity)getActivity()).add_download(trackModel, trackModel.getVideoId(), type, list_pos, "remove");
                        }
                    }

                }
                dismiss();
                break;
            case R.id.li_favorite:
                try {
                    SavedFragment fragment1 = (SavedFragment)((MainActivity)getActivity()).getCurrentFragment();
                    fragment1.removeDownload(list_pos, trackModel.getFavorite());
                }catch (Exception e){
                    e.printStackTrace();
                }
                dismiss();
                break;
            case R.id.add_playlist:
                break;
            case R.id.rename:
                SavedFragment fragments = (SavedFragment)((MainActivity)getActivity()).getCurrentFragment();
                fragments.renameSong(list_pos);
                dismiss();
                break;
            case R.id.fix:
                if(TextUtils.isEmpty(trackModel.getVideoId()) || trackModel.getVideoId().equalsIgnoreCase("")){
                    ((MainActivity)getActivity()).getVID_download(trackModel, type, list_pos, "add");
                }else{
                    ((MainActivity)getActivity()).add_download(trackModel, trackModel.getVideoId(), type, list_pos, "add");
                }
                dismiss();

        }
    }

    @Override
    public float getCornerRadius(){
        return  getActivity().getResources().getDimension(R.dimen.padding_20);
    }

    @Override
    public int getPeekHeight(){
        if(cur_favorite.equalsIgnoreCase("download")){
            return 1410;
        }else{
            return 1480;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
