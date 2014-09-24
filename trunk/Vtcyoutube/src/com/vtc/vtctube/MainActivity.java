package com.vtc.vtctube;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.vtc.vtctube.category.FragmentCategory;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.like.FragmentResent;
import com.vtc.vtctube.menu.MenuDrawer;
import com.vtc.vtctube.menu.MenuDrawer.OnDrawerStateChangeListener;
import com.vtc.vtctube.menu.Position;
import com.vtc.vtctube.model.AccountModel;
import com.vtc.vtctube.model.ItemMeu;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.search.FragmentSearchResult;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IClickCate;
import com.vtc.vtctube.utils.IRShareFeed;
import com.vtc.vtctube.utils.IRclickTocate;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends SherlockFragmentActivity implements
		SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
	public static String currentCate;

	private MenuDrawer leftMenu;
	private SuggestionsAdapter mSuggestionsAdapter;
	private static final String[] COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1, };

	private ListView listview;
	private View header;
	private SimpleFacebook mSimpleFacebook = null;
	private TextView lblUserName;
	private TextView lblAccountId;
	private TextView lblTitle;
	public static TextView lblError;
	private ImageView imgLogo;

	private ImageView imgAvata;
	private SearchView searchView;
	private LinearLayout lineAdmob;
	private AdView adView;

	public static ImageLoader imageLoader = null;
	public static DatabaseHelper myDbHelper;
	public static SmoothProgressBar smooth;

	private ResultSearchCallBack callBackSearch;
	public static ResultCallBackCLick callBackCLick;
	public static ResultCallBackCate callBackCLickCate;
	public static ResultClickShare callClickShare;

	private List<ItemMeu> listItemMenu;
	private List<String> listQuerySearch;
	private String queryCurent = "";

	private GlobalApplication globalApp;

	private boolean isMenuCate = false;
	private FragmentManager fragmentManager;
	private FragmentTransaction ft;
	public static ViewGroup mainView;
	private Random random = new Random();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = (ViewGroup) getWindow().getDecorView().findViewById(
				android.R.id.content);

		myDbHelper = new DatabaseHelper(MainActivity.this);
		callBackCLick = new ResultCallBackCLick();
		callBackCLickCate = new ResultCallBackCate();
		callClickShare = new ResultClickShare();

		fragmentManager = getSupportFragmentManager();

		ft = fragmentManager.beginTransaction();
		try {
			myDbHelper.createDataBase();
			myDbHelper.openDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			throw sqle;
		}
		listQuerySearch = getQuerySearch("SELECT * FROM "
				+ DatabaseHelper.TB_QUERY_SEARCH);
		globalApp = (GlobalApplication) getApplicationContext();

		listItemMenu = Utils.getMenu(MainActivity.this, R.menu.ribbon_menu);
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(MainActivity.this.getApplicationContext()));
		}
		callBackSearch = new ResultSearchCallBack();

		leftMenu = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,
				Position.LEFT);
		leftMenu.setDropShadowColor(Color.parseColor("#503f3f3f"));
		leftMenu.setDropShadowSize(8);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		leftMenu.setMenuSize(2 * width / 3);
		leftMenu.setMenuView(R.layout.leftmenu);

		setContentView(R.layout.fragment_content);
		lineAdmob = (LinearLayout) findViewById(R.id.adview);
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(Utils.ADMOB_ID);
		lineAdmob.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		listview = (ListView) findViewById(R.id.listView1);
		header = getLayoutInflater().inflate(R.layout.account_layout, null);
		listview.addHeaderView(header);

		lblUserName = (TextView) header.findViewById(R.id.lblName);
		lblAccountId = (TextView) header.findViewById(R.id.lblEmail);
		lblError = (TextView) findViewById(R.id.lblError);
		imgAvata = (ImageView) header.findViewById(R.id.imgAvata);

		leftMenu.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

			@Override
			public void onDrawerStateChange(int oldState, int newState) {
				if (mSimpleFacebook.isLogin()) {
					getProfile();
				}
				Utils.hideSoftKeyboard(MainActivity.this);
			}
		});

		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSimpleFacebook.isLogin()) {
					getProfile();
				} else {
					mSimpleFacebook.login(onLoginListener);
				}
			}
		});

		MenuAdapter menuAdapter = new MenuAdapter(MainActivity.this);
		for (int i = 0; i < listItemMenu.size(); i++) {
			menuAdapter.addItem(listItemMenu.get(i));
		}
		listview.setAdapter(menuAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				clickMenu(position);

			}

		});

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.icon_menuleft));
		getSupportActionBar().setTitle("VTCTUBE");
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bgr_tasktop));

		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM
						| ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setCustomView(R.layout.header_task);
		imgLogo = (ImageView) findViewById(R.id.iconHeader);
		lblTitle = (TextView) findViewById(R.id.lblHeaderTile);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		Fragment newFragment = FragmentHome.newInstance(1);
		ft.add(R.id.container, newFragment).commit();
	}

	public void clickMenu(int position) {
		int id = listItemMenu.get(position - 1).getRegId();
		switch (id) {
		case R.id.menu_trangchu:
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.popBackStack();
			FragmentCategory.frament = null;
			MainActivity.callBackCLick.onClick(false, "");
			break;

		case R.id.menu_video_moinhat:
			actionNewvideo();
			break;
		case R.id.menu_video_xemnhieu:
			actionXemnhieu();
			break;

		case R.id.menu_video_yeuthich:
			String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
			if (myDbHelper.getCountRow(DatabaseHelper.TB_LIKE, sqlLike) > 0) {
				addFragmentResent(R.id.menu_video_yeuthich, getResources()
						.getString(R.string.lblmenu_yeuthich));
			} else {
				Utils.getDialogMessges(MainActivity.this, getResources()
						.getString(R.string.lblMsgrong));
			}

			break;
		case R.id.menu_video_daxem:
			String sqlDaxem = "SELECT * FROM " + DatabaseHelper.TB_RESENT;
			if (myDbHelper.getCountRow(DatabaseHelper.TB_RESENT, sqlDaxem) > 0) {
				addFragmentResent(R.id.menu_video_daxem, getResources()
						.getString(R.string.lblmenu_daxem));
			} else {
				Utils.getDialogMessges(MainActivity.this, getResources()
						.getString(R.string.lblMsgrong));
			}

			break;

		}
		leftMenu.toggleMenu();
	}

	public ArrayList<String> getQuerySearch(String sql) {
		Cursor c = myDbHelper.query(DatabaseHelper.TB_LISTVIDEO, null, null,
				null, null, null, null);
		c = myDbHelper.rawQuery(sql);
		ArrayList<String> listAccount = new ArrayList<String>();

		if (c.moveToFirst()) {
			do {
				listAccount.add(c.getString(0).replace("%20", " "));
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public String getLinkAvataFace(String id) {
		return "https://graph.facebook.com/" + id + "/picture?type=normal";
	}

	public void setAccInfo() {
		if (globalApp.getAccountModel() != null) {
			lblAccountId.setText(globalApp.getAccountModel().getUserID());

			if (lblUserName.getText().equals("Đăng nhập")) {
				imageLoader.displayImage(getLinkAvataFace(globalApp
						.getAccountModel().getUserID()), imgAvata, Utils
						.getOptions(MainActivity.this, R.drawable.img_erorrs));
			}
			lblUserName.setText(globalApp.getAccountModel().getUserName());

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		adView.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		adView.pause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != 102)
			mSimpleFacebook.onActivityResult(this, requestCode, resultCode,
					data);

	}

	public class ResultClickShare implements IRShareFeed {

		@Override
		public void onShare(String title, String thumnail, String slug) {
			if (mSimpleFacebook.isLogin()) {
				setPostShare(title, thumnail, slug);
			} else {
				mSimpleFacebook.login(new OnLoginListener() {

					@Override
					public void onFail(String reason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onException(Throwable throwable) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onThinking() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onNotAcceptingPermissions(Type type) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLogin() {
						// TODO Auto-generated method stub

					}
				});
			}

		}
	}

	public void setPostShare(String title, String thumnail, String slug) {

		String shareLink = "http://vtctube.vn/" + slug + "-" + ".html";
		Feed feed = new Feed.Builder().setMessage(title)
				.setName("VTCTube-Xem thỏa thích. Chỉ cần Click")
				.setCaption("").setDescription("Trải nghiệm mới với VTCTube")
				.setPicture(thumnail).setLink(shareLink).build();
		SimpleFacebook.getInstance().publish(feed, true, onPublishListener);
	}

	OnPublishListener onPublishListener = new OnPublishListener() {
		@Override
		public void onComplete(String postId) {
			Toast.makeText(MainActivity.this, "Chia sẽ thành công",
					Toast.LENGTH_LONG).show();
		}

		public void onException(Throwable throwable) {
			Toast.makeText(MainActivity.this, "onException", Toast.LENGTH_LONG)
					.show();
		};

		@Override
		public void onThinking() {

		};

		/*
		 * You can override other methods here: onThinking(), onFail(String
		 * reason), onException(Throwable throwable)
		 */
	};

	OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
		}

		@Override
		public void onException(Throwable throwable) {
		}

		@Override
		public void onThinking() {
		}

		@Override
		public void onLogin() {
			getProfile();

		}

		@Override
		public void onNotAcceptingPermissions(Type type) {

		}
	};

	public void getProfile() {

		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				String user_ID = profile.getId();// user id
				String profileName = profile.getName();// user's
				AccountModel account = new AccountModel();
				account.setUserID(user_ID);
				account.setUserName(profileName);
				globalApp.setAccountModel(account);
				setAccInfo();
			}

			@Override
			public void onThinking() {
			}

			@Override
			public void onFail(String reason) {

				super.onFail(reason);
			}

		});

	}

	public class ResultCallBackCate implements IRclickTocate {

		@Override
		public void getCate(String title, String cate) {
			addFragment(title, cate);
		}

	}

	public void addFragmentSearch(String json, String tag, int keyOption) {
		Utils.hideSoftKeyboard(MainActivity.this);

		MainActivity.callBackCLick.onClick(false, "Tìm kiếm");
		FragmentManager fragmentManager = MainActivity.this
				.getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentSearchResult fragment = null;
		fragment = (FragmentSearchResult) fragmentManager
				.findFragmentByTag(tag);
		if (fragment == null) {
			fragment = FragmentSearchResult.newInstance(json, queryCurent,
					keyOption);
			ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, tag);
		} else {
			FragmentSearchResult fragmentTmp = new FragmentSearchResult();
			fragmentTmp.setCate(json, queryCurent, keyOption);
			ft.show(fragment);
		}

		ft.commit();

	}

	public void addFragmentResent(int id, String title) {
		MainActivity.callBackCLick.onClick(false, title);
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		fragmentManager = getSupportFragmentManager();

		FragmentResent fragment = (FragmentResent) fragmentManager
				.findFragmentByTag(Utils.TAG_RESENT);
		if (fragment == null) {
			fragment = FragmentResent.newInstance(id);
			ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_RESENT);
		} else {
			FragmentResent fResent = new FragmentResent();
			fResent.onResumeData(id);
			ft.show(fragment);
		}

		ft.commit();

	}

	public void addFragment(String title, String cate) {
		MainActivity.callBackCLick.onClick(true, title);

		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentCategory fragment = null;
		fragment = (FragmentCategory) fragmentManager
				.findFragmentByTag(Utils.TAG_CATE);
		if (fragment == null) {
			fragment = FragmentCategory.newInstance(cate, title);
			ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_CATE);
		} else {
			FragmentCategory fragmentTmp = new FragmentCategory();
			fragmentTmp.setCate(cate);
			ft.show(fragment);
		}

		ft.commit();

	}

	public class ResultCallBackCLick implements IClickCate {

		@Override
		public void onClick(boolean isShowTitle, String title) {
			isMenuCate = isShowTitle;
			if (isShowTitle) {
				lblTitle.setVisibility(View.VISIBLE);
				imgLogo.setVisibility(View.GONE);
				lblTitle.setText(title);
			} else {
				lblTitle.setVisibility(View.GONE);
				imgLogo.setVisibility(View.VISIBLE);
			}
			invalidateOptionsMenu();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		// Create the search view
		searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("Tìm kiếm");
		searchView.setOnQueryTextListener(this);
		searchView.setOnSuggestionListener(this);

		if (mSuggestionsAdapter == null) {
			MatrixCursor cursor = new MatrixCursor(COLUMNS);
			for (int i = 0; i < listQuerySearch.size(); i++) {
				cursor.addRow(new String[] { String.valueOf(i),
						listQuerySearch.get(i) });
			}
			mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar()
					.getThemedContext(), cursor);
		}

		searchView.setSuggestionsAdapter(mSuggestionsAdapter);

		menu.add(1, 10000, Menu.NONE, "Search")
				.setIcon(
						isLight ? R.drawable.icon_search
								: R.drawable.icon_search)
				.setActionView(searchView)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		if (isMenuCate) {
			SubMenu subMenu1 = menu.addSubMenu("Danh mục");
			for (int i = 0; i < FragmentHome.listData.size(); i++) {
				subMenu1.add(0, Integer.parseInt(FragmentHome.listData.get(i)
						.getIdCategory()), Menu.NONE, FragmentHome.listData
						.get(i).getTitle());
			}

			MenuItem subMenu1Item = subMenu1.getItem();
			subMenu1Item.setIcon(R.drawable.ic_feedmnu_o);
			subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			leftMenu.toggleMenu();
		} else if (id != 0 && id != 10000) {
			lblTitle.setText(item.getTitle());
			addFragment(item.getTitle().toString(), String.valueOf(id));
		}
		return super.onOptionsItemSelected(item);
	}

	public class ResultSearchCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {

			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				int count_total = jsonObj.getInt("count_total");
				if (status.equals("ok") && count_total > 0) {
					switch (type) {
					case Utils.LOAD_SEARCH:
						String sqlCheck = "SELECT * FROM "
								+ DatabaseHelper.TB_QUERY_SEARCH
								+ " WHERE query='" + queryCurent + "'";

						if (myDbHelper.getCountRow(
								DatabaseHelper.TB_QUERY_SEARCH, sqlCheck) == 0) {
							myDbHelper.insertQuerySearch(queryCurent);
						}
						addFragmentSearch(result, Utils.TAG_SEARCH,
								Utils.LOAD_SEARCH);

						break;
					case Utils.LOAD_NEWVIDEO:

						addFragmentSearch(result, Utils.TAG_NEWVIDEO,
								Utils.LOAD_NEWVIDEO);
						break;
					case Utils.LOAD_XEMNHIEU:

						addFragmentSearch(result, Utils.TAG_XEMNHIEU,
								Utils.LOAD_XEMNHIEU);
						break;
					}

				} else {
					Utils.getDialogMessges(MainActivity.this, getResources()
							.getString(R.string.lblMsgrong));

				}
			} catch (Exception e) {
				e.printStackTrace();
				Utils.getDialogMessges(MainActivity.this, getResources()
						.getString(R.string.lblMsgrong));
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

	@Override
	public void onBackPressed() {
		Fragment myFragment = getSupportFragmentManager().findFragmentByTag(
				Utils.TAG_CATE);
		if (myFragment != null && myFragment.isVisible()) {
			onBackView();
			return;
		}

		Fragment myFragmentSearch = getSupportFragmentManager()
				.findFragmentByTag(Utils.TAG_SEARCH);
		if (myFragmentSearch != null && myFragmentSearch.isVisible()) {
			onBackView();
			return;
		}
		Fragment myFragmentResent = getSupportFragmentManager()
				.findFragmentByTag(Utils.TAG_RESENT);
		if (myFragmentResent != null && myFragmentResent.isVisible()) {
			onBackView();
			return;
		}
		Fragment myFragmentXemnhieu = getSupportFragmentManager()
				.findFragmentByTag(Utils.TAG_XEMNHIEU);
		if (myFragmentXemnhieu != null && myFragmentXemnhieu.isVisible()) {
			onBackView();
			return;
		}

		Fragment myFragmentNew = getSupportFragmentManager().findFragmentByTag(
				Utils.TAG_NEWVIDEO);
		if (myFragmentNew != null && myFragmentNew.isVisible()) {
			onBackView();
			return;
		}
		finish();
		System.exit(0);
	}

	public void onBackView() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStack();
		MainActivity.callBackCLick.onClick(false, "");

	}

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
		queryCurent = c.getString(c
				.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

		AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView
				.findViewById(R.id.abs__search_src_text);

		if (searchTextView != null) {
			searchTextView.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			searchTextView.setTypeface(Typeface.DEFAULT);
			searchTextView.setText(queryCurent);
		}
		queryCurent = queryCurent.replace(" ", "%20");
		actionSearch(queryCurent);
		return false;
	}

	public void actionSearch(String query) {
		String url = Utils.host + "get_search_results?search=" + query;
		Log.d("searchUrl", url);
		new AysnRequestHttp(mainView, Utils.LOAD_SEARCH, smooth, callBackSearch)
				.execute(url);
	}

	public void actionNewvideo() {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dateobj = new Date();
		String currentDateandTime = df.format(dateobj).toString();
		String url = Utils.host + "get_date_posts?date=" + currentDateandTime;
		Log.d("searchUrl", url);
		new AysnRequestHttp(mainView, Utils.LOAD_NEWVIDEO, smooth,
				callBackSearch).execute(url);
	}

	public void actionXemnhieu() {
		currentCate = FragmentHome.listData.get(
				random.nextInt(FragmentHome.listData.size() - 1))
				.getIdCategory();
		String url = Utils.host + "get_posts?count=5&page=1&cat=" + currentCate;

		Log.d("urlXemnhieu", url);

		new AysnRequestHttp((ViewGroup) mainView, Utils.LOAD_XEMNHIEU,
				MainActivity.smooth, callBackSearch).execute(url);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		queryCurent = query.replace(" ", "%20");
		actionSearch(queryCurent);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		List<String> listTmp = new ArrayList<String>();
		for (int i = 0; i < listQuerySearch.size(); i++) {
			if (listQuerySearch.get(i).contains(newText)) {
				listTmp.add(listQuerySearch.get(i));
			}
		}

		MatrixCursor cursor = new MatrixCursor(COLUMNS);
		for (int i = 0; i < listTmp.size(); i++) {
			cursor.addRow(new String[] { String.valueOf(i), listTmp.get(i) });
		}
		mSuggestionsAdapter.changeCursor(cursor);
		mSuggestionsAdapter.notifyDataSetChanged();

		return false;
	}

	private class SuggestionsAdapter extends CursorAdapter {

		public SuggestionsAdapter(Context context, Cursor c) {
			super(context, c, 0);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv = (TextView) view;
			final int textIndex = cursor
					.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
			tv.setText(cursor.getString(textIndex));
		}
	}
}
