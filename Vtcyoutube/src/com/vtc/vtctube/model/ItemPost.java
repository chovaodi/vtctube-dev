package com.vtc.vtctube.model;

import com.vtc.vtctube.category.PinnedAdapter;

public class ItemPost {
	private String title;
	private String id;
	private String url;
	private String content;
	private String cateId;
	private String videoId;
	private String status;
	private int pageCount;
	private String countview;
	private int option = PinnedAdapter.MOINHAT;
	private String slug;
	private boolean isLike = false;

	public String getCountview() {
		return countview;
	}
	
	public void setCountview(String countview) {
		this.countview = countview;
	}
	
	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getSlug() {
		return slug;
	}

	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public int getOption() {
		return option;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int type;
	private int idPost;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setCateId(String cateId) {
		this.cateId = cateId;
	}

	public String getCateId() {
		return cateId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setIdPost(int idPost) {
		this.idPost = idPost;
	}

	public int getIdPost() {
		return idPost;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
