package com.vtc.vtctube.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.CategoryActivity;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemMeu;
import com.vtc.vtctube.model.ItemPost;

public class Utils {
	private static String urlFolder = "/VtcTube/";
	public static String GET_CATE_INDEX = "get_category_index";
	public static String host = "http://vtctube.vn/api/";
	public final static int LOAD_FIRST_DATA = 1;
	public final static int LOAD_MORE = 3;
	public final static int AYSN_LOAD = 2;
	public final static int REFRESH = 4;
	public final static String DEVELOPER_KEY_YOUTUBE = "AIzaSyBOIqSHxSY2pRqPdJaCwjDQ9FBzkNQmXhE";

	public static String getUrlHttp(String host, String funtionName) {
		return host + funtionName;

	}

	public static void getVideoView(String videoId, Activity activity) {

		Intent intent = null;
		intent = YouTubeStandalonePlayer.createVideoIntent(
				activity, Utils.DEVELOPER_KEY_YOUTUBE, videoId);
		if (intent != null) {
			if (canResolveIntent(intent,activity)) {
				activity.startActivityForResult(intent, 101);
			} else {
				YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(
						activity, 102).show();
			}
		}

	}

	private static boolean canResolveIntent(Intent intent, Activity activity) {
		List<ResolveInfo> resolveInfo =activity.getPackageManager()
				.queryIntentActivities(intent, 0);
		return resolveInfo != null && !resolveInfo.isEmpty();
	}

	
	public static void hideSoftKeyboard(Activity activity) {
		try {
			InputMethodManager inputMethodManager = (InputMethodManager) activity
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<ItemPost> checkLikeVideo(List<ItemPost> list,
			List<ItemPost> listVideoLike) {
		List<ItemPost> listTmp = new ArrayList<ItemPost>();
		listTmp = list;
		for (int i = 0; i < list.size(); i++) {
			listTmp.get(i).setLike(false);
			for (int j = 0; j < listVideoLike.size(); j++) {
				if (list.get(i).getIdPost() == listVideoLike.get(j).getIdPost()) {
					listTmp.get(i).setLike(true);
				}
			}
		}
		return listTmp;
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

	public static DisplayImageOptions getOptions(Context activity) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.img_erorrs)
				.showImageOnFail(R.drawable.img_erorrs)
				.showImageOnLoading(R.drawable.img_erorrs)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}

	public static ArrayList<ItemPost> getVideoLike(String sql, int tabIndex) {
		Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_LIKE, null,
				null, null, null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<ItemPost> listAccount = new ArrayList<ItemPost>();

		if (c.moveToFirst()) {

			do {
				ItemPost item = new ItemPost();
				item.setIdPost(c.getInt(0));
				item.setCateId(c.getInt(1) + "");
				item.setVideoId(c.getString(2));
				item.setUrl(c.getString(3));
				item.setStatus(c.getString(4));
				item.setTitle(c.getString(5));
				item.setLike(true);
				item.setOption(tabIndex);
				listAccount.add(item);
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public static ArrayList<ItemPost> getVideoLocal(String sql, int tabidex) {
		Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_LISTVIDEO,
				null, null, null, null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<ItemPost> listAccount = new ArrayList<ItemPost>();

		if (c.moveToFirst()) {

			do {
				ItemPost item = new ItemPost();
				item.setCateId(c.getInt(0) + "");
				item.setTitle(c.getString(1));
				item.setVideoId(c.getString(2));
				item.setUrl(c.getString(3));
				item.setStatus(c.getString(4));
				item.setPageCount(c.getInt(5));
				item.setIdPost(c.getInt(6));
				item.setOption(tabidex);

				listAccount.add(item);
			} while (c.moveToNext());
		}
		return listAccount;
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
			Log.d("path", Environment.getExternalStorageDirectory() + urlFolder
					+ pathFile);
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
