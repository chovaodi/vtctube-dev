package com.vtc.vtctube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.MenuDrawer.OnDrawerStateChangeListener;
import net.simonvt.menudrawer.Position;

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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.vtc.vtctube.connectserver.AysnRequestHttp;
import com.vtc.vtctube.connectserver.IResult;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends SherlockFragmentActivity implements
		SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
	private MenuDrawer leftMenu;
	private SuggestionsAdapter mSuggestionsAdapter;
	public static SmoothProgressBar smooth;
	private static final String[] COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1, };

	private ListView listview;
	private View header;
	private List<ItemMeu> listItemMenu;
	private SimpleFacebook mSimpleFacebook = null;

	private GlobalApplication globalApp;
	private TextView lblUserName;
	private TextView lblAccountId;
	private ImageView imgAvata;
	private SearchView searchView;

	private String queryCurent;
	public static ImageLoader imageLoader = null;
	private ResultSearchCallBack callBackSearch;
	public static DatabaseHelper myDbHelper;
	private List<String> listQuerySearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myDbHelper = new DatabaseHelper(MainActivity.this);

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

		listview = (ListView) findViewById(R.id.listView1);
		header = getLayoutInflater().inflate(R.layout.account_layout, null);
		listview.addHeaderView(header);
		lblUserName = (TextView) header.findViewById(R.id.lblName);
		lblAccountId = (TextView) header.findViewById(R.id.lblEmail);
		imgAvata = (ImageView) header.findViewById(R.id.imgAvata);
		leftMenu.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

			@Override
			public void onDrawerStateChange(int oldState, int newState) {
				if (mSimpleFacebook.isLogin()) {
					getProfile();
				}
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

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.icon_menuleft));
		getSupportActionBar().setTitle(" ");
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bgr_tasktop));

		Fragment newFragment = FragmentHome.newInstance(1);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(R.id.container, newFragment).commit();
	}

	public ArrayList<String> getQuerySearch(String sql) {
		Cursor c = myDbHelper.query(DatabaseHelper.TB_LISTVIDEO, null, null,
				null, null, null, null);
		c = myDbHelper.rawQuery(sql);
		ArrayList<String> listAccount = new ArrayList<String>();

		if (c.moveToFirst()) {
			do {
				listAccount.add(c.getString(0));
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
						.getOptions(MainActivity.this));
			}
			lblUserName.setText(globalApp.getAccountModel().getUserName());

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);

	}

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

		menu.add("Search")
				.setIcon(
						isLight ? R.drawable.icon_search
								: R.drawable.icon_search)
				.setActionView(searchView)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		return true;
	}

	public class ResultSearchCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				int count_total = jsonObj.getInt("count_total");
				if (status.equals("ok") & count_total > 0) {
					String sqlCheck = "SELECT * FROM "
							+ DatabaseHelper.TB_QUERY_SEARCH + " WHERE query='"
							+ queryCurent + "'";

					if (myDbHelper.getCountRow(DatabaseHelper.TB_QUERY_SEARCH,
							sqlCheck) == 0) {
						myDbHelper.insertQuerySearch(queryCurent);
					}
					Intent intent = new Intent(MainActivity.this,
							SearchResultActivity.class);
					intent.putExtra("json", result);
					intent.putExtra("keyword", queryCurent);
					startActivity(intent);
				} else {
					Toast.makeText(MainActivity.this,
							"Không tìm thấy nội dụng này", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				Toast.makeText(MainActivity.this,
						"Không tìm thấy nội dụng này", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			leftMenu.toggleMenu();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		Log.d("onSuggestionSelect", "onSuggestionSelect");
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

		actionSearch(queryCurent);
		return false;
	}

	public void actionSearch(String query) {
		String url = Utils.host + "get_search_results?search=" + query;
		new AysnRequestHttp(1, smooth, callBackSearch).execute(url);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		queryCurent = query;
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
