package com.example.notepad.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notepad.db.ImagePathDB;

public class PathDbCrudUtils {

    // DB 관련 메소드
    public static void dbInsert(Context context, String imgPath, String imgKind, String memoKey){
        ImagePathDB imagePathDB;
        SQLiteDatabase database;
        try {
            imagePathDB = new ImagePathDB(context,"ImagePath",null,1);
            database = imagePathDB.getWritableDatabase();

            String sql = "insert into ImagePath values('"+imgPath+"', '"+imgKind+"', '"+memoKey+"','0')";
            database.execSQL(sql);

            imagePathDB.close();
            database.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // memokey 같은걸로 ImagePath 여러개 반환하는것 만들어야함
    // ImagePath로 1개 이미지 찾음
    public static void dbSelect(Context context, String imgPath, String imgKind, String memoKey){
        ImagePathDB imagePathDB;
        SQLiteDatabase database;
        Cursor cursor;
        try {
            imagePathDB = new ImagePathDB(context, "ImagePath", null, 1);
            database = imagePathDB.getReadableDatabase();

            String sql = "select * from ImagePath where id_imagePath='"+imgPath+"'";
            cursor = database.rawQuery(sql, null);
            while(cursor.moveToNext()){
                if(cursor.getString(3).equals("1")){
                    continue;
                }
                String id = cursor.getString(0);
                String pass = cursor.getString(1);
                String meKey = cursor.getString(2);

                System.out.println("id_imagePath: "+id+" imgKind: "+pass +" memoKey: "+ meKey);
            }
            imagePathDB.close();
            database.close();
            cursor.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 삭제
    public static void dbDelete(Context context, String imgPath){
        ImagePathDB imagePathDB;
        SQLiteDatabase database;
        try {
            imagePathDB = new ImagePathDB(context,"ImagePath",null,1);
            database = imagePathDB.getWritableDatabase();

            String sql = "Update ImagePath SET deleteImg = 1 Where = '"+imgPath+"'";
            database.execSQL(sql);

            imagePathDB.close();
            database.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
