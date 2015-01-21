package com.vtc.basetube.utils;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.vtc.basetube.model.Item;
import com.vtc.basetube.model.ItemVideo;

public class Utils {
	public static final String TAG = "BASE_TUBE";
	public final static String DEVELOPER_KEY_YOUTUBE = "AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";// "AIzaSyA49SV21QaIN0oj9iUqW-u4zWi-41NDFNo";

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
}
