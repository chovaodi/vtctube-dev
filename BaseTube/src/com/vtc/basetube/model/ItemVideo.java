package com.vtc.basetube.model;

public class ItemVideo {
	private String title;
	private String id;
	private String uploader;
	private String viewCount;
	private String time;
	private String mThumbnail;
	private int type;
	private String playlistId;
	private String description;
	private String mPublishAt;
	private String mDuration;
	private String mCategoryName = "";

	public void setmCategoryName(String mCategoryName) {
		this.mCategoryName = mCategoryName;
	}

	public String getmCategoryName() {
		return mCategoryName;
	}

	private boolean isCate = false;

	public void setCate(boolean isCate) {
		this.isCate = isCate;
	}

	public boolean isCate() {
		return isCate;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public String getViewCount() {
		return viewCount;
	}

	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getThumbnail() {
		return mThumbnail;
	}

	public void setThumbnail(String thumbnail) {
		mThumbnail = thumbnail;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public String getPlaylistId() {
		return this.playlistId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPublishAt() {
		return mPublishAt;
	}

	public void setPublishAt(String publishAt) {
		mPublishAt = publishAt;
	}

	public String getDuration() {
		return mDuration;
	}

	public void setDuration(String duration) {
		mDuration = duration;
	}
}
