package com.example.notepad.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImagePathDB extends SQLiteOpenHelper {
    public ImagePathDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // deleteImg 가 0이면 존재하는 이미지, 1이면 삭제된 이미지
        String sql = "CREATE TABLE ImagePath" + "(id_imagePath Text primary key, imageKind Text, memoKey Text, deleteImg Integer);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }
}