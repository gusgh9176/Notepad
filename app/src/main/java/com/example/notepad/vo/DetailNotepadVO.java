package com.example.notepad.vo;

import java.io.Serializable;

public class DetailNotepadVO implements Serializable {
    private int notepadNo;
    private String titleStr;
    private String description;
    private boolean delete = false;

    public DetailNotepadVO(int notepadNo, String titleStr, String description) {
        this.notepadNo = notepadNo;
        this.titleStr = titleStr;
        this.description = description;
    }

    public int getNotepadNo() {
        return notepadNo;
    }

    public void setNotepadNo(int notepadNo) {
        this.notepadNo = notepadNo;
    }

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
