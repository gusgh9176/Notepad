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

    private static Map<String, DetailNotepadVO> db = new LinkedHashMap<String, DetailNotepadVO>(); // 키, 오브젝트 저장
    private static File file;

    // 오브젝트 추가
    public static void addNotepad(String index, DetailNotepadVO DetailNotepadVO){
        db.put(index, DetailNotepadVO);
    }

    // DetailNotepadVO 객체 반환
    public static DetailNotepadVO getNotepad(String index){
        return db.get(index);
    }

    // index들 얻음
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

    // 업데이트 한 db 변수 로컬에 저장
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

    // 로컬에 저장했던 db 불러오기
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
