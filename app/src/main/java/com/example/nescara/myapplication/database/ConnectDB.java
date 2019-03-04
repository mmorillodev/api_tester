package com.example.nescara.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectDB extends SQLiteOpenHelper {
    private static final String NAME = "history.db";
    public static final String TABLE = "history";
    public static final String ID = "id";
    public static final String PARAMETERS = "parameters";
    public static final String HEADERS = "headers";
    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String SUCCESS_FLG = "success_flg";
    private static final int VERSION = 1;

    public ConnectDB(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                URL + " TEXT , " +
                PARAMETERS + " TEXT, " +
                SUCCESS_FLG + " INTEGER, " +
                METHOD + " TEXT, " +
                HEADERS + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
//        onCreate(db);
    }
}
