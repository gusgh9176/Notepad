package com.example.notepad.vo;

import android.graphics.drawable.Drawable;

public class ListViewNotepadVO {
    private Drawable iconDrawable ; // 미리보기 아이콘
    private String index; // 메모의 인덱스
    private String titleStr ; // 제목
    private String descStr ; // 본문의 일부분

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getDescStr() {
        return descStr;
    }

    public void setDescStr(String descStr) {
        this.descStr = descStr;
    }
}
