package com.vtc.vtctube.utils;

public interface IResult {
	public void getResult(int type, String result);

	public void pushResutClickItem(int type, int postion, boolean isLike);

	public void onCLickView(int type, String idYoutube);
	
}
