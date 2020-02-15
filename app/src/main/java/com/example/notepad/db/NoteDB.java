package com.example.notepad.db;

import com.example.notepad.vo.NotepadVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NoteDB {

    private static Map<String, NotepadVO> db = new HashMap<String, NotepadVO>();

    public static void addArticle(String index, NotepadVO NotepadVO){
        db.put(index, NotepadVO);
    }

    public static NotepadVO getArticle(String index){
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
}
