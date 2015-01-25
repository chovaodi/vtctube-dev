package com.vtc.basetube.model;

public class Category {
    private String mId;
    private String mTitle;
    private String mThumbnail;
    private String mDescription;
    private int mViewCount;
    private String mPublishAt;
    private String mPlaylistId;
    private String mDuration;

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

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getViewCount() {
        return mViewCount;
    }

    public void setCountView(int viewCount) {
        mViewCount = viewCount;
    }

    public String getPublishAt() {
        return mPublishAt;
    }

    public void setPublishAt(String publishAt) {
        mPublishAt = publishAt;
    }

    public String getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(String playlistId) {
        mPlaylistId = playlistId;
    }
    
    public String getDuration() {
        return mDuration;
    }
    
    public void setDuration(String duration) {
        mDuration = duration;
    }
}
