package com.uru.hudba.musicservice;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.uru.hudba.application.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.constant.Constant;
import com.uru.hudba.constant.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NotiService extends IntentService {
    /**
     * @param name
     * @deprecated
     */


    private Toast errToast;
    private boolean isNewSong = false;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder notification;
    RemoteViews bigViews, smallViews;
    Bitmap bitmap;
    public static final String ACTION_PLAY = "action.ACTION_PLAY";
    public static final String ACTION_NEXT = "action.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "action.ACTION_PREVIOUS";
    public static final String ACTION_STOP = "action.ACTION_STOP";
    public static final String ACTION_SEEKTO = "action.ACTION_SEEKTO";
    public static final String ACTION_PAUSE = "action.ACTION_PAUSE";


    static NotiService notiService;

    public static MediaPlayer mediaPlayer;

    public NotiService() {
        super(null);
    }

    public long getDuration() {
        if(mediaPlayer == null) {
            return 0;
        } else {
            return mediaPlayer.getDuration();
        }
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    static public NotiService getInstance() {
        if (notiService == null) {
            notiService = new NotiService();
        }
        return notiService;
    }
    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        try {
            registerReceiver(mActivityReceiver, new IntentFilter("com.uru.hdubaService"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String action = intent.getAction();
            switch (action) {

                case ACTION_PLAY:
                    startNewSong();
                    break;
                case ACTION_PAUSE:
                    togglePlay();
                    break;
                case ACTION_STOP:
                    stop(intent);
                    break;
                case ACTION_PREVIOUS:
                    previous();
                    break;
                case ACTION_NEXT:
                    next();
                    break;
                case ACTION_SEEKTO:
                    seekTo(intent.getExtras().getLong("seekto"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    private void startNewSong() {
        if(mediaPlayer != null){
            mediaPlayer.pause();
            Constant.isPlayed = false;
        }
        isNewSong = true;
        String url;
        try {
            if(!TextUtils.isEmpty(Constant.arrayList_play.get(Constant.playPos).getFavorite()) && Constant.arrayList_play.get(Constant.playPos).getFavorite().contains("download")){
                String path = getFilesDir().getAbsolutePath() + File.separator + "video"  + File.separator;
                url = Constant.arrayList_play.get(Constant.playPos).getTrack_id();

                String video_folder = getFilesDir().getAbsolutePath() + File.separator + "video";
                File mp3_file = new File(video_folder, Constant.arrayList_play.get(Constant.playPos).getTrack_id() + ".mp3");
                if(mp3_file.exists()) {
                    url = path + url + ".mp3";
                }
                File m4a_file = new File(video_folder, Constant.arrayList_play.get(Constant.playPos).getTrack_id() + ".m4a");
                if(m4a_file.exists()) {
                    url = path + url + ".m4a";
                }

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), R.string.msg_file_no1, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        mediaPlayer.start();
                        Constant.isPlayed = true;
                        Intent intent_play = new Intent("com.uru.hdubaActivity");
                        intent_play.putExtra("is_played", true);
                        sendBroadcast(intent_play);
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

//                        Log.e("toast", "aaa" + mp.getAudioSessionId());
//                        if(errToast == null){
//                            errToast = Toast.makeText(getApplicationContext(),"FileSource Get fail", Toast.LENGTH_LONG);
//                            errToast.show();
//                            mediaPlayer = null;
//                        }

                        //next();
                        return false;
                    }
                });
                if (notification == null) {
                    createNoti();
                } else {
                    updateNoti();
                }
            }else{
                if (notification == null) {
                    createNoti();
                } else {
                    updateNoti();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void togglePlay(){
        if(!TextUtils.isEmpty(Constant.arrayList_play.get(Constant.playPos).getFavorite()) && Constant.arrayList_play.get(Constant.playPos).getFavorite().contains("download")){
            try {
                if(mediaPlayer != null){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                                    Constant.isPlayed = false;
                    }else{
                        mediaPlayer.start();
                                  Constant.isPlayed = true;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            updateNotiPlay(false);
            Constant.isPlayed = !Constant.isPlayed;
        }else{
            Constant.isPlayed = !Constant.isPlayed;
            updateNotiPlay(true);
        }
        Intent intent = new Intent("com.uru.hdubaActivity");
        intent.putExtra("pause", true);
        sendBroadcast(intent);

    }
    private void next() {
        Intent intent = new Intent("com.uru.hdubaActivity");
        intent.putExtra("next", true);
        sendBroadcast(intent);
    }
    private void previous() {
        Intent intent = new Intent("com.uru.hdubaActivity");
        intent.putExtra("prev", true);
        sendBroadcast(intent);
    }
    private void stop(Intent intent) {
        try {
            Constant.isPlayed = false;
            if(mediaPlayer != null){
                mediaPlayer.release();
            }
            stopService(intent);
            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void seekTo(long seek) {
        mediaPlayer.seekTo((int) seek);
        mediaPlayer.start();
    }
    private void createNoti() {
        bigViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        smallViews = new RemoteViews(getPackageName(), R.layout.layout_noti_small);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("isnoti", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, NotiService.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotiService.class);
        playIntent.setAction(ACTION_PAUSE);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotiService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotiService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "hudba_ch_1";
        notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setPriority(Notification.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(Constant.arrayList_play.get(Constant.playPos).getTitle())
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setOnlyAlertOnce(false);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);

            MediaSessionCompat mMediaSession;
            mMediaSession = new MediaSessionCompat(getApplicationContext(), getString(R.string.app_name));
            mMediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession.getSessionToken())
                    .setShowCancelButton(true)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP)))
                    .addAction(new NotificationCompat.Action(
                            R.drawable.mini_prev, "Previous",
                            ppreviousIntent))
                    .addAction(new NotificationCompat.Action(
                            R.drawable.mini_pause, "Pause",
                            pplayIntent))
                    .addAction(new NotificationCompat.Action(
                            R.drawable.mini_next, "Next",
                            pnextIntent))
                    .addAction(new NotificationCompat.Action(
                            R.drawable.ic_close, "Close",
                            pcloseIntent));

            notification.setContentTitle(Constant.arrayList_play.get(Constant.playPos).getTitle());
            notification.setContentText(Constant.arrayList_play.get(Constant.playPos).getArtist_name());
        } else {
            bigViews.setOnClickPendingIntent(R.id.imageView_noti_play, pplayIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_next, pnextIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_prev, ppreviousIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_close, pcloseIntent);
            smallViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

            bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);

            bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getTitle());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getTitle());

            bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist_name());
            smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist_name());

            notification.setCustomContentView(smallViews).setCustomBigContentView(bigViews);
        }

        startForeground(101, notification.build());
        updateNotiImage();
    }

    @SuppressLint("StaticFieldLeak")
    private void updateNotiImage() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    if(Constant.arrayList_play.get(Constant.playPos).getFavorite().contains("download")){
                        getBitmapFromLocal(Constant.arrayList_play.get(Constant.playPos).getTrack_id());
                    }else{
                        getBitmapFromURL(Constant.arrayList_play.get(Constant.playPos).getCover_image());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notification.setLargeIcon(bitmap);
                    } else {
                        bigViews.setImageViewBitmap(R.id.imageView_noti, bitmap);
                        smallViews.setImageViewBitmap(R.id.status_bar_album_art, bitmap);
                    }
                    mNotificationManager.notify(101, notification.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void updateNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setContentTitle(Constant.arrayList_play.get(Constant.playPos).getTitle());
            notification.setContentText(Constant.arrayList_play.get(Constant.playPos).getArtist_name());
        } else {
            bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getTitle());
            bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist_name());
            smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist_name());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getTitle());
        }
        updateNotiImage();
        updateNotiPlay(true);
    }
    @SuppressLint("RestrictedApi")
    private void updateNotiPlay(Boolean isPlay) {
        try {
            if(isPlay){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification.mActions.remove(1);
                    Intent playIntent = new Intent(this, NotiService.class);
                    playIntent.setAction(ACTION_PAUSE);
                    PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (!Constant.isPlayed) {
                        notification.mActions.add(1, new NotificationCompat.Action(
                                R.drawable.mini_pause, "Pause",
                                ppreviousIntent));
                    } else {
                        notification.mActions.add(1, new NotificationCompat.Action(
                                R.drawable.mini_play, "Play",
                                ppreviousIntent));
                    }
                } else {
                    if (!Constant.isPlayed) {
                        bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);
                    } else {
                        bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_play);
                    }
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification.mActions.remove(1);
                    Intent playIntent = new Intent(this, NotiService.class);
                    playIntent.setAction(ACTION_PAUSE);
                    PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Constant.isPlayed) {
                        notification.mActions.add(1, new NotificationCompat.Action(
                                R.drawable.mini_pause, "Pause",
                                ppreviousIntent));

                    } else {
                        notification.mActions.add(1, new NotificationCompat.Action(
                                R.drawable.mini_play, "Play",
                                ppreviousIntent));
                    }
                } else {
                    if (Constant.isPlayed) {
                        bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);
                    } else {
                        bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_play);
                    }
                }
            }

            mNotificationManager.notify(101, notification.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getBitmapFromURL(String src) {
        try {
            if (App.isNetworkAvailable(getApplicationContext())) {
                URL url = new URL(src);
                InputStream input;
                if(GlobalConstants.BaseUrl.contains("https://")) {
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                } else {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                }
                bitmap = BitmapFactory.decodeStream(input);
            } else {
                try {
                    if (Constant.isDownloaded) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(src)));
                    } else {
                        //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), methods.getAlbumArtUri(Integer.parseInt(src)));
                    }
                } catch (Exception e) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
                }
            }
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }
    private void getBitmapFromLocal(String src) {
        try {
            String path = getFilesDir().getAbsolutePath() + File.separator + "image"  + File.separator;
            path = path + src + ".jpg";
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(path)));
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
        }
    }
    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(mActivityReceiver);
            try {
                if(mediaPlayer != null){
                    mediaPlayer.pause();
                    mediaPlayer.release();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //         Log.e("service", "Got broadcast");
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                boolean noti_play = intent.getBooleanExtra("noti_play", false);
                if(noti_play){
                    try {
                        if(Constant.arrayList_play.get(Constant.playPos).getFavorite().contains("download")){
                            if(Constant.isPlayed){
                                mediaPlayer.start();
                            }else{
                                mediaPlayer.pause();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    updateNotiPlay(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
}
