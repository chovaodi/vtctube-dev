package com.vtc.basetube.model;

public class Category {
    private String mId;
    private String mTitle;
    private String mThumbnail;

    public Category() {
        // TODO Auto-generated constructor stub
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

}
