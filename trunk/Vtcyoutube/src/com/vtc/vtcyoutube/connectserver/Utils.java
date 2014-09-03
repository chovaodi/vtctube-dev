package com.vtc.vtcyoutube.connectserver;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vtc.vtcyoutube.R;

public class Utils {
	public final static int LOAD_CATEGORY = 1;

	public static DisplayImageOptions getOptions(Context activity) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.bgr_icon_category)
				.showImageOnFail(R.drawable.bgr_icon_category)
				.showImageOnLoading(R.drawable.bgr_icon_category)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}



}
