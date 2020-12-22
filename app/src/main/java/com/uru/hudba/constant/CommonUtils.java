package com.uru.hudba.constant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import com.uru.hudba.R;
import com.uru.hudba.model.UserModel;

import java.io.File;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class CommonUtils {
    public static final int NETWORK_TIME = 30000;
    public static Dialog mProgressDlg;
    public static UserModel currentUser = new UserModel();
    public static boolean network_flag = false;

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static float scale = 0;
    public static String m_membership_status = "unpaid";
    public static void showLoadingDialog(final Activity activity) {
        if (mProgressDlg != null && mProgressDlg.isShowing()) return;
        try {
            mProgressDlg = new Dialog(activity);
            mProgressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.transparent_color)));
            View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null);
            mProgressDlg.setContentView(contentView);
            mProgressDlg.setCancelable(false);
            mProgressDlg.setTitle(null);
            mProgressDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideLoadingDialog() {
        try {
            if (mProgressDlg != null && mProgressDlg.isShowing()) {
                mProgressDlg.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getCountry(Context context) {
        String country = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
        return country;
    }
    public static long getSeekFromPercentage(int percentage, long totalDuration) {

        long currentSeconds = 0;
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        currentSeconds = (percentage * totalSeconds) / 100;

        // return percentage
        return currentSeconds * 1000;
    }
    public static String milliSecondsToTimer(long milliseconds, long duration) {
        String finalTimerString = "";
        String hourString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        int temp_hour = (int) (duration / (1000 * 60 * 60));
        if (temp_hour != 0) {
            hourString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        // Prepending 0 to minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = hourString + minutesString + ":" + secondsString;

//        // return timer string
        return finalTimerString;
    }
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }
    public static String getDeviceToken(Context context) {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static Bitmap fixPhotoOrientation(File imgFile, Bitmap bmp) {
        try {
            ExifInterface ei = new ExifInterface(imgFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e("***", "fixPhotoOrientation: " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bmp, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bmp, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bmp, 270);
                default:
                    return bmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return bmp;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    public static int dp2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }
    public static int px2dp(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }
    public static String getDateFromUTCTimestamp(long mTimestamp, String mDateFormate) {
        String date = "";
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat(mDateFormate, Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)mTimestamp*1000);
        try {
            date = sdf.format(currenTimeZone);
        }catch (Exception e){
            date = "";
            e.printStackTrace();
        }
        return date;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    //add comma to float number. ex: 12345.67 -> 12,345.67
    private static String number_format(float num){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        try{
            return myFormat.format(num);
        }catch (Exception e){
            return "0";
        }
    }

    public static boolean isEmpty(String value){
        if(value == null || value.trim().isEmpty()){
            return true;
        }
        return false;
    }
    //get float value from string

    public static String removeAccent(String text) {
        String result = Normalizer.normalize(text, Normalizer.Form.NFD);
        return result.replaceAll("[^\\p{ASCII}]", "");
    }
}