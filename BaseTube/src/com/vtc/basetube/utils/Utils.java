package com.vtc.basetube.utils;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vtc.basetube.model.Item;

public class Utils {
	public final static String DEVELOPER_KEY_YOUTUBE = "AIzaSyA49SV21QaIN0oj9iUqW-u4zWi-41NDFNo";

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