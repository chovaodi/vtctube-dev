package com.vtc.basetube.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.vtc.basetube.MainActivity;
import com.vtc.basetube.model.Item;
import com.vtc.basetube.model.ItemVideo;

public class Utils {
	public static final String TAG = "BASE_TUBE";
	public final static String DEVELOPER_KEY_YOUTUBE = "AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";// "AIzaSyA49SV21QaIN0oj9iUqW-u4zWi-41NDFNo";
	public static int LIKE = 0;
	public static int VIEWED = 1;
	private static SimpleDateFormat sUSTimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static SimpleDateFormat sVITimeFormatter = new SimpleDateFormat(
			"dd-MM-yyyy");
	public static final String EXTRA_VIDEO_ID = "video_id";
	public static final String EXTRA_PLAYLIST_ID = "playlist_id"; 

	public static ArrayList<Item> getMenu(Activity activity, int menu) {

		ArrayList<Item> menuItems = null;
		try {
			menuItems = new ArrayList<Item>();
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
						Item item = new Item();
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

	public static void gotoMarket(Activity activity) {
		Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			activity.startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("http://play.google.com/store/apps/details?id="
							+ activity.getPackageName())));
		}
	}

	public static void shareButton(ItemVideo item, Context context) {
		String shareLink = "";
		shareLink = "https://www.youtube.com/watch?v=" + item.getId();

		String userEntry = item.getTitle() + "\n"
				+ "Quảng Ninh TV nơi mong đến, chốn tìm về" + shareLink;

		Intent textShareIntent = new Intent(Intent.ACTION_SEND);
		textShareIntent.putExtra(Intent.EXTRA_TEXT, userEntry);
		textShareIntent.setType("text/plain");
		context.startActivity(textShareIntent);
	}

	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	public static ArrayList<String> getQuerySearch(String sql) {
		Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_SEARCH,
				null, null, null, null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<String> listAccount = new ArrayList<String>();

		if (c.moveToFirst()) {
			do {
				listAccount.add(c.getString(0).replace("%20", " "));
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public static ArrayList<ItemVideo> getVideoData(String sql,
			DatabaseHelper myDbHelper) {
		ArrayList<ItemVideo> listAccount = null;
		try {
			Cursor c = myDbHelper.query(DatabaseHelper.TB_DATA, null, null,
					null, null, null, null);
			c = myDbHelper.rawQuery(sql);
			listAccount = new ArrayList<ItemVideo>();

			if (c.moveToFirst()) {

				do {
					ItemVideo item = new ItemVideo();
					item.setId(c.getString(0));
					item.setType(c.getInt(1));
					item.setTitle(c.getString(2));
					item.setThumbnail(c.getString(3));
					item.setDuration(c.getString(4));

					listAccount.add(item);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listAccount;
	}

	public static String getTime(String timeString) {
		try {
			Date date = sUSTimeFormatter.parse(timeString);
			return sVITimeFormatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return timeString;
		}
	}
}
