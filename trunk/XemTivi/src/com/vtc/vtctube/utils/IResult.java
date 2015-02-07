package com.vtc.vtctube.utils;

import com.vtc.vtctube.model.ItemPost;

public interface IResult {
	public void getResult(int type, String result);

	public void pushResutClickItem(int type, int postion, boolean isLike);

	public void onCLickView(ItemPost item);
	
}
