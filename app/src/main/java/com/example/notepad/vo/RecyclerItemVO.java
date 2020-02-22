package com.example.notepad.vo;

import android.graphics.drawable.Drawable;

public class RecyclerItemVO {
    private Drawable iconDrawable;
    private String index;
    private String titleStr;
    private String descStr;

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
