package com.uru.hudba.activity;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mancj.slideup.SlideUp;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.Constant;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.download.DownloadVideoAsyn;
import com.uru.hudba.download.GetLinkListenMusic;
import com.uru.hudba.fragment.DetailBottomFragment;
import com.uru.hudba.fragment.HomeFragment;
import com.uru.hudba.fragment.LibraryFragment;
import com.uru.hudba.fragment.PlaylistTrackDetailFragment;
import com.uru.hudba.fragment.SavedFragment;
import com.uru.hudba.fragment.SearchDetailFragment;
import com.uru.hudba.fragment.SearchFragment;
import com.uru.hudba.fragment.SettingFragment;
import com.uru.hudba.fragment.TrackDetailFragment;
import com.uru.hudba.interfaces.JsonGetLinkListenMusic;
import com.uru.hudba.model.DownloadModel;
import com.uru.hudba.model.TrackModel;
import com.uru.hudba.musicservice.NotiService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_PLAYING_MODE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;
import static com.uru.hudba.constant.GlobalConstants.PRE_VOLUME_MODE;

public class MainActivity extends BaseActivity implements View.OnClickListener, DownloadVideoAsyn.EventDownloadVideoCallBack, JsonGetLinkListenMusic {

    private BottomNavigationView mBottomNav;
    private FrameLayout bg_main;
    private View mini_panel;
    private ImageView mini_pause;
    private TextView txt_mini_title, txt_mini_artist;
    private RoundedImageView mini_image;
    private ProgressBar min_progress;
    private GifImageView min_loading;

    private SlideUp songSlideView;
    private View detail_panel;
    private ImageView btn_detail_close, btn_detail_list,btn_detail_downstatus,detail_pause, detail_prev, detail_next, detail_shuffle, detail_repeat, detail_more, detail_share, detail_cover_image;
    private TextView detail_title, detail_artist, detail_start_time, detail_end_time;
    private YouTubePlayerView youtubeView;
    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerTracker youtube_tracker;
    private SeekBar detail_seekbar, volume_seekbar;
    private LinearLayout volume_setting_li;


    private TrackModel cur_track;

    private boolean isPlaying = false;
    public String current_page = "home";
    public String current_playing_type = "";


    private int mSelectedItem;
    private MenuItem selectedItem;

    private String time_pattern = "%d : %02d";
    boolean tmp_play = false;
    private float cur_duration = 0;

    private static final String YOUTUBE_ID = "ea4-5mrpGfE";
    private String cur_download_trackid = "";
    private TrackModel cur_download_track;
    private String cur_download_type = "";


