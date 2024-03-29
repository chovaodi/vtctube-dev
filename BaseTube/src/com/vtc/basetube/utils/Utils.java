package com.vtc.basetube.utils;

import java.io.IOException;
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
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.vtc.basetube.R;
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
	public static final String EXTRA_QUERRY = "querry";

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

	public static void CreatePopupMenu(final Context context, View v,
			final ItemVideo itemvd) {
		final DatabaseHelper myDbHelper = new DatabaseHelper(context);
		try {
			myDbHelper.createDataBase();
			myDbHelper.openDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			throw sqle;
		}
		PopupMenu mypopupmenu = new PopupMenu(context, v);

		MenuInflater inflater = mypopupmenu.getMenuInflater();

		inflater.inflate(R.menu.popup_menu, mypopupmenu.getMenu());
		mypopupmenu.show();
		mypopupmenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				String title = (String) item.getTitle();
				Log.d("title", title);
				if (title.equalsIgnoreCase("yêu thích")) {
					if (myDbHelper.getCountRow("SELECT * FROM "
							+ DatabaseHelper.TB_DATA + " WHERE videoId='"
							+ itemvd.getId() + "' and type='" + Utils.LIKE
							+ "'") == 0) {
						myDbHelper.insertVideoLike(itemvd, Utils.LIKE);
						Toast.makeText(context, "Thêm vào danh sách yêu thích",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Utils.shareButton(itemvd, context);
				}

				return false;
			}
		});

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
				+ "Quảng Ninh TV Kết nối và lan tỏa" + shareLink;

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

	public static ArrayList<String> getQuerySearch(String sql, Activity activity) {
		final DatabaseHelper myDbHelper = new DatabaseHelper(activity);
		try {
			myDbHelper.createDataBase();
			myDbHelper.openDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			throw sqle;
		}
		Cursor c = myDbHelper.query(DatabaseHelper.TB_SEARCH, null, null, null,
				null, null, null);
		c = myDbHelper.rawQuery(sql);
		ArrayList<String> listAccount = new ArrayList<String>();

		if (c.moveToFirst()) {
			do {
				listAccount.add(c.getString(0).replace("%20", " "));
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public static String getTime(String timeString) {
		try {
			Date date = sUSTimeFormatter.parse(timeString);
			return sVITimeFormatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return timeString;
		}
	}
}
