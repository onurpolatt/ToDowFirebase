package com.example.onurp.myapplication.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by onurp on 7.09.2017.
 */

public class dbHandler extends SQLiteOpenHelper {
    public static final String TAG = "dbHandler";
    public static dbHandler mDatabaseInstance = null;
    public Context mContext;

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "taskdbs.db";
    //tablo isimleri
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_SHEADERS = "sheaders";

    //tasks tablosu için sütun isimleri
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_HEADER = "header";
    public static final String COLUMN_IMPLVL = "implvl";
    public static final String COLUMN_SGROUPID = "sgroup_ID";
    public static final String COLUMN_DATE = "date";

    //sheaders tablosu için sütun isimleri
    public static final String COLUMN_HEADER_ID="section_ID";
    public static final String COLUMN_SECTION_NAME="section_name";

    public static final String CREATE_TABLE_TASKS= "create table "
            + TABLE_TASKS + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_HEADER + " TEXT, "
            + COLUMN_CONTENT + " TEXT, "
            + COLUMN_IMPLVL + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_SGROUPID + " INTEGER, "
            + " FOREIGN KEY ("+COLUMN_SGROUPID+") REFERENCES "+TABLE_SHEADERS+"("+COLUMN_HEADER_ID+"));";

    public static final String CREATE_TABLE_SHEADERS = "CREATE TABLE "
            + TABLE_SHEADERS + "(" + COLUMN_HEADER_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_SECTION_NAME + " TEXT" + ")";

    public static dbHandler newInstance(Context context){
        if (mDatabaseInstance == null){
            mDatabaseInstance = new dbHandler(context.getApplicationContext());
        }
        return mDatabaseInstance;
    }
    public dbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public dbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,DATABASE_NAME,factory,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, " TABLO OLUŞTURMA KISMI. ");
            db.execSQL(CREATE_TABLE_SHEADERS);
            db.execSQL(CREATE_TABLE_TASKS);
            db.execSQL("PRAGMA foreign_keys=ON");
        } catch (SQLException e) {
            Log.e(TAG, " Tablo oluşturma sırasında hata oluştu. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SHEADERS);
        onCreate(db);
    }
}
