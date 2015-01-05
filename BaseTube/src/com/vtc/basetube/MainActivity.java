package com.vtc.basetube;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.vtc.basetube.adapter.MenuLeftAdapter;
import com.vtc.basetube.fragment.VideoPlayerFragment;
import com.vtc.basetube.model.Item;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class MainActivity extends SherlockFragmentActivity implements
		SearchView.OnQueryTextListener, SearchView.OnSuggestionListener,
		OnDisplayVideo {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lineLeftMenu = (LinearLayout) findViewById(R.id.lineMenu);
		leftMenu = (ListView) findViewById(R.id.rbm_listview);
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerOpened(View arg0) {
				if (adapterMenu == null) {
					addMenuLeft(R.menu.ribbon_menu);
				}

			}

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub

			}
		});

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.frame_container, FragmentHome.newInstance(),
						"TAG_HOME").commit();

	}

	public void addMenuLeft(int menu) {
		listMenuLeft = Utils.getMenu(MainActivity.this, menu);
		adapterMenu = new MenuLeftAdapter(this);

		for (int i = 0; i < listMenuLeft.size(); i++) {
			Item item = listMenuLeft.get(i);
			adapterMenu.addSeparatorItem(item);
		}

		leftMenu.setAdapter(adapterMenu);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void display() {
		displayVideo();
	}

	private void displayVideo() {
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
			fragmentTransaction
					.replace(R.id.play_frame_layout, mVideoPlayerFragment,
							"TAG_VIDEO").addToBackStack(null).commit();
		} else {
			mVideoPlayerFragment.updateData();
			mVideoPlayerFragment.maximize();
		}
	}

	@Override
	public void onBackPressed() {
		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMaximize()) {
			mVideoPlayerFragment.minimize();
			return;
		}
		finish();
		System.exit(0);
		super.onBackPressed();
	}
}
