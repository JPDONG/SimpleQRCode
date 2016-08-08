package com.learn.myqrcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryDBHelper extends SQLiteOpenHelper{
    //public static String CREATE_TABLE = "create table qr_history (id integer primary key autoincrement,title text,date text,message text)";
    public  String CREATE_TABLE = "CREATE TABLE qr_history (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,date TEXT,message TEXT);";
    private String TAG = "QRHistoryDBHelper";

    public QRHistoryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: true");
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