    private Handler seekHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constant.context = this;
        Constant.downloading_list.clear();

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_tab);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item,false);

                return true;
            }
        });
        selectedItem = mBottomNav.getMenu().getItem(0);


        bg_main = (FrameLayout) findViewById(R.id.fragmentContainer);
        mini_panel = findViewById(R.id.mini_panel);
        mini_pause = mini_panel.findViewById(R.id.btnPlayPauseMain);
        txt_mini_title = mini_panel.findViewById(R.id.txt_title);
        txt_mini_artist = mini_panel.findViewById(R.id.txt_artist);
        mini_image = mini_panel.findViewById(R.id.cover_img);
        min_progress = mini_panel.findViewById(R.id.min_progress);
        min_loading = mini_panel.findViewById(R.id.item_playing);

        detail_panel = findViewById(R.id.detail_panel);
        btn_detail_close = detail_panel.findViewById(R.id.close);
        btn_detail_list = detail_panel.findViewById(R.id.list);
        detail_title = detail_panel.findViewById(R.id.title);
        detail_artist = detail_panel.findViewById(R.id.artist);
        youtubeView = detail_panel.findViewById(R.id.youtube_player_view);
        detail_cover_image = detail_panel.findViewById(R.id.cover_img);
        btn_detail_downstatus = detail_panel.findViewById(R.id.main_favorite);
        btn_detail_downstatus.setOnClickListener(this);
        detail_start_time = detail_panel.findViewById(R.id.start);
        detail_end_time = detail_panel.findViewById(R.id.end);
        detail_seekbar = detail_panel.findViewById(R.id.seekbar);
        volume_setting_li = detail_panel.findViewById(R.id.li_volume);
        detail_pause = detail_panel.findViewById(R.id.play);
        detail_prev = detail_panel.findViewById(R.id.prev);
        detail_next = detail_panel.findViewById(R.id.next);
        detail_shuffle = detail_panel.findViewById(R.id.shuffle);
        detail_shuffle.setOnClickListener(this);
        detail_share = detail_panel.findViewById(R.id.share);
        detail_share.setOnClickListener(this);
        detail_more = detail_panel.findViewById(R.id.more);
        detail_more.setOnClickListener(this);
        detail_repeat = detail_panel.findViewById(R.id.repeat);
        detail_repeat.setOnClickListener(this);
        volume_seekbar = detail_panel.findViewById(R.id.volume_seekbar);
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume_seekbar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seekbar.setProgress(volume_level);
        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mini_pause.setOnClickListener(this);
        songSlideView = new SlideUp(detail_panel);
        mini_panel.setOnClickListener(this);
        youtubeView.setHovered(true);
        youtubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                mYouTubePlayer = youTubePlayer;
                mYouTubePlayer.addListener(youtube_tracker);
            }
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second){
                if(cur_track != null){
                    Log.e("seek0000", second + "");
                    if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                        min_progress.setProgress((int) second);
                        detail_seekbar.setProgress((int) second);
                        detail_start_time.setText(String.format(time_pattern, new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) second * 1000)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) second * 1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) second * 1000)))}));
                        if(cur_duration <= second){
                            isPlaying = false;
                            detail_pause.setImageResource(R.drawable.ic_play);
                            mini_pause.setImageResource(R.drawable.ic_play);
                            nextPlay();
                        }
                    }
                }

            }
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration){
                cur_duration = duration;
                isPlaying = true;
                detail_pause.setImageResource(R.drawable.ic_pause);
                mini_pause.setImageResource(R.drawable.ic_pause);
                min_loading.setVisibility(View.GONE);
                min_progress.setMax((int) duration);
                detail_seekbar.setMax((int) duration);
                detail_end_time.setText(String.format(time_pattern, new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) duration * 1000)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) duration* 1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration* 1000)))}));
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                if(state == PlayerConstants.PlayerState.PAUSED){
                    Constant.isPlayed = false;
                    detail_pause.setImageResource(R.drawable.ic_play);
                    mini_pause.setImageResource(R.drawable.ic_play);
                    Intent intent = new Intent("com.uru.hdubaService");
                    intent.putExtra("noti_play", true);
                    sendBroadcast(intent);
                }else if(state == PlayerConstants.PlayerState.PLAYING){
                    Constant.isPlayed = true;
                    detail_pause.setImageResource(R.drawable.ic_pause);
                    mini_pause.setImageResource(R.drawable.ic_pause);
                    Intent intent = new Intent("com.uru.hdubaService");
                    intent.putExtra("noti_play", true);
                    sendBroadcast(intent);
                }


            }
        });
        detail_pause.setOnClickListener(this);
        detail_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(mYouTubePlayer != null){
                    if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                        tmp_play = isPlaying;
                        mYouTubePlayer.pause();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!TextUtils.isEmpty(cur_track.getFavorite()) && cur_track.getFavorite().contains("download")){
                    int progress = seekBar.getProgress();
                    Log.e("seek1:", seekBar.getProgress() + "");
                    try {
                        Intent intent = new Intent(MainActivity.this, NotiService.class);
                        intent.setAction(NotiService.ACTION_SEEKTO);
                        intent.putExtra("seekto", CommonUtils.getSeekFromPercentage(progress, NotiService.getInstance().getDuration()));
                        startService(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e("seek2:", seekBar.getProgress() + "");
                    if(mYouTubePlayer != null){
                        mYouTubePlayer.seekTo(seekBar.getProgress());
                        if(tmp_play){
                            mYouTubePlayer.play();
                        }else{
                            mYouTubePlayer.pause();
                        }

                    }
                }


            }
        });
        youtube_tracker = new YouTubePlayerTracker();

        songSlideView.setSlideListener(new SlideUp.SlideListener() {
            @SuppressLint("Range")
            public void onSlideDown(float v) {
                float f = 0.0f;
                if (0.5f - (v / 100.0f) > 0.0f) {
                    f =  0.5f - (v / 100.0f);
                }
                bg_main.setAlpha( 1 - f );
                if (v == 100.0f) {
                    mini_panel.setVisibility(View.VISIBLE);
                    mBottomNav.setVisibility(View.VISIBLE);
                    return;
                }
                if(songSlideView.isVisible()){
                    mini_panel.setVisibility(View.GONE);
                    mBottomNav.setVisibility(View.GONE);
                }
                if(Double.isInfinite(v)){
                    mini_panel.setVisibility(View.VISIBLE);
                    mBottomNav.setVisibility(View.VISIBLE);
                }

            }
            public void onVisibilityChanged(int i) {

            }
        });
        btn_detail_close.setOnClickListener(this);
        detail_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPlay();
                mBottomNav.setVisibility(View.GONE);
            }
        });
        detail_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPlay();
                mBottomNav.setVisibility(View.GONE);
            }
        });


        selectFragment(selectedItem,true);
        downloadInit();
        registerReceiver(mActivityReceiver, new IntentFilter("com.uru.hdubaActivity"));


    }


    /********************file download module**********************/
    private void downloadInit(){
        PRDownloader.initialize(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30000)
                .setConnectTimeout(30000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }
    private void downloadImageRequest(String url, String fileName){
        fileName = fileName + ".jpg";
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "image"+ File.separator;

        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        add_download_db();
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }
    private void downloadVideoRequest(String url, String fileName){
        fileName = fileName + ".mp3";
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "video"+ File.separator;

        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        downloadImageRequest(cur_download_track.getCover_image(), cur_download_trackid);
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }
    @Override
    public void downloadVideoFinish(String s) {
        downloadImageRequest(cur_download_track.getCover_image(), cur_download_trackid);
    }
    @Override
    public void updateProgress(int pro) {

    }
    @Override
    public void downloadVideoFail(String fail) {

        if(current_page.equalsIgnoreCase("track_detail")){
            TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("saved_song")){
            SavedFragment fragment = (SavedFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("playlist_track_detail")){
            PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("search")){
            SearchFragment fragment = (SearchFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("search_detail")){
            SearchFragment fragment = (SearchFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        Constant.cur_download_trackid = "";
        Constant.downloading_list.remove(0);
        if(Constant.downloading_list.size()>0){
            download_action(Constant.downloading_list.get(0));
        }
        showErrorMessages("File Download fail: Please try again");
    }
    @Override
    public void getLinkSuccess(String linkMusic) {
        downloadVideoRequest(linkMusic, cur_download_trackid);
    }
    @Override
    public void getLinkFail() {

        if(current_page.equalsIgnoreCase("track_detail")){
            TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("saved_song")){
            SavedFragment fragment = (SavedFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("playlist_track_detail")){
            PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("search")){
            SearchFragment fragment = (SearchFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        if(current_page.equalsIgnoreCase("search_detail")){
            SearchFragment fragment = (SearchFragment) getCurrentFragment();
            if(fragment.getAdapter() != null){
                fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                fragment.getAdapter().notifyDataSetChanged();
            }
        }
        Constant.cur_download_trackid = "";
        Constant.downloading_list.remove(0);
        if(Constant.downloading_list.size()>0){
            download_action(Constant.downloading_list.get(0));
        }

        showErrorMessages("FileSource Get fail: Please try again");

    }

    /*************************download***************************************/
    private void download_action(DownloadModel downloadModel){
        cur_download_trackid = downloadModel.getTrackModel().getTrack_id();
        cur_download_track = downloadModel.getTrackModel();
        cur_download_type = downloadModel.getType();
        Constant.cur_download_trackid = cur_download_track.getTrack_id();
        GetLinkListenMusic getLinkListenMusic = new GetLinkListenMusic();
        getLinkListenMusic.setCallBack(MainActivity.this);
        getLinkListenMusic.execute("https://www.youtube.com/watch?v=" + downloadModel.getVideo_id());
    }
    public void add_download(TrackModel cur_track, String videoId, String type, int list_pos, String action){

        if(action.equalsIgnoreCase("add")) {
            if(Constant.downloading_list != null){
                DownloadModel tmp = new DownloadModel();
                tmp.setTrackModel(cur_track);
                tmp.setVideo_id(videoId);
                tmp.setType(type);
                tmp.setPosition(list_pos);
                tmp.setAction("add");
                Constant.downloading_list.add(tmp);
            }
            if(Constant.downloading_list.size() == 1){
                download_action(Constant.downloading_list.get(0));
            }
            Constant.cur_download_pos = list_pos;
            if(current_page.equalsIgnoreCase("track_detail")){
                TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("saved_song")){
                SavedFragment fragment = (SavedFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("playlist_track_detail")){
                PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("search")){
                SearchFragment fragment = (SearchFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("search_detail")){
                SearchDetailFragment fragment = (SearchDetailFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
        }else{
            cur_download_track = cur_track;
            add_download_db();
        }
    }
    private void add_download_db(){
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        if (App.isNetworkAvailable(getApplicationContext())) {
            Ion.with(getApplicationContext())
                    .load(GlobalConstants.ADD_DOWNLOAD)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("p_listid", cur_download_track.getP_listid())
                    .setBodyParameter("track_id", cur_download_track.getTrack_id())
                    .setBodyParameter("title", cur_download_track.getTitle())
                    .setBodyParameter("artist_id", cur_download_track.getArtist_id())
                    .setBodyParameter("artist_name", cur_download_track.getArtist_name())
                    .setBodyParameter("cover_image", cur_download_track.getCover_image())
                    .setBodyParameter("album_id", cur_download_track.getAlbum_id())
                    .setBodyParameter("album_name", cur_download_track.getAlbum_name())
                    .setBodyParameter("videoId", cur_download_track.getVideoId())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if(current_page.equalsIgnoreCase("track_detail")){
                                TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
                                if(fragment.getAdapter() != null){
                                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                                    fragment.getAdapter().notifyDataSetChanged();
                                }
                            }
                            if(current_page.equalsIgnoreCase("saved_song")){
                                SavedFragment fragment = (SavedFragment) getCurrentFragment();
                                if(fragment.getAdapter() != null){
                                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                                    fragment.getAdapter().notifyDataSetChanged();
                                }
                            }
                            if(current_page.equalsIgnoreCase("playlist_track_detail")){
                                PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
                                if(fragment.getAdapter() != null){
                                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                                    fragment.getAdapter().notifyDataSetChanged();
                                }
                            }
                            if(current_page.equalsIgnoreCase("search")){
                                SearchFragment fragment = (SearchFragment) getCurrentFragment();
                                if(fragment.getAdapter() != null){
                                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                                    fragment.getAdapter().notifyDataSetChanged();
                                }
                            }
                            if(current_page.equalsIgnoreCase("search_detail")){
                                SearchDetailFragment fragment = (SearchDetailFragment) getCurrentFragment();
                                if(fragment.getAdapter() != null){
                                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                                    fragment.getAdapter().notifyDataSetChanged();
                                }
                            }
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        String tmp_favorite = "";
                                        if(resultObj.getString("api_result").equalsIgnoreCase("1")){
                                            tmp_favorite = "download";
                                            Constant.cur_download_trackid = "";
                                            Constant.downloading_list.remove(0);
                                            if(Constant.downloading_list.size()>0){
                                                download_action(Constant.downloading_list.get(0));
                                            }
                                        }
                                        if(cur_download_type.equalsIgnoreCase("play_view")){
                                            setFavorite(tmp_favorite, "play_view", Constant.playPos);
                                        }else{
                                            setFavorite(tmp_favorite, "", Constant.cur_download_pos);
                                        }


                                    } else {
                                        showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                showErrorMessages(R.string.txt_server_error);
                                Constant.downloading_list.remove(0);
                                if(Constant.downloading_list.size()>0){
                                    download_action(Constant.downloading_list.get(0));
                                }
                            }
                        }
                    });
        } else {
            showErrorMessages(R.string.txt_network_error);
            if(current_page.equalsIgnoreCase("track_detail")){
                TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("saved_song")){
                SavedFragment fragment = (SavedFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("playlist_track_detail")){
                PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("search")){
                SearchFragment fragment = (SearchFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            if(current_page.equalsIgnoreCase("search_detail")){
                SearchFragment fragment = (SearchFragment) getCurrentFragment();
                if(fragment.getAdapter() != null){
                    fragment.getAdapter().unsetDownloading(Constant.cur_download_trackid);
                    fragment.getAdapter().notifyDataSetChanged();
                }
            }
            Constant.cur_download_trackid = "";
            Constant.downloading_list.remove(0);
            if(Constant.downloading_list.size()>0){
                download_action(Constant.downloading_list.get(0));
            }
        }
    }

    /***************player**********************/
    public void setTrackList(List<TrackModel> trackList){
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(trackList);
    }
    public void initPlayer(int track_pos){
        if(!Constant.arrayList_play.get(track_pos).getTrack_id().equalsIgnoreCase(Constant.cur_track_id)){
            stopPlayer();
        }
        try {
            cur_track = Constant.arrayList_play.get(track_pos);

            if(!Constant.cur_track_id.equalsIgnoreCase(cur_track.getTrack_id())){



                Constant.playPos = track_pos;
                Constant.cur_track_id = cur_track.getTrack_id();
                Constant.cur_videoId = cur_track.getVideoId();

                mini_pause.setImageResource(R.drawable.ic_play);
                detail_pause.setImageResource(R.drawable.ic_play);
                txt_mini_title.setText(cur_track.getTitle());
                txt_mini_artist.setText(cur_track.getArtist_name());
                min_progress.setProgress(0);
                detail_seekbar.setProgress(0);
                min_loading.setVisibility(View.VISIBLE);

                detail_title.setText(cur_track.getTitle());
                detail_artist.setText(cur_track.getArtist_name());
                if(!TextUtils.isEmpty(cur_track.getFavorite())){
                    btn_detail_downstatus.setImageResource(R.drawable.favorite_heart);
                }else{
                    btn_detail_downstatus.setImageResource(R.drawable.unfavorite_heart);
                }

                if(!TextUtils.isEmpty(cur_track.getFavorite()) && cur_track.getFavorite().contains("download")){
                    youtubeView.setVisibility(View.INVISIBLE);
                    detail_cover_image.setVisibility(View.VISIBLE);
                    String path = getFilesDir().getAbsolutePath() + File.separator + "image"  + File.separator;
                    path = path + cur_track.getTrack_id() + ".jpg";
                    File imgFile = new File(path);
                    Glide.with(MainActivity.this)
                            .load(imgFile)
                            .override(70, 70)
                            .into(mini_image);
                    Glide.with(MainActivity.this)
                            .load(imgFile)
                            .into(detail_cover_image);
                }else{
                    String cur_playing_mode = App.readSharedSetting(PRE_PLAYING_MODE, "photo");
                    Log.e("playmode:2", cur_playing_mode);
                    if(cur_playing_mode.equalsIgnoreCase("photo")){
                        youtubeView.setVisibility(View.INVISIBLE);
                        detail_cover_image.setVisibility(View.VISIBLE);
                    }else{
                        youtubeView.setVisibility(View.VISIBLE);
                        detail_cover_image.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(cur_track.getCover_image())) {
                        try {
                            Glide.with(MainActivity.this)
                                    .load(cur_track.getCover_image())
                                    .override(70, 70)
                                    .into(mini_image);
                            Glide.with(MainActivity.this)
                                    .load(cur_track.getCover_image())
                                    .into(detail_cover_image);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                if(!songSlideView.isVisible()){
                    mini_panel.setVisibility(View.VISIBLE);
                    mBottomNav.setVisibility(View.VISIBLE);
                }



                isPlaying = false;
                Constant.isPlayed = false;



                if(!App.isNetworkAvailable(getApplicationContext())){
                    if(!TextUtils.isEmpty(cur_track.getFavorite()) && cur_track.getFavorite().contains("download")){
                        Intent intent = new Intent(MainActivity.this, NotiService.class);
                        intent.setAction(NotiService.ACTION_PLAY);
                        startService(intent);
                    }else{
                        showErrorMessages("To listen this song, you must be connected. If you download it, it will be available also offline");
                        return;
                    }
                }else{
                    if(!TextUtils.isEmpty(cur_track.getFavorite()) && cur_track.getFavorite().contains("download")){
                        Intent intent = new Intent(MainActivity.this, NotiService.class);
                        intent.setAction(NotiService.ACTION_PLAY);
                        startService(intent);
                    }else{
                        if(!TextUtils.isEmpty(Constant.cur_videoId) && Constant.cur_videoId != null){
                            if(mYouTubePlayer != null){
                                mYouTubePlayer.loadVideo(Constant.cur_videoId, 0);
                                Intent intent = new Intent(MainActivity.this, NotiService.class);
                                intent.setAction(NotiService.ACTION_PLAY);
                                startService(intent);
                            }
                        }else{
                            getVideoID(cur_track.getTrack_id(), cur_track.getTitle(), cur_track.getArtist_name(), track_pos);
                            return;
                        }
                    }
                }
                if(current_page.equalsIgnoreCase("track_detail")){
                    TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
                    if(fragment.getAdapter() != null){
                        fragment.getAdapter().setPosition(track_pos, "common");
                        fragment.getAdapter().setChecked(true, "common");
                        fragment.getAdapter().notifyDataSetChanged();
                    }
                }
                if(current_page.equalsIgnoreCase("saved_song")){
                    SavedFragment fragment = (SavedFragment) getCurrentFragment();
                    if(fragment.getAdapter() != null){
                        fragment.getAdapter().setPosition(track_pos, "common");
                        fragment.getAdapter().setChecked(true, "common");
                        fragment.getAdapter().notifyDataSetChanged();
                    }
                }
                if(current_page.equalsIgnoreCase("playlist_track_detail")){
                    PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
                    if(fragment.getAdapter() != null){
                        fragment.getAdapter().setPosition(track_pos, "common");
                        fragment.getAdapter().setChecked(true, "common");
                        fragment.getAdapter().notifyDataSetChanged();
                    }
                }
                if(current_page.equalsIgnoreCase("search")){
                    SearchFragment fragment = (SearchFragment) getCurrentFragment();
                    if(fragment.getAdapter() != null){
                        fragment.getAdapter().setPosition(track_pos, "common");
                        fragment.getAdapter().setChecked(true, "common");
                        fragment.getAdapter().notifyDataSetChanged();
                    }
                }
                if(current_page.equalsIgnoreCase("search_detail")){
                    SearchDetailFragment fragment = (SearchDetailFragment) getCurrentFragment();
                    if(fragment.getAdapter() != null){
                        fragment.getAdapter().setPosition(track_pos, "common");
                        fragment.getAdapter().setChecked(true, "common");
                        fragment.getAdapter().notifyDataSetChanged();
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void nextPlay(){
        if(!Constant.isRepeat){
            if(Constant.playPos < Constant.arrayList_play.size()-1){
                initPlayer(Constant.playPos + 1);
            }
        }else{
            initPlayer(Constant.playPos);
        }
    }
    private void prevPlay(){
        if(!Constant.isRepeat){
            if(Constant.playPos != 0){
                initPlayer(Constant.playPos - 1);
            }
        }else{
            initPlayer(Constant.playPos);
        }
    }
    private void stopPlayer(){
        if(cur_track != null){
            if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                if(mYouTubePlayer != null){
                    if(youtube_tracker.getState() == PlayerConstants.PlayerState.PLAYING){
                        mYouTubePlayer.pause();
                    }
                }
            }else{
                Intent intent = new Intent(MainActivity.this, NotiService.class);
                intent.setAction(NotiService.ACTION_PAUSE);
                startService(intent);
            }
        }
    }
    private void notiPause(){
        isPlaying = Constant.isPlayed;
        if (Constant.isPlayed) {
            if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                mYouTubePlayer.pause();
            }
            detail_pause.setImageResource(R.drawable.ic_play);
            mini_pause.setImageResource(R.drawable.ic_play);
        } else {
            if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                mYouTubePlayer.play();
            }
            detail_pause.setImageResource(R.drawable.ic_pause);
            mini_pause.setImageResource(R.drawable.ic_pause);
        }
    }
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };
    public void seekUpdation() {
        try {
            if(Constant.arrayList_play.get(Constant.playPos).getFavorite().contains("download")){
                Log.e("seek12:", NotiService.mediaPlayer.getCurrentPosition() + ":" + NotiService.mediaPlayer.getDuration());
                min_progress.setProgress(CommonUtils.getProgressPercentage(NotiService.mediaPlayer.getCurrentPosition(), NotiService.getInstance().getDuration()));
                detail_seekbar.setProgress(CommonUtils.getProgressPercentage(NotiService.mediaPlayer.getCurrentPosition(), NotiService.getInstance().getDuration()));
                detail_start_time.setText(CommonUtils.milliSecondsToTimer(NotiService.mediaPlayer.getCurrentPosition(), NotiService.getInstance().getDuration()));
                detail_end_time.setText(CommonUtils.milliSecondsToTimer(NotiService.mediaPlayer.getDuration(), NotiService.getInstance().getDuration()));
                if (NotiService.mediaPlayer != null) {
                    seekHandler.removeCallbacks(run);
                    seekHandler.postDelayed(run, 1000);
                }
                if(NotiService.mediaPlayer.getDuration() - NotiService.mediaPlayer.getCurrentPosition() < (int) (NotiService.mediaPlayer.getDuration()/100)){
                    nextPlay();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***************favorite**********************/
    public void setFavorite(String favorite, @NotNull String type, int list_pos){
        if(type.equalsIgnoreCase("play_view")){
            if(TextUtils.isEmpty(favorite)){
                btn_detail_downstatus.setImageResource(R.drawable.unfavorite_heart);
            }else{
                btn_detail_downstatus.setImageResource(R.drawable.favorite_heart);
            }
            cur_track.setFavorite(favorite);
            Constant.arrayList_play.set(Constant.playPos, cur_track);
            try {
                HomeFragment homeFragment = (HomeFragment) getCurrentFragment();
                if(current_playing_type.equalsIgnoreCase("home")){
                    homeFragment.update_trackList("home", Constant.playPos, cur_track);
                }else if(current_playing_type.equalsIgnoreCase("favorite")){
                    homeFragment.update_trackList("favorite", Constant.playPos, cur_track);
                }else if(current_playing_type.equalsIgnoreCase("trending")){
                    homeFragment.update_trackList("trending", Constant.playPos, cur_track);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(type.equalsIgnoreCase("list_view")){
            if(current_page.equalsIgnoreCase("saved_song")){
                SavedFragment savedFragment =  (SavedFragment) getCurrentFragment();
                savedFragment.updateAdapter(list_pos, favorite);
            }else if(current_page.equalsIgnoreCase("playlist_track_detail")){
                PlaylistTrackDetailFragment playlistTrackDetailFragment =  (PlaylistTrackDetailFragment) getCurrentFragment();
                playlistTrackDetailFragment.updateAdapter(list_pos, null, "add");
            }else if(current_page.equalsIgnoreCase("track_detail")){
                TrackDetailFragment detailAlbumFragment =  (TrackDetailFragment) getCurrentFragment();
                detailAlbumFragment.updateAdapter(list_pos, favorite);
            }else if(current_page.equalsIgnoreCase("search_detail")){
                SearchDetailFragment searchDetailFragment =  (SearchDetailFragment) getCurrentFragment();
                searchDetailFragment.updateAdapter(list_pos, favorite);
            }
        }
        if(current_page.equalsIgnoreCase("track_detail")){
            TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
            if(cur_track != null){
                if(!current_playing_type.equalsIgnoreCase("track") && !current_playing_type.equalsIgnoreCase("detail")){
                    fragment.updateAdapter(list_pos, favorite);
                }else{
                    fragment.updateAdapter(Constant.playPos, favorite);
                }
            }else{
                fragment.updateAdapter(list_pos, favorite);
            }
        }
        if(current_page.equalsIgnoreCase("saved_song")){
            SavedFragment fragment = (SavedFragment) getCurrentFragment();
            if(cur_track != null){
                if(!current_playing_type.equalsIgnoreCase("track") && !current_playing_type.equalsIgnoreCase("detail")){
                    fragment.updateAdapter(list_pos, favorite);
                }else{
                    fragment.updateAdapter(Constant.playPos, favorite);
                }
            }else{
                fragment.updateAdapter(list_pos, favorite);
            }
        }



    }
    public void add_favorite(TrackModel cur_track, String type, int list_pos){
        String user_id = App.readSharedSetting(PRE_USER_ID, "");
        if (App.isNetworkAvailable(getApplicationContext())) {
            Ion.with(getApplicationContext())
                    .load(GlobalConstants.ADD_FAVORITE)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("user_id", user_id)
                    .setBodyParameter("p_listid", cur_track.getP_listid())
                    .setBodyParameter("track_id", cur_track.getTrack_id())
                    .setBodyParameter("title", cur_track.getTitle())
                    .setBodyParameter("artist_id", cur_track.getArtist_id())
                    .setBodyParameter("artist_name", cur_track.getArtist_name())
                    .setBodyParameter("cover_image", cur_track.getCover_image())
                    .setBodyParameter("album_id", cur_track.getAlbum_id())
                    .setBodyParameter("album_name", cur_track.getAlbum_name())
                    .setBodyParameter("videoId", cur_track.getVideoId())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        String tmp_favorite = "";
                                        if(resultObj.getString("api_result").equalsIgnoreCase("1")){
                                            tmp_favorite = "favorite";
                                        }
                                        if(type.equalsIgnoreCase("play_view")){
                                            setFavorite(tmp_favorite, "play_view", Constant.playPos);
                                        }else{
                                            setFavorite(tmp_favorite, type, list_pos);
                                        }
                                    } else {
                                        showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            showErrorMessages(R.string.txt_network_error);
        }
    }

    /***************getvideoID**********************/
    private void getVideoID(String id, String track_name, String artist_name, int pos){
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        if (App.isNetworkAvailable(getApplicationContext())) {
            Ion.with(getApplicationContext())
                    .load(GlobalConstants.SEARCH_VIDEOID)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("track_id", id)
                    .setBodyParameter("track_name", track_name)
                    .setBodyParameter("artist_name", artist_name)
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
                                        Constant.cur_videoId = api_result.getString("videoId");
                                        if(mYouTubePlayer != null){
                                            if(youtube_tracker.getState() == PlayerConstants.PlayerState.PLAYING){
                                                mYouTubePlayer.pause();
                                            }
                                            mYouTubePlayer.loadVideo(Constant.cur_videoId, 0);
                                            if(current_page.equalsIgnoreCase("track_detail")){
                                                TrackDetailFragment fragment = (TrackDetailFragment) getCurrentFragment();
                                                if(fragment.getAdapter() != null){
                                                    fragment.getAdapter().setPosition(pos, "common");
                                                    fragment.getAdapter().setChecked(true, "common");
                                                    fragment.getAdapter().notifyDataSetChanged();
                                                }
                                            }
                                            if(current_page.equalsIgnoreCase("saved_song")){
                                                SavedFragment fragment = (SavedFragment) getCurrentFragment();
                                                if(fragment.getAdapter() != null){
                                                    fragment.getAdapter().setPosition(pos, "common");
                                                    fragment.getAdapter().setChecked(true, "common");
                                                    fragment.getAdapter().notifyDataSetChanged();
                                                }
                                            }
                                            if(current_page.equalsIgnoreCase("playlist_track_detail")){
                                                PlaylistTrackDetailFragment fragment = (PlaylistTrackDetailFragment) getCurrentFragment();
                                                if(fragment.getAdapter() != null){
                                                    fragment.getAdapter().setPosition(pos, "common");
                                                    fragment.getAdapter().setChecked(true, "common");
                                                    fragment.getAdapter().notifyDataSetChanged();
                                                }
                                            }
                                            if(current_page.equalsIgnoreCase("search")){
                                                SearchFragment fragment = (SearchFragment) getCurrentFragment();
                                                if(fragment.getAdapter() != null){
                                                    fragment.getAdapter().setPosition(pos, "common");
                                                    fragment.getAdapter().setChecked(true, "common");
                                                    fragment.getAdapter().notifyDataSetChanged();
                                                }
                                            }
                                            if(current_page.equalsIgnoreCase("search_detail")){
                                                SearchDetailFragment fragment = (SearchDetailFragment) getCurrentFragment();
                                                if(fragment.getAdapter() != null){
                                                    fragment.getAdapter().setPosition(pos, "common");
                                                    fragment.getAdapter().setChecked(true, "common");
                                                    fragment.getAdapter().notifyDataSetChanged();
                                                }
                                            }
                                            Intent intent = new Intent(MainActivity.this, NotiService.class);
                                            intent.setAction(NotiService.ACTION_PLAY);
                                            startService(intent);
                                        }
                                    } else {
                                        showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                    Constant.cur_videoId = "";
                                    showErrorMessages("Couldn't find video");
                                }
                            } else {
                                showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            showErrorMessages(R.string.txt_network_error);
        }
    }
    public void getVID_download(TrackModel trackModel, String type, int list_pos, String action){
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
        if (App.isNetworkAvailable(getApplicationContext())) {
            Ion.with(getApplicationContext())
                    .load(GlobalConstants.SEARCH_VIDEOID)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("track_id", trackModel.getTrack_id())
                    .setBodyParameter("track_name", trackModel.getTitle())
                    .setBodyParameter("artist_name", trackModel.getArtist_name())
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
                                        String cvid = api_result.getString("videoId");
                                        if(!TextUtils.isEmpty(cvid) && cvid != null){
                                            add_download(trackModel, cvid, type, list_pos, action);
                                        }
                                    } else {
                                        showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                    showErrorMessages("Couldn't find video");
                                }
                            } else {
                                showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            showErrorMessages(R.string.txt_network_error);
        }
    }



    public void setLibrary(){
        selectedItem = mBottomNav.getMenu().getItem(1);
        selectFragment(selectedItem, true);
    }
    private void selectFragment(MenuItem item,boolean flag) {

        Fragment frag = null;
        switch (item.getItemId()) {
            case R.id.tab_home:
                frag = new HomeFragment();
                break;
            case R.id.tab_library:
                frag = new LibraryFragment();
                break;
            case R.id.tab_search:
                if(!current_page.equalsIgnoreCase("search"))
                    frag = new SearchFragment();
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();
        mBottomNav.getMenu().setGroupCheckable(0, true, true);
        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size()-1; i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            if(menuItem.getItemId() == item.getItemId()){
                menuItem.setChecked(true);
            }

        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //if(!flag)ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            ft.replace(R.id.fragmentContainer, frag, frag.getTag());
            ft.commit();
        }

    }
    public void onClickAccount(){
        SettingFragment frag = new SettingFragment();
        pushFragment(frag, true, SettingFragment.class.getName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlayPauseMain:
                if (isPlaying) {
                    isPlaying = false;
                    Constant.isPlayed = false;
                    try {
                        if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                            mYouTubePlayer.pause();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    detail_pause.setImageResource(R.drawable.ic_play);
                    mini_pause.setImageResource(R.drawable.ic_play);
                } else {
                    isPlaying = true;
                    Constant.isPlayed = true;
                    try {
                        if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                            mYouTubePlayer.play();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    detail_pause.setImageResource(R.drawable.ic_pause);
                    mini_pause.setImageResource(R.drawable.ic_pause);
                }
                Intent intent = new Intent("com.uru.hdubaService");
                intent.putExtra("noti_play", true);
                sendBroadcast(intent);
                break;
            case R.id.mini_panel:
                mBottomNav.setVisibility(View.GONE);
                songSlideView.animateIn();
                try {
                    if(!TextUtils.isEmpty(cur_track.getFavorite()) && cur_track.getFavorite().contains("download")){
                        youtubeView.setVisibility(View.INVISIBLE);
                        detail_cover_image.setVisibility(View.VISIBLE);
                    }else{
                        String cur_playing_mode = App.readSharedSetting(PRE_PLAYING_MODE, "photo");
                        Log.e("playmode:2", cur_playing_mode);
                        if(cur_playing_mode.equalsIgnoreCase("photo")){
                            youtubeView.setVisibility(View.INVISIBLE);
                            detail_cover_image.setVisibility(View.VISIBLE);
                        }else{
                            youtubeView.setVisibility(View.VISIBLE);
                            detail_cover_image.setVisibility(View.GONE);
                        }
                    }
                    String cur_volume_mode = App.readSharedSetting(PRE_VOLUME_MODE, "invisible");
                    if(cur_volume_mode.equalsIgnoreCase("visible")){
                        volume_setting_li.setVisibility(View.VISIBLE);
                    }else{
                        volume_setting_li.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.play:

                if (isPlaying) {
                    try {
                        if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                            mYouTubePlayer.pause();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    isPlaying = false;
                    Constant.isPlayed = false;
                    mini_pause.setImageResource(R.drawable.ic_play);
                    detail_pause.setImageResource(R.drawable.ic_play);
                } else {
                    isPlaying = true;
                    try {
                        if(TextUtils.isEmpty(cur_track.getFavorite()) || !cur_track.getFavorite().contains("download")){
                            mYouTubePlayer.play();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Constant.isPlayed = true;
                    detail_pause.setImageResource(R.drawable.ic_pause);
                    mini_pause.setImageResource(R.drawable.ic_pause);
                }
                Intent intent1 = new Intent("com.uru.hdubaService");
                intent1.putExtra("noti_play", true);
                sendBroadcast(intent1);
                break;
            case R.id.close:
                songSlideView.animateOut();
                break;
            case R.id.more:
                try {
                    DetailBottomFragment detailBottomFragment = new DetailBottomFragment(cur_track, "play_view", Constant.playPos);
                    detailBottomFragment.show(getSupportFragmentManager(),"MyBottomSheet");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_song));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constant.arrayList_play.get(Constant.playPos).getCover_image());
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_song)));
                break;
            case R.id.main_favorite:
                add_favorite(cur_track, "play_view", Constant.playPos);
                break;
            case R.id.shuffle:
                if (Constant.isSuffle) {
                    Constant.isSuffle = false;
                    detail_shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle));
                } else {
                    Constant.isSuffle = true;
                    detail_shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_active));
                }
                break;
            case R.id.repeat:
                if (Constant.isRepeat) {
                    Constant.isRepeat = false;
                    detail_repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat));
                } else {
                    Constant.isRepeat = true;
                    detail_repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_active));
                }
                break;
        }
    }


    BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean nextFlag = intent.getBooleanExtra("next", false);
            if(nextFlag){
                nextPlay();
            }
            boolean prevFlag = intent.getBooleanExtra("prev", false);
            if(prevFlag){
                prevPlay();
            }
            boolean pauseFlag = intent.getBooleanExtra("pause", false);
            if(pauseFlag){
                notiPause();
            }
            boolean isPlayed = intent.getBooleanExtra("is_played", false);
            if(isPlayed){
                min_loading.setVisibility(View.GONE);
                detail_pause.setImageResource(R.drawable.ic_pause);
                mini_pause.setImageResource(R.drawable.ic_pause);
                seekUpdation();
            }


        }
    };


    @Override
    public void onPause() {
        seekHandler.removeCallbacks(run);
        youtubeView.enableBackgroundPlayback(true);
        super.onPause();
    }
    @Override
    public void onDestroy() {
        youtubeView.release();
        PRDownloader.cancelAll();
        PRDownloader.shutDown();
        unregisterReceiver(mActivityReceiver);
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){
        if(songSlideView.isVisible()){
            songSlideView.animateOut();
            mini_panel.setVisibility(View.VISIBLE);
            mBottomNav.setVisibility(View.VISIBLE);
        }else{
            boolean flag = popFragment();
            if(!flag){
                View dialogView = getLayoutInflater().inflate(R.layout.dlg_exit, null);

                final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(MainActivity.this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

                dlg.setCanceledOnTouchOutside(true);
                final TextView ok = dialogView.findViewById(R.id.ok);
                final TextView cancel = dialogView.findViewById(R.id.cancel);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                        finish();
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
        }
    }
}