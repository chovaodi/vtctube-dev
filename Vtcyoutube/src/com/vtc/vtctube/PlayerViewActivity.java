/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vtc.vtctube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.RightLikeAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.menu.MenuDrawer;
import com.vtc.vtctube.menu.MenuDrawer.OnDrawerStateChangeListener;
import com.vtc.vtctube.menu.Position;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 */
public class PlayerViewActivity extends YouTubeFailureRecoveryActivity {
	String videoId = "";
	private String title;
	private String countview;
	private String url = "";
	private String slug;
	private String cate;

	private int id;
	private String status;

	private Button btnLienquan;
	private Button btnChitiet;
	private LinearLayout lineChitiet;
	private LinearLayout lineBack;
	private ProgressBar prLoadLike;
	
	private TextView lblYeuthich;
	private TextView lblTitle;
	private TextView lblTaskTitle;
	private TextView lblCountView;
	private TextView lblShare;

	public static ViewGroup mainView;
	private ListView listvideo;
	private WebView webview_fbview;
	private ProgressBar loaddingcmt;
	private YouTubePlayerView youTubeView;
	private YouTubePlayer player;
	private List<ItemPost> listData;
	private MenuDrawer rightMenu;
	private RightLikeAdapter adapter = null;
	private ListView listYeuthich;
	private ResultItemClick callBackOnlick = new ResultItemClick();
	private ItemPost itemActive = null;
	private boolean isLoadding = false;
	private int inPostActive;

	private List<ItemPost> listVideoRanDom = new ArrayList<ItemPost>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = (ViewGroup) getWindow().getDecorView().findViewById(
				android.R.id.content);

