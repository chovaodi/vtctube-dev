package com.vtc.basetube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.vtc.basetube.adapter.MenuLeftAdapter;
import com.vtc.basetube.fragment.FragmentAbout;
import com.vtc.basetube.fragment.FragmentCategory;
import com.vtc.basetube.fragment.FragmentLike;
import com.vtc.basetube.fragment.FragmentSearch;
import com.vtc.basetube.fragment.FragmentViewed;
import com.vtc.basetube.fragment.VideoPlayerFragment;
import com.vtc.basetube.model.Item;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.utils.DatabaseHelper;
import com.vtc.basetube.utils.ICategoryMore;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class MainActivity extends SherlockFragmentActivity implements
		SearchView.OnQueryTextListener, SearchView.OnSuggestionListener,
		OnDisplayVideo, ICategoryMore {
	private DrawerLayout mDrawerLayout;
	private LinearLayout lineLeftMenu;
	private ListView leftMenu;
	private SearchView searchView;

	private ArrayList<Item> listMenuLeft = null;
	private MenuLeftAdapter adapterMenu = null;
	private SuggestionsAdapter mSuggestionsAdapter;
	private static final String[] COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1, };
	private VideoPlayerFragment mVideoPlayerFragment;
	public static ProgressBar progressBar;

	private String currentTag = "TAG_HOME";
	private int idActive;
	public static TextView lblMessage;
	public static DatabaseHelper myDbHelper;
	private String mSearchValue = "";
	private List<String> listQuerySearch;
	private String mTitle = "Trang chủ";

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
		listQuerySearch = Utils.getQuerySearch("SELECT * FROM "
				+ DatabaseHelper.TB_SEARCH);
		setContentView(R.layout.activity_main);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		lblMessage = (TextView) findViewById(R.id.lblThongbao);
		lblMessage.setVisibility(View.GONE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lineLeftMenu = (LinearLayout) findViewById(R.id.lineMenu);
		leftMenu = (ListView) findViewById(R.id.rbm_listview);
		View footer = getLayoutInflater()
				.inflate(R.layout.fotter_detailt, null);
		leftMenu.addFooterView(footer);

		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				if (adapterMenu == null) {
					addMenuLeft(R.menu.ribbon_menu);
				}

				zoomoutPlayer();
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerOpened(View arg0) {
				getSupportActionBar().setTitle(
						getResources().getString(R.string.lblDanhmuc));
			}

			@Override
			public void onDrawerClosed(View arg0) {
				getSupportActionBar().setTitle(mTitle);
				clickMenu();
			}
		});

		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.frame_container,
						FragmentHome.newInstance(QuangNinhTvApplication
								.getInstance()), currentTag).commit();

	}

	public void addMenuLeft(int menu) {
		listMenuLeft = Utils.getMenu(MainActivity.this, menu);
		adapterMenu = new MenuLeftAdapter(this);

		for (int i = 0; i < listMenuLeft.size(); i++) {
			Item item = listMenuLeft.get(i);
			adapterMenu.addSeparatorItem(item);
		}

		leftMenu.setAdapter(adapterMenu);
		leftMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				mTitle = adapterMenu.getItem(pos).getTitle();
				idActive = adapterMenu.getItem(pos).getRegId();
				mDrawerLayout.closeDrawer(lineLeftMenu);
				lblMessage.setVisibility(View.GONE);
			}

		});

	}

	public void clickMenu() {
		getSupportActionBar().setTitle(mTitle);
		switch (idActive) {
		case R.id.right_menu_home:
			setHome();
			break;
		case R.id.right_menu_daxem:
			currentTag = "TAG_VIEWED";
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container,
							FragmentViewed.newInstance(), currentTag).commit();
			break;
		case R.id.right_menu_yeuthich:
			currentTag = "TAG_LIKE";
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container, FragmentLike.newInstance(),
							currentTag).commit();
			break;
		case R.id.right_menu_danhgia:
			Utils.gotoMarket(MainActivity.this);
			break;
		case R.id.right_menu_gioithieu:

			currentTag = "TAG_ABOUT";
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container, FragmentAbout.newInstance(),
							currentTag).commit();
			break;

		}
		idActive = Integer.MAX_VALUE;

	}

	public void setHome() {
		if (!currentTag.equals("TAG_HOME")) {
			currentTag = "TAG_HOME";
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.frame_container,
							FragmentHome.newInstance(QuangNinhTvApplication
									.getInstance()), currentTag).commit();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		boolean drawerOpenLeft = mDrawerLayout.isDrawerOpen(lineLeftMenu);
		if (item.getItemId() == android.R.id.home) {
			if (drawerOpenLeft) {
				mDrawerLayout.closeDrawer(lineLeftMenu);
			} else {
				mDrawerLayout.openDrawer(lineLeftMenu);

			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint(getResources().getString(R.string.lblTimkiem));
		searchView.setOnQueryTextListener(this);
		searchView.setOnSuggestionListener(this);

		if (mSuggestionsAdapter == null) {
			MatrixCursor cursor = new MatrixCursor(COLUMNS);
			for (int i = 0; i < 4; i++) {
				cursor.addRow(new String[] { String.valueOf(i), "Demo" + i });
			}
			mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar()
					.getThemedContext(), cursor);
		}

		searchView.setSuggestionsAdapter(mSuggestionsAdapter);
		menu.add(1, 10000, Menu.NONE,
				getResources().getString(R.string.lblTimkiem))
				.setIcon(isLight ? R.drawable.ic_search : R.drawable.ic_search)
				.setActionView(searchView)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		return super.onCreateOptionsMenu(menu);
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

	@Override
	public boolean onQueryTextSubmit(String query) {
		addFragmentSearch(query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		listQuerySearch = Utils.getQuerySearch("SELECT * FROM "
				+ DatabaseHelper.TB_SEARCH);
		Log.d("listQuerySearch", listQuerySearch.get(0) + " hshsh");
		for (int i = 0; i < listQuerySearch.size(); i++) {

			if (listQuerySearch.get(i).contains(newText)) {
				String valueTmp = listQuerySearch.get(i);
				listQuerySearch.set(i, listQuerySearch.get(0));
				listQuerySearch.set(0, valueTmp);

				break;
			}
		}

		MatrixCursor cursor = new MatrixCursor(COLUMNS);
		for (int i = 0; i < listQuerySearch.size(); i++) {
			cursor.addRow(new String[] { String.valueOf(i),
					listQuerySearch.get(i) });
		}
		mSuggestionsAdapter.changeCursor(cursor);
		mSuggestionsAdapter.notifyDataSetChanged();
		return false;
	}

	@Override
	public boolean onSuggestionSelect(int position) {

		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
		mSearchValue = c.getString(c
				.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

		AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView
				.findViewById(R.id.abs__search_src_text);

		if (searchTextView != null) {
			searchTextView.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			searchTextView.setTypeface(Typeface.DEFAULT);
			searchTextView.setText(mSearchValue);
		}
		addFragmentSearch(mSearchValue);
		return false;
	}

	public void addFragmentSearch(String txtSearch) {
		if (MainActivity.myDbHelper.getCountRow("SELECT * FROM "
				+ DatabaseHelper.TB_SEARCH + " WHERE txtQuery='" + txtSearch
				+ "'") == 0) {
			MainActivity.myDbHelper.insertQuerySearch(txtSearch);
		}

		mSearchValue = txtSearch.replace(" ", "%20");
		if (!currentTag.equals("TAG_SEARCH")) {
			currentTag = "TAG_SEARCH";
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container,
							FragmentSearch.newInstance(mSearchValue),
							currentTag).commit();
		} else {
			FragmentSearch fraSearch = FragmentSearch.newInstance(mSearchValue);
			fraSearch.updateValue(mSearchValue);
		}
	}

	@Override
	public void display(ItemVideo itemvd) {
		if (myDbHelper.getCountRow("SELECT * FROM " + DatabaseHelper.TB_DATA
				+ " WHERE videoId='" + itemvd.getId() + "' and type='"
				+ Utils.VIEWED + "'") == 0) {
			myDbHelper.insertVideoLike(itemvd, Utils.VIEWED);

		}

		displayVideo(itemvd.getId(), itemvd.getPlaylistId());
	}

	private void displayVideo(String videoId, String playlistId) {
		Context context = getApplicationContext();
		if (Utils.hasConnection(context) == false) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.not_available_while_offline),
					Toast.LENGTH_LONG).show();
			return;
		}
		Log.d("VTCTube", "displayVideo: " + mVideoPlayerFragment);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (mVideoPlayerFragment == null) {
			mVideoPlayerFragment = VideoPlayerFragment.newInstance();
			Bundle bundle = new Bundle();
			bundle.putString(Utils.EXTRA_VIDEO_ID, videoId);
			bundle.putString(Utils.EXTRA_PLAYLIST_ID, playlistId);
			mVideoPlayerFragment.setArguments(bundle);
			fragmentTransaction
					.replace(R.id.play_frame_layout, mVideoPlayerFragment,
							"TAG_VIDEO").addToBackStack(null).commit();
			Log.d("VTCTube", "displayVideo: " + mVideoPlayerFragment);
		} else {
			mVideoPlayerFragment.playVideo(videoId);
			mVideoPlayerFragment.displayRelatedVideo(videoId);
			mVideoPlayerFragment.maximize();
		}
	}

	@Override
	public void onBackPressed() {

		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMaximize()) {
			mVideoPlayerFragment.minimize();
			Log.d("minsize", "min");

		} else {
			if (!currentTag.equals("TAG_HOME")) {
				mTitle = getResources().getString(R.string.lblTrangchu);
				getSupportActionBar().setTitle(mTitle);
				lblMessage.setVisibility(View.GONE);
				currentTag = "TAG_HOME";
				getSupportFragmentManager()
						.beginTransaction()
						.replace(
								R.id.frame_container,
								FragmentHome.newInstance(QuangNinhTvApplication
										.getInstance()), currentTag).commit();
			} else {
				finish();
				System.exit(0);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("chovaoid","test");
	}

	public void zoominPlayer() {
		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMinimize()) {
			mVideoPlayerFragment.maximize();
			return;
		}
	}

	public void zoomoutPlayer() {
		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMaximize()) {
			mVideoPlayerFragment.minimize();
			return;
		}

	}

	@Override
	public void viewAll(String playlistId) {
		currentTag = "TAG_CATE";
		Bundle bundle = new Bundle();
		bundle.putCharSequence(Utils.EXTRA_PLAYLIST_ID, playlistId);
		FragmentCategory fragment = FragmentCategory.newInstance();
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, fragment, currentTag).commit();

	}

	@Override
	public BaseTubeApplication getTubeApplication() {
		return QuangNinhTvApplication.getInstance();
	}
}
