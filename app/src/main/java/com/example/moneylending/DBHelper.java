package com.example.moneylending;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.service.autofill.UserData;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context,"Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user_info(id TEXT PRIMARY KEY , auth_key TEXT, transact_id TEXT, wallet INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user_info");
    }

    public boolean insertUserData(String id, String auth_key, String transact_id, int wallet){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("auth_key", auth_key);
        contentValues.put("transact_id", transact_id);
        contentValues.put("wallet", wallet);
        long result = DB.insert("user_info",null,contentValues);
        return result != -1;
    }
    public boolean insertUserData(String id, String auth_key){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("auth_key", auth_key);
        long result = DB.insert("user_info",null,contentValues);
        return result != -1;
    }
    public boolean updateUserData(String id, String auth_key){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("auth_key", auth_key);
        long result = DB.update("user_info",contentValues,"id=?",new String[]{id});
        return result != -1;
    }
    public boolean updateUserWallet(String id, int wallet){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("wallet", wallet);
        long result = DB.update("user_info",contentValues,"id=?",new String[]{id});
        return result != -1;
    }
    public boolean updateUserData(String id, String auth_key, String transact_id, int wallet){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("auth_key", auth_key);
        contentValues.put("transact_id", transact_id);
        contentValues.put("wallet", wallet);
        long result = DB.update("user_info",contentValues,"id=?",new String[]{id});
        return result != -1;
    }
    public boolean deleteUserData(String id){
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("user_info", "id=?", new String[]{id});
        return result != -1;
    }
    public boolean deleteAllUserData(){
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("user_info", null, null);
        return result != -1;
    }
    public Cursor getData(){
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT id, auth_key, transact_id, wallet FROM user_info", null);
    }
    public Cursor getInfo(String id){
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT id, auth_key, transact_id, wallet FROM user_info WHERE ID="+id, null);
    }
}