		adapter = new RightLikeAdapter(PinnedAdapter.TYPE_VIEW_CATE,
				PlayerViewActivity.this, callBackOnlick);

		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);

		getActionBar().hide();
		rightMenu = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,
				Position.RIGHT);
		rightMenu.setDropShadowColor(Color.parseColor("#503f3f3f"));
		rightMenu.setDropShadowSize(8);
		rightMenu.setAnimationCacheEnabled(true);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		rightMenu.setMenuSize(5 * width / 6);
		rightMenu.setMenuView(R.layout.rightmenu);
		prLoadLike=(ProgressBar)findViewById(R.id.prLoadLike);
		
		setContentView(R.layout.playerview_demo);
		listYeuthich = (ListView) findViewById(R.id.listViewYeuthich);
		listYeuthich.setAdapter(adapter);
		addViewPost();
		getActionBar().setTitle(title);

		btnLienquan = (Button) findViewById(R.id.btnLienquan);
		btnChitiet = (Button) findViewById(R.id.btnChitiet);
		listvideo = (ListView) findViewById(R.id.listvideo);
		lblTitle = (TextView) findViewById(R.id.lblTitle);
		lblTaskTitle = (TextView) findViewById(R.id.lblTaskTitle);
		lblCountView = (TextView) findViewById(R.id.lblLuotxem);
		lblShare = (TextView) findViewById(R.id.btnShareDetailt);
		lblShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MainActivity.callClickShare.onShare(title, url, slug);
			}
		});

		lblYeuthich = (TextView) findViewById(R.id.lblYeuthich);
		lblYeuthich.setSelected(Utils.itemCurrent.isLike());
		lblYeuthich.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!itemActive.isLike()) {
					actionLike();
					itemActive.setLike(true);
					lblYeuthich.setSelected(true);
				} else {
					itemActive.setLike(false);
					MainActivity.myDbHelper.deleteLikeVideo(
							DatabaseHelper.TB_LIKE, itemActive.getIdPost());
					lblYeuthich.setSelected(false);
				}

			}
		});

		ImageButton imgLike = (ImageButton) findViewById(R.id.btnLike);
		imgLike.setSelected(true);
		imgLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rightMenu.toggleMenu();

			}
		});
		lineChitiet = (LinearLayout) findViewById(R.id.lineChitiet);
		lineBack = (LinearLayout) findViewById(R.id.lineBack);
		lineBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(R.anim.slide_in_bottom,
						R.anim.slide_out_bottom);
			}
		});

		lblTitle.setText(title);

		listvideo.setVisibility(View.VISIBLE);
		lineChitiet.setVisibility(View.GONE);

		btnLienquan.setSelected(true);
		btnChitiet.setSelected(false);

		btnLienquan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				btnLienquan.setSelected(true);
				btnChitiet.setSelected(false);
				listvideo.setVisibility(View.VISIBLE);
				lineChitiet.setVisibility(View.GONE);

			}
		});

		btnChitiet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				btnLienquan.setSelected(false);
				btnChitiet.setSelected(true);
				if (btnChitiet.isSelected()) {
					loadComment("http://vtctube.vn/" + itemActive.getSlug());
				}

				listvideo.setVisibility(View.GONE);
				lineChitiet.setVisibility(View.VISIBLE);
			}
		});
		ResultOnclikTab callBackOnlick = new ResultOnclikTab();
		PinnedAdapter adapter = new PinnedAdapter(
				PinnedAdapter.TYPE_VIEW_DETAIL, this, callBackOnlick);

		String queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
				+ MainActivity.currentCate + "' and videoId !='" + videoId
				+ "'";
		listData = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
				queryVideoLocal, 0);

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
		}
		listvideo.setAdapter(adapter);

		loaddingcmt = (ProgressBar) findViewById(R.id.loading);
		webview_fbview = (WebView) findViewById(R.id.contentView);
		settingWebView();
		setDataview(Utils.itemCurrent);
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);

		rightMenu
				.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

					@Override
					public void onDrawerStateChange(int oldState, int newState) {
						if (newState == MenuDrawer.STATE_OPEN) {
							addViewPost();
						}
						if (newState == MenuDrawer.STATE_CLOSED) {
							if (inPostActive != itemActive.getIdPost()
									&& itemActive != null
									&& !itemActive.getVideoId().equals(id)) {
								player.cueVideo(itemActive.getVideoId());
								setDataview(itemActive);
								//itemActive = null;
							}
						}
					}
				});

	}

	public void addViewPost() {

		String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		List<ItemPost> listData = Utils.getVideoLike(sqlLike,
				PinnedAdapter.YEUTHICH);

		if (listData.size() == 0 && listVideoRanDom.size() == 0) {
			String url = Utils.host + "get_posts?count=8";
			ResultCallBack callBack = new ResultCallBack();
			if (!isLoadding) {
				isLoadding = true;
				prLoadLike.setVisibility(View.VISIBLE);
				new AysnRequestHttp(mainView, Utils.LOAD_FIRST_DATA,
						MainActivity.smooth, callBack).execute(url);
			}
		} else if (listData.size() != adapter.getCount()) {
			if (listData.size() > 0 && listVideoRanDom.size() > 0
					&& listData.size() != adapter.getCount()) {
				addViewData(listData);
			}
			if (listData.size() > 0 && listData.size() != adapter.getCount()) {
				addViewData(listData);
			}
			if (listVideoRanDom.size() > 0
					&& listVideoRanDom.size() != adapter.getCount()) {
				addViewData(listVideoRanDom);
			}

		}

	}

	public void addViewData(List<ItemPost> list) {
		adapter.clear();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getStatus().equals("publish")) {
				list.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(list.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			prLoadLike.setVisibility(View.INVISIBLE);
			isLoadding = false;
			Utils.disableEnableControls(true, (ViewGroup) mainView);

			try {
				if (listVideoRanDom == null) {
					listVideoRanDom = new ArrayList<ItemPost>();
				}
				JSONObject jsonObj = new JSONObject(result);
				int count_total = jsonObj.getInt("count_total");
				if (count_total > 0) {
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item = Utils.getItemPost(json, 0, 0);

						listVideoRanDom.add(item);

					}

					addViewData(listVideoRanDom);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			// TODO Auto-generated method stub

		}
	}

	public class ResultItemClick implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			rightMenu.toggleMenu();
			itemActive = item;

		}

	}

	// public void addViewPost() {
	//
	// String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
	// List<ItemPost> listData = Utils.getVideoLike(sqlLike,
	// PinnedAdapter.YEUTHICH);
	// if (listData.size() == adapter.getCount())
	// return;
	// adapter.clear();
	// for (int i = 0; i < listData.size(); i++) {
	// if (listData.get(i).getStatus().equals("publish")) {
	// listData.get(i).setType(PinnedAdapter.ITEM);
	// adapter.add(listData.get(i));
	// }
	// }
	// adapter.notifyDataSetChanged();
	//
	// }

	public void actionLike() {
		String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE id='" + itemActive.getIdPost() + "'";
		if (MainActivity.myDbHelper.getCountRow(DatabaseHelper.TB_LIKE,
				sqlCheck) == 0) {
			MainActivity.myDbHelper.insertVideoLike(id, cate, videoId, url,
					status, title, slug, countview);
		}
		// Utils.getDialogMessges(PlayerViewActivity.this,
		// "Video vừa được thêm vào danh sách yêu thích");

	}

	public void setDataview(ItemPost item) {
		itemActive = item;
		cate = item.getCateId();
		slug = item.getSlug();
		url = item.getUrl();
		title = item.getTitle();
		id = item.getIdPost();
		countview = item.getCountview();
		status = item.getStatus();
		videoId = item.getVideoId();
		inPostActive = id;

		lblTaskTitle.setText(Html.fromHtml(item.getTitle()));
		lblTitle.setText(Html.fromHtml(item.getTitle()));
		lblCountView.setText("Lượt xem: " + item.getCountview());

	}

	public class ResultOnclikTab implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			try {
				if (!item.getVideoId().equals(title)) {
					player.cueVideo(item.getVideoId());
					setDataview(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		this.player = player;
		if (!wasRestored && videoId.length() > 0) {
			player.cueVideo(videoId);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onBackPressed() {
		final int left = rightMenu.getDrawerState();
		if (left == MenuDrawer.STATE_OPEN || left == MenuDrawer.STATE_OPENING) {
			rightMenu.closeMenu();
			return;
		}
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);

	}

	private void settingWebView() {
		webview_fbview.getSettings().setJavaScriptEnabled(true);
		webview_fbview.setLongClickable(false);
		webview_fbview.getSettings().setBuiltInZoomControls(false);
		webview_fbview.getSettings().setLoadWithOverviewMode(true);
		webview_fbview.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);

		webview_fbview.getSettings().setUseWideViewPort(true);
		webview_fbview.requestFocus(View.FOCUS_DOWN);
		webview_fbview.setPadding(0, 0, 0, 0);
		webview_fbview.setWebViewClient(new webViewClient());
		webview_fbview.setWebChromeClient(new webChromeClient());
		webview_fbview.setInitialScale(100);
		webview_fbview.clearCache(true);
		webview_fbview.clearHistory();
		webview_fbview.getSettings().setDefaultFontSize(14);
		webview_fbview.addJavascriptInterface(new JavaScriptInterface(
				PlayerViewActivity.this), "Android");

		webview_fbview.setVisibility(View.VISIBLE);
		webview_fbview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100
						&& loaddingcmt.getVisibility() == ProgressBar.GONE) {
					loaddingcmt.setVisibility(ProgressBar.VISIBLE);
				}
				// Pbar.setProgress(progress);
				if (progress == 100) {
					loaddingcmt.setVisibility(ProgressBar.GONE);
				}
			}
		});
	}

	public class JavaScriptInterface {
		Context mContext;

		// Instantiate the interface and set the context
		JavaScriptInterface(Context c) {
			mContext = c;
		}

		// using Javascript to call the finish activity
		public void closeMyActivity() {
			// finish();
		}
	}

	private void loadComment(String url) {
		String script = "<div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&appId=648492845199272&version=v2.0\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>";

		String commentBox = "<div class=\"fb-comments\" data-href=\"" + url
				+ "\" data-numposts=\"30\" data-colorscheme=\"light\"></div>";

		String html = "<html><head><style type='text/css'>img { max-width: 100%%; width: auto; height: auto; } p { text-align: justify; width: auto; } </style></head><body style=\"margin: 0; padding: 0\">"
				+ script + commentBox + "</body></html>";

		webview_fbview.loadDataWithBaseURL("http://9gag.tv", html, "text/html",
				null, null);
	}

	private class webChromeClient extends WebChromeClient {

		// display alert message in Web View
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			new android.app.AlertDialog.Builder(view.getContext())
					.setMessage(message).setCancelable(true).show();
			result.confirm();
			return true;
		}
	}

	private class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			webview_fbview.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			int pos = url.indexOf("code=");
			if (pos > 0) {
				// webView.loadDataWithBaseURL("", WEB_DATA_LOADING,
				// "text/html",
				// "UTF-8", "");
				// String tmp = url.split("&")[0];
				// String code = tmp.substring(pos + 5);

			} else {
				try {
					// dialogLoading.dismiss();
				} catch (Exception exception) {
				}
			}
			// loaddingcmt.setVisibility(View.GONE);

			super.onPageFinished(view, url);
		}
	}
}
