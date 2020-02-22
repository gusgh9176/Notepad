package com.example.notepad.vo;

import java.io.Serializable;

public class ImageVO implements Serializable {
    private String memoKey;
    private int imageKind;
    private String imageUrl;
    private int order;
    private boolean delete = false;

    public ImageVO(String memoKey, int imageKind, String imageUrl, int order) {
        this.memoKey = memoKey;
        this.imageKind = imageKind;
        this.imageUrl = imageUrl;
        this.order = order;
    }

    public String getMemoKey() {
        return memoKey;
    }

    public void setMemoKey(String memoKey) {
        this.memoKey = memoKey;
    }

    public int getImageKind() {
        return imageKind;
    }

    public void setImageKind(int imageKind) {
        this.imageKind = imageKind;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
