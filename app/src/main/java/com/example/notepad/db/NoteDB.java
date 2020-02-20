package com.example.notepad.db;

import com.example.notepad.vo.DetailNotepadVO;

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

public class NoteDB {

    private static Map<String, DetailNotepadVO> db = new LinkedHashMap<String, DetailNotepadVO>();
    private static File file;

    public static void addNotepad(String index, DetailNotepadVO DetailNotepadVO){
        db.put(index, DetailNotepadVO);
    }

    public static DetailNotepadVO getNotepad(String index){
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
            file = new File(dir, "memo Test");
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
            file = new File(dir, "memo Test");
            ois = new ObjectInputStream(new FileInputStream(file));
            db = (HashMap<String, DetailNotepadVO>) ois.readObject();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{ if(ois != null) ois.close(); }catch (Exception e){e.printStackTrace();}
        }
    }
}
