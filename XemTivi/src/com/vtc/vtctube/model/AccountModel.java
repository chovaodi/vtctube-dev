package com.vtc.vtctube.model;

public class AccountModel {

	private String userID;
	private String userName;
	private String email;
	private String urlPhoto;
	private int type;

	public static int LOGIN_GOOGLE = 1;
	public static int LOGIN_FACE = 2;

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrlPhoto() {
		return urlPhoto;
	}

	public void setUrlPhoto(String urlPhoto) {
		this.urlPhoto = urlPhoto;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
