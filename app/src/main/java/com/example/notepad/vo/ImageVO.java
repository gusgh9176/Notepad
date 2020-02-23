package com.example.notepad.vo;

import java.io.Serializable;

public class ImageVO implements Serializable {
    private String memoKey; // 어느 메모의 이미지인지
    private int imageKind; // 이미지 종류(카메라, 앨범, URL)
    private String imageUrl; // 이미지 경로
    private int order; // 메모 내 몇번 째 이미지인지
    private boolean delete = false; // 삭제 되었는지 maxDescription 개 까지

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
