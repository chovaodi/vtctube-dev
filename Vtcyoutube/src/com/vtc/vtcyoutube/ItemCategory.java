package com.vtc.vtcyoutube;

public class ItemCategory {
	private int icon;
	private String title;
	private String urlThumnail;
	private String idCategory;
	private String slug;
	private int postcount;
	private int idPost;

	public int getIdPost() {
		return idPost;
	}

	public void setIdPost(int idPost) {
		this.idPost = idPost;
	}

	public int getPostcount() {
		return postcount;
	}

	public void setPostcount(int postcount) {
		this.postcount = postcount;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getSlug() {
		return slug;
	}

	public String getIdCategory() {
		return idCategory;
	}

	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	public void setUrlThumnail(String urlThumnail) {
		this.urlThumnail = urlThumnail;
	}

	public String getUrlThumnail() {
		return urlThumnail;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
