package com.vtc.vtcyoutube.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vtc.vtcyoutube.ItemMeu;
import com.vtc.vtcyoutube.R;

public class Utils {
	private static String urlFolder = "/VtcTube/";
	public static String GET_CATE_INDEX = "get_category_index";
	public static String host = "http://vtctube.vn/api/";

	public static String getUrlHttp(String host, String funtionName) {
		return host + funtionName;

	}

	public static ArrayList<ItemMeu> getMenu(Activity activity, int menu) {

		ArrayList<ItemMeu> menuItems = null;
		try {
			menuItems = new ArrayList<ItemMeu>();
			XmlResourceParser xpp = activity.getResources().getXml(menu);

			xpp.next();
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {
					String elemName = xpp.getName();
					if (elemName.equals("item")) {
						String textId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"title");
						String iconId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"icon");
						String resId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"id");
						ItemMeu item = new ItemMeu();
						item.setTitle(textId);
						item.setRegId(Integer.valueOf(resId.replace("@", "")));
						item.setIcon(Integer.valueOf(iconId.replace("@", "")));
						menuItems.add(item);

					}
				}
				eventType = xpp.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuItems;

	}

	public final static int LOAD_CATEGORY = 1;
	public final static int AYSN_LOAD = 2;

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

	public static boolean isExistFile(String urlFile) {
		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/VtcTube");
		if (!folder.exists()) {
			return false;
		}

		File myFile = new File(Environment.getExternalStorageDirectory()
				+ urlFolder + urlFile);
		if (!myFile.exists()) {
			return false;
		}

		return true;
	}

	public static void writeJsonFile(String json, boolean isOverwriter,
			String pathFile) {
		try {
			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/VtcTube");
			if (!folder.exists()) {
				folder.mkdir();
			}

			File myFile = new File(Environment.getExternalStorageDirectory()
					+ urlFolder + pathFile);
			Log.d("path",Environment.getExternalStorageDirectory()
					+ urlFolder + pathFile);
			if (!myFile.exists()) {
				myFile.createNewFile();
			}

			FileOutputStream fOut = new FileOutputStream(myFile, isOverwriter);
			PrintWriter printWriter = new PrintWriter(fOut);

			printWriter.println(json);

			printWriter.close();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readJsonFile(String urlFile) {
		String jsonFile = "";
		try {
			File myFile = new File(Environment.getExternalStorageDirectory()
					+ urlFolder + urlFile);
			if (myFile.exists()) {
				Scanner scanner = new Scanner(myFile);
				while (scanner.hasNextLine()) {
					jsonFile = jsonFile + scanner.nextLine();
				}
			}
		} catch (Exception e) {
		}
		return jsonFile;
	}
}
