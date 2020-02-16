package com.example.notepad.db;

import com.example.notepad.vo.DetailNotepadVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NoteDB {

    private static Map<String, DetailNotepadVO> db = new HashMap<String, DetailNotepadVO>();

    public static void addArticle(String index, DetailNotepadVO DetailNotepadVO){
        db.put(index, DetailNotepadVO);
    }

    public static DetailNotepadVO getArticle(String index){
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
