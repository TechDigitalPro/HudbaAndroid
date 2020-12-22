package com.uru.hudba.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uru.hudba.constant.DBConfig;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = DBConfig.DATABASE_NAME;

    // Constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    //    Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static DatabaseHelper getInstance(Context context) {
        if(databaseHelper==null){
            synchronized (DatabaseHelper.class) {
                if(databaseHelper==null)
                    databaseHelper = new DatabaseHelper(context);
            }
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        String CREATE_DOWNLOAD_TABLE = "CREATE TABLE " + DBConfig.TABLE_DOWNLOAD + "("
                + DBConfig.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBConfig.COLUMN_TRACKID + " TEXT , "
                + DBConfig.COLUMN_TITLE + " TEXT , "
                + DBConfig.COLUMN_ARTISTID + " TEXT , "
                + DBConfig.COLUMN_ARTISTNAME + " TEXT , "
                + DBConfig.COLUMN_IMAGE + " TEXT , "
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_VIDEOID + " TEXT ,"
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_DESCRIPTION + " TEXT "
                + ")";
        db.execSQL(CREATE_DOWNLOAD_TABLE);

        String CREATE_HOME_TABLE = "CREATE TABLE " + DBConfig.TABLE_HOME + "("
                + DBConfig.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBConfig.COLUMN_TRACKID + " TEXT , "
                + DBConfig.COLUMN_TITLE + " TEXT , "
                + DBConfig.COLUMN_ARTISTID + " TEXT , "
                + DBConfig.COLUMN_ARTISTNAME + " TEXT , "
                + DBConfig.COLUMN_IMAGE + " TEXT , "
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_VIDEOID + " TEXT ,"
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_DESCRIPTION + " TEXT "
                + ")";
        db.execSQL(CREATE_HOME_TABLE);

        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + DBConfig.TABLE_FAVORITE + "("
                + DBConfig.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBConfig.COLUMN_TRACKID + " TEXT , "
                + DBConfig.COLUMN_TITLE + " TEXT , "
                + DBConfig.COLUMN_ARTISTID + " TEXT , "
                + DBConfig.COLUMN_ARTISTNAME + " TEXT , "
                + DBConfig.COLUMN_IMAGE + " TEXT , "
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_VIDEOID + " TEXT ,"
                + DBConfig.COLUMN_ALBUMID + " TEXT ,"
                + DBConfig.COLMUN_DESCRIPTION + " TEXT "
                + ")";
        db.execSQL(CREATE_FAVORITE_TABLE);

      }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_DOWNLOAD);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_HOME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_FAVORITE);
        // Create tables again
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

}
