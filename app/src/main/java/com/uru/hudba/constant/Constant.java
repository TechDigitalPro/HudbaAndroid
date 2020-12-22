package com.uru.hudba.constant;

import android.content.Context;

import com.uru.hudba.model.DownloadModel;
import com.uru.hudba.model.TrackModel;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {
    public static int playPos = 0;
    public static int cur_download_pos = 0;
    public static String cur_download_trackid = "";
    public static ArrayList<TrackModel> arrayList_play = new ArrayList<>();
    public static ArrayList<DownloadModel> downloading_list = new ArrayList<>();

    public static Boolean isRepeat = false, isSuffle = false, isPlayed = false, isToNoti = false, isAppOpen = false, isOnline = true,
            isDownloaded = false, isBannerAd = true, isInterAd = true, isNativeAd = true, isSongDownload = false, isUpdate = false;
    public static Context context;
    public static String cur_track_id = "";
    public static String cur_videoId = "";

}
