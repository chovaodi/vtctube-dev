package com.vtc.basetube.model;

public class ItemVideo {
	private String title;
	private String id;
	private String uploader;
	private String countView;
	private String time;
	private String mThumbnail;

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

	public String getCountView() {
		return countView;
	}

	public void setCountView(String countView) {
		this.countView = countView;
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
}
