package com.example.nescara.myapplication.database;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.nescara.myapplication.data.Request;

import java.util.ArrayList;
import java.util.List;


public class HistoryDAO extends ConnectDB{
    private SQLiteDatabase bancoWriter;
    private SQLiteDatabase bancoReader;
    private Context context;
    private List<Request> model;

    public HistoryDAO(Context context){
        super(context);
        this.context = context;
        bancoWriter = getWritableDatabase();
        bancoReader = getReadableDatabase();
    }

    public void insert(Request obj){
        String sql = "INSERT INTO " + TABLE + " ("
                 + URL + ", " + PARAMETERS + ", "
                 + HEADERS + ", " + METHOD + ", " + SUCCESS_FLG + ")"
                 + " VALUES (\"" + obj.getUrl() + "\", '" + strEscape(obj.getParams()) + "', '"
                 + strEscape(obj.getHeaders()) + "', \"" + obj.getMethod() + "\", " + (obj.isSuccess_flg() ? 1 : 0) + ")";
        try {
            bancoWriter.execSQL(sql);
        } catch (SQLException e){
//            Log.d("SQLException", strEscape(obj.getParams()));
            Activity activity = (Activity) context;
            activity.runOnUiThread(() -> Toast.makeText(context, "Failed to insert in the history!", Toast.LENGTH_SHORT).show());
        }
    }

    public List<Request> read(){
        model = new ArrayList<>();
        String sql = "SELECT " + ID + ", " + URL + ", " + PARAMETERS + ", "
                + HEADERS + ", " + METHOD + ", " + SUCCESS_FLG +  " FROM " + TABLE;
        Cursor cursor = bancoReader.rawQuery(sql, new String[0]);

        while (cursor.moveToNext()){
            Request model = new Request();
            model.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
            model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            model.setMethod(cursor.getString(cursor.getColumnIndex(METHOD)));
            model.setHeaders(cursor.getString(cursor.getColumnIndex(HEADERS)));
            model.setParams(cursor.getString(cursor.getColumnIndex(PARAMETERS)));
            model.setSuccess_flg(cursor.getInt(cursor.getColumnIndex(SUCCESS_FLG)) == 1);
            this.model.add(model);
        }
        return this.model;
    }

    public void delete(){
        String sql = "DELETE FROM " + TABLE;
        bancoWriter.execSQL(sql);
    }

    public void delete(int id){
        String sql = "DELETE FROM " + TABLE + " WHERE " + ID + " = " + id;
//        Log.d("Query delete", sql);
        bancoWriter.execSQL(sql);
    }

    private String strEscape(String str){
        if(str == null) return "null";
        return str.replaceAll("'", "''");
    }
}
