package com.uru.hudba.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.NotificationCompat;

import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;


public class DownloadVideoAsyn extends AsyncTask<String, Integer, String> {
    private String title;
    private int check;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private String CHANNEL_ID = "download_video_tiktok_auto";
    private String pathVideo = title + ".m4a";
    private int id = 1232;
    private Context mContext;
    private NotificationChannel mChannel;
    Notification noti;
    private String PATH = "";

    public DownloadVideoAsyn(String title, int check, Context context) {
        this.title = title;
        this.check = check;
        this.mContext = context;
        this.PATH = ((MainActivity) this.mContext).getFilesDir().getAbsolutePath() + File.separator + "video"  + File.separator;
    }

    private EventDownloadVideoCallBack callBack;

    public void setCallBack(EventDownloadVideoCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (check == 2) {
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);


            Intent iCancel = new Intent();
            id = id + 1;
            iCancel.putExtra("id", id);
            iCancel.putExtra("pathVideo", pathVideo);
            iCancel.setAction("CANCEL_ACTION");
            PendingIntent PICancel = PendingIntent.getBroadcast(mContext, 0, iCancel, PendingIntent.FLAG_UPDATE_CURRENT);


            builder.setSmallIcon(R.drawable.ic_download)
                    .setContentTitle(title.replace("&#39;", "") + ".m4a")
                    .setContentText(mContext.getString(R.string.download))
                    .setChannelId(CHANNEL_ID)
                    .setPriority(Notification.PRIORITY_MAX)

                    .addAction(R.drawable.icon_play_thumb, "Cancel", PICancel)
                    .setAutoCancel(true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                mChannel = new NotificationChannel(CHANNEL_ID, "channel-name", importance);
                mChannel.setSound(null, null);
                manager.createNotificationChannel(mChannel);
            }

        }
    }

    @Override

    protected String doInBackground(String... strings) {
        String link = strings[0];
        try {
            URL url = new URL(link);
            // check folder exist
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdir();
            }
            String path = PATH + title + ".m4a";

            File file = new File(path);
            long total = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int fileLength = connection.getContentLength();

            //=============================
            byte[] b = new byte[1024];
            int count = inputStream.read(b);

            while (count != -1) {
                total += count;
                fileOutputStream.write(b, 0, count);
                count = inputStream.read(b);
                int progress = (int) (total * 100 / fileLength);
                publishProgress(progress);
                if (check == 2) {
                    builder.setProgress(100, progress, false);
                    builder.setContentText(mContext.getString(R.string.download) + " " + progress + "%");
                    noti = builder.build();
                    noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                    manager.notify(id, noti);
                    if (progress == 100) {
                        manager.cancel(id);
                    }
                }
            }
            inputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                URL url = new URL(link);
                // check folder exist
                File folder = new File(PATH);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                Date currentTime = Calendar.getInstance().getTime();
                title = currentTime.getTime() + "";
                String path = PATH + title + ".m4a";
                File file = new File(path);
                long total = 0;
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                int fileLength = connection.getContentLength();

                //=============================
                byte[] b = new byte[1024];
                int count = inputStream.read(b);

                while (count != -1) {
                    total += count;
                    fileOutputStream.write(b, 0, count);
                    count = inputStream.read(b);
                    int progress = (int) (total * 100 / fileLength);
                    publishProgress(progress);
                    if (check == 2) {
                        builder.setProgress(100, progress, false);
                        builder.setContentText(mContext.getString(R.string.download) + " " + progress + "%");
                        noti = builder.build();
                        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        manager.notify(id, noti);
                        if (progress == 100) {
                            manager.cancel(id);
                        }
                    }
                }
                inputStream.close();
                fileOutputStream.close();
            } catch (Exception ex) {
                return null;
            }
        }
        return PATH + title + ".m4a";
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            Uri uri = Uri.parse("file://" + "/storage/emulated/0/musicdownloader/" + s);
            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            mContext.sendBroadcast(scanFileIntent);
            callBack.downloadVideoFinish(s);
        } else {
            callBack.downloadVideoFail("Error");
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (check == 1) {
            if (callBack != null) {
                callBack.updateProgress(values[0]);
            }
        }
    }

    public interface EventDownloadVideoCallBack {
        void downloadVideoFinish(String s);

        void updateProgress(int pro);

        void downloadVideoFail(String fail);

    }


}
