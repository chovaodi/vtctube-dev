package com.vtc.vtcyoutube;

public class ItemPost {
	private String title;
	private String id;
	private String url;
	private String content;
	public int type;
	private int idPost;

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
