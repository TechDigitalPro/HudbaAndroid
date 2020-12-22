package com.uru.hudba.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.uru.hudba.constant.DBConfig;
import com.uru.hudba.model.TrackModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DatabaseQueryClass {
    private Context context;

    public DatabaseQueryClass(Context context){
        this.context = context;
   }

    public long insert_download(TrackModel tmpfileData){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfig.COLUMN_TRACKID, tmpfileData.getTrack_id());
        contentValues.put(DBConfig.COLUMN_TITLE, tmpfileData.getTitle());
        contentValues.put(DBConfig.COLUMN_ARTISTID, tmpfileData.getArtist_id());
        contentValues.put(DBConfig.COLUMN_ARTISTNAME, tmpfileData.getArtist_name());
        contentValues.put(DBConfig.COLUMN_IMAGE, tmpfileData.getCover_image());
        contentValues.put(DBConfig.COLUMN_ALBUMID, tmpfileData.getAlbum_id());
        contentValues.put(DBConfig.COLMUN_ALBUMNAME, tmpfileData.getAlbum_name());
        contentValues.put(DBConfig.COLMUN_VIDEOID, tmpfileData.getVideoId());
        contentValues.put(DBConfig.COLMUN_DESCRIPTION, tmpfileData.getDescription());

        try {
            id = sqLiteDatabase.insertOrThrow(DBConfig.TABLE_DOWNLOAD, null, contentValues);
        } catch (SQLiteException e){

        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }
    public long insert_home(TrackModel tmpfileData){

        long id = -1;
        if(getCountHome()>10){
            delete_home_one();
        }
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfig.COLUMN_TRACKID, tmpfileData.getTrack_id());
        contentValues.put(DBConfig.COLUMN_TITLE, tmpfileData.getTitle());
        contentValues.put(DBConfig.COLUMN_ARTISTID, tmpfileData.getArtist_id());
        contentValues.put(DBConfig.COLUMN_ARTISTNAME, tmpfileData.getArtist_name());
        contentValues.put(DBConfig.COLUMN_IMAGE, tmpfileData.getCover_image());
        contentValues.put(DBConfig.COLUMN_ALBUMID, tmpfileData.getAlbum_id());
        contentValues.put(DBConfig.COLMUN_ALBUMNAME, tmpfileData.getAlbum_name());
        contentValues.put(DBConfig.COLMUN_VIDEOID, tmpfileData.getVideoId());
        contentValues.put(DBConfig.COLMUN_DESCRIPTION, tmpfileData.getDescription());

        try {
            id = sqLiteDatabase.insertOrThrow(DBConfig.TABLE_HOME, null, contentValues);
        } catch (SQLiteException e){

        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }
    public void delete_home_one(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(new StringBuilder().append("DELETE FROM ").append(DBConfig.TABLE_HOME).append(" ORDER BY ").append(DBConfig.COLUMN_ID).append(" ASC LIMIT(0,1);").toString());
    }


    public boolean deleteTmpFile(String track_id){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        return sqLiteDatabase.delete(DBConfig.TABLE_DOWNLOAD,DBConfig.COLUMN_TRACKID + "=" + "'"+track_id+"'",null)>0;
    }
    public boolean delete_home(String id){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        return sqLiteDatabase.delete(DBConfig.TABLE_HOME,DBConfig.COLUMN_ID + "=" + "'"+id+"'",null)>0;
    }



    public List<TrackModel> getDownloadList(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(DBConfig.TABLE_DOWNLOAD, null, null, null, null, null, null, null);
            if(cursor!=null)
                if(cursor.moveToFirst()){
                    ArrayList<TrackModel> fileList = new ArrayList<>();
                    fileList.clear();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(DBConfig.COLUMN_ID));
                        String track_id = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TRACKID));
                        String title = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TITLE));
                        String artist_id = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_ARTISTID));
                        String artist_name = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_ARTISTNAME));
                        String cover_image = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_IMAGE));
                        String album_id = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_ALBUMID));
                        String album_name = cursor.getString(cursor.getColumnIndex(DBConfig.COLMUN_ALBUMNAME));
                        String video_id = cursor.getString(cursor.getColumnIndex(DBConfig.COLMUN_VIDEOID));
                        String description = cursor.getString(cursor.getColumnIndex(DBConfig.COLMUN_DESCRIPTION));

                        TrackModel trackModel = new TrackModel();
                        trackModel.setId(String.valueOf(id));
                        trackModel.setTitle(title);
                        trackModel.setAlbum_name(album_name);
                        trackModel.setAlbum_id(album_id);
                        trackModel.setCover_image(cover_image);
                        trackModel.setArtist_id(artist_id);
                        trackModel.setArtist_name(artist_name);
                        trackModel.setTrack_id(track_id);
                        trackModel.setDescription(description);
                        trackModel.setVideoId(video_id);

                        fileList.add(trackModel);
                    }   while (cursor.moveToNext());

                    return fileList;
                }
        } catch (Exception e){
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }
//    public List<FileInfoData> getAllRecentFile() throws ParseException {
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String curdate = dateFormat.format(new Date());
//        Date myDate = dateFormat.parse(curdate);
//        Date searchDate = new Date(myDate.getTime() - 864000000L);
//
//
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
//        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
//
//        Cursor cursor = null;
//        try {
//            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_FILE_BACKUP);
//            String selection="deldate > ?";
//            String[] whereClause={dateFormat.format(searchDate)};
//
//            if(count<9)
//                cursor = sqLiteDatabase.query(DBConfig.TABLE_FILE_BACKUP, null, selection, whereClause, null, null, "_id DESC", null);
//            else
//                cursor = sqLiteDatabase.query(DBConfig.TABLE_FILE_BACKUP, null, selection, whereClause, null, null, "_id DESC","8");
//            if(cursor!=null)
//                if(cursor.moveToFirst()){
//                    List<FileInfoData> fileList = new ArrayList<>();
//                    do {
//                        int id = cursor.getInt(cursor.getColumnIndex(DBConfig.COLUMN_TMP_ID));
//                        String name = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_NAME));
//                        String link = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_LINK));
//                        String format = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_EXTENSION));
//                        long size = cursor.getLong(cursor.getColumnIndex(DBConfig.COLUMN_TMP_SIZE));
//
//                        //       String deldate = parseDate(cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_DELDATE)),"dd/MM/yyyy");
//                        String deldate = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_DELDATE));
//                        String recoveryPath = cursor.getString(cursor.getColumnIndex(DBConfig.COLMUN_TMP_RECOVERYPATH));
//                        String original = cursor.getString(cursor.getColumnIndex(DBConfig.COLMUN_TMP_ORIGINAL));
//                        String type = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_TMP_TYPE));
//                        fileList.add(new FileInfoData(id, name, link, format, size, deldate, recoveryPath, type,original));
//                    }   while (cursor.moveToNext());
//
//                    return fileList;
//                }
//        } catch (Exception e){
//            Toast.makeText(context, "DB operation fail", Toast.LENGTH_SHORT).show();
//        } finally {
//            if(cursor!=null)
//                cursor.close();
//            sqLiteDatabase.close();
//        }
//
//        return Collections.emptyList();
//    }


    public long getCountDownload(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_DOWNLOAD);
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return count;
    }

    public long getCountHome(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_HOME);
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return count;
    }
    public long getCountFavorite(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_FAVORITE);
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return count;
    }
//    public long setForgetPassword(String password){
//
//        long id = -1;
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
//        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DBConfig.COLUMN_FORGET_PASSWORD, password);
//        try {
//            int count = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_USER_INFO,null,null);
//            if(count!=0)
//                id = sqLiteDatabase.update(DBConfig.TABLE_USER_INFO, contentValues, null,null);
//            else
//                id = (int) sqLiteDatabase.insertOrThrow(DBConfig.TABLE_USER_INFO, null, contentValues);
//        } catch (SQLiteException e){
//
//        } finally {
//            sqLiteDatabase.close();
//        }
//
//        return id;
//    }


//    public long setRecoveryEmail(String mail){
//
//        long id = -1;
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
//        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DBConfig.COLUMN_EMAIL, mail);
//        try {
//            int count = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, DBConfig.TABLE_USER_INFO,null,null);
//            if(count!=0)
//                id = sqLiteDatabase.update(DBConfig.TABLE_USER_INFO, contentValues, null,null);
//            else
//                id = (int) sqLiteDatabase.insertOrThrow(DBConfig.TABLE_USER_INFO, null, contentValues);
//        } catch (SQLiteException e){
//
//        } finally {
//            sqLiteDatabase.close();
//        }
//
//        return id;
//    }


}
