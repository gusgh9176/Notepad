package com.example.notepad.db;

import com.example.notepad.vo.ImageVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImageDB {
    private static Map<String, ImageVO> db = new LinkedHashMap<String, ImageVO>();
    private static File file;

    public static void addImage(String index, ImageVO DetailNotepadVO){
        db.put(index, DetailNotepadVO);
    }

    public static ImageVO getImage(String index){
        return db.get(index);
    }

    public static List<String> getIndexes(){
        Iterator<String> keys = db.keySet().iterator();

        List<String> keyList = new ArrayList<String>();
        String key ="";
        while (keys.hasNext()){
            key = keys.next();
            keyList.add(key);
        }
        return keyList;
    }

    public static void save(File dir){
        ObjectOutputStream oos = null;
        try {
            file = new File(dir, "image Test");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(db);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{ if(oos != null) oos.close(); }catch(Exception e){e.printStackTrace();}
        }
    }

    public static void load(File dir){
        ObjectInputStream ois = null;
        try {
            file = new File(dir, "image Test");
            ois = new ObjectInputStream(new FileInputStream(file));
            db = (HashMap<String, ImageVO>) ois.readObject();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{ if(ois != null) ois.close(); }catch (Exception e){e.printStackTrace();}
        }
    }
}
