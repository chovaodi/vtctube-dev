package com.vtc.vtctube.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.PlayerViewActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemMeu;
import com.vtc.vtctube.model.ItemPost;

public class Utils {
	private static String urlFolder = "/VtcTube/";
	public static String GET_CATE_INDEX = "get_category_index";
	public static String host = "http://vtctube.vn/api/";
	public final static int LOAD_FIRST_DATA = 7;
	public final static int LOAD_MORE = 3;
	public final static int AYSN_LOAD = 2;
	public final static int REFRESH = 4;

	public final static int LOAD_SEARCH = 5;
	public final static int LOAD_NEWVIDEO = 6;
	public final static int LOAD_XEMNHIEU = 7;
	public static ItemPost itemCurrent = null;

	public final static String DEVELOPER_KEY_YOUTUBE = "AIzaSyBOIqSHxSY2pRqPdJaCwjDQ9FBzkNQmXhE";
	public static String ADMOB_ID = "ca-app-pub-8362644350234649/3664611615";
	
	public static String TAG_ABOUT = "TAG_ABOUT";
	public static String TAG_CATE = "TAG_CATE";
	public static String TAG_RESENT = "TAG_RESENT";
	public static String TAG_SEARCH = "TAG_SEARCH";
	public static String TAG_NEWVIDEO = "TAG_NEWVIDEO";
	public static String TAG_XEMNHIEU = "TAG_XEMNHIEU";

	public static String getUrlHttp(String host, String funtionName) {
		return host + funtionName;

	}

	public static int convertDpToPixel(int dps, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		final float scale = metrics.density;
		int pixels = (int) (dps * scale + 0.5f);
		return pixels;
	}

	public static boolean isOnline(Activity activity) {
		if (activity != null) {
			ConnectivityManager connectivity = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null)
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
			}
		}
		return false;

	}

	public static void getVideoView(ItemPost item, Activity activity) {
		itemCurrent=item;
		Intent intent = new Intent(activity, PlayerViewActivity.class);
		activity.startActivity(intent);

		// Intent intent = null;
		// intent = YouTubeStandalonePlayer.createVideoIntent(activity,
		// Utils.DEVELOPER_KEY_YOUTUBE, videoId);
		// if (intent != null) {
		// if (canResolveIntent(intent, activity)) {
		// activity.startActivityForResult(intent, 102);
		// } else {
		// YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(
		// activity, 102).show();
		// }
		// }

	}

	public static void getDialogMessges(Activity activity, String mes) {
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(
				R.drawable.background_card);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.dialognote);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		dialog.getWindow().setLayout(5 * width / 6, LayoutParams.WRAP_CONTENT);

		dialog.show();
		TextView lblMess = (TextView) dialog.findViewById(R.id.lblMessage);
		lblMess.setText(Html.fromHtml(mes));

		Button btnAccept = (Button) dialog.findViewById(R.id.btnAccept);

		btnAccept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});

	}

	private static boolean canResolveIntent(Intent intent, Activity activity) {
		List<ResolveInfo> resolveInfo = activity.getPackageManager()
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
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setLike(false);
			for (int j = 0; j < listVideoLike.size(); j++) {
				if (list.get(i).getIdPost() == listVideoLike.get(j).getIdPost()) {
					list.get(i).setLike(true);
				}
			}
		}

		return list;
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

	public static void disableEnableControls(boolean enable, ViewGroup vg) {
		try {
			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				child.setEnabled(enable);
				if (child instanceof ViewGroup) {
					disableEnableControls(enable, (ViewGroup) child);
				}
			}
		} catch (Exception e) {

		}
	}

	public static DisplayImageOptions getOptions(Context activity, int draw) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(draw).showImageOnFail(draw)
				.showImageOnLoading(draw).resetViewBeforeLoading(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(false)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}

	public static ArrayList<ItemPost> getVideoLike(String sql, int tabIndex) {
		ArrayList<ItemPost> listAccount = null;
		try {
			Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_LIKE,
					null, null, null, null, null, null);
			c = MainActivity.myDbHelper.rawQuery(sql);
			listAccount = new ArrayList<ItemPost>();

			if (c.moveToFirst()) {

				do {
					ItemPost item = new ItemPost();
					item.setIdPost(c.getInt(0));
					item.setCateId(c.getInt(1) + "");
					item.setVideoId(c.getString(2));
					item.setUrl(c.getString(3));
					item.setStatus(c.getString(4));
					item.setTitle(c.getString(5));
					item.setSlug(c.getString(6));
					item.setCountview(c.getInt(7) + "");
					item.setLike(true);
					item.setOption(tabIndex);
					listAccount.add(item);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listAccount;
	}

	public static ItemPost getItemPost(JSONObject json, int pageCount,
			int tabIndex) {
		ItemPost item = new ItemPost();
		try {
			item.setIdPost(json.getInt("id"));
			item.setStatus(json.getString("slug"));
			item.setCateId(MainActivity.currentCate);
			item.setPageCount(pageCount);
			item.setTitle(json.getString("title"));
			item.setStatus(json.getString("status"));
			item.setVideoId(getIdVideo(json.getString("content")));
			item.setUrl(json.getString("thumbnail"));
			item.setOption(tabIndex);
			item.setCountview(json.getJSONObject("custom_fields")
					.getJSONArray("post_views_count").get(0).toString());

		} catch (Exception e) {

		}
		return item;
	}

	public static String getIdVideo(String content) {
		String[] value;
		try {
			value = content.split("data-video_id=");
			String[] data1 = value[1].split(" ");
			return data1[0].replace("\"", "");
		} catch (Exception e) {

		}
		return "";
	}

	public static ArrayList<ItemPost> getVideoLocal(String tableName,
			String sql, int tabidex) {
		Cursor c = MainActivity.myDbHelper.query(tableName, null, null, null,
				null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<ItemPost> listAccount = new ArrayList<ItemPost>();

		if (c.moveToLast()) {

			do {
				ItemPost item = new ItemPost();
				item.setCateId(c.getInt(0) + "");
				item.setTitle(c.getString(1));
				item.setVideoId(c.getString(2));
				item.setUrl(c.getString(3));
				item.setStatus(c.getString(4));
				item.setPageCount(c.getInt(5));
				item.setIdPost(c.getInt(6));
				item.setSlug(c.getString(7));
				item.setCountview(c.getInt(8) + "");
				item.setOption(tabidex);

				listAccount.add(item);
			} while (c.moveToPrevious());
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
