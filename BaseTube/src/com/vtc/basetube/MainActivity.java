package com.vtc.basetube;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vtc.basetube.adapter.MenuLeftAdapter;
import com.vtc.basetube.model.Item;
import com.vtc.basetube.utils.Utils;

public class MainActivity extends SherlockFragmentActivity {
	private DrawerLayout mDrawerLayout;
	private LinearLayout lineLeftMenu;
	private ListView leftMenu;

	private ArrayList<Item> listMenuLeft = null;
	private MenuLeftAdapter adapterMenu = null;

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

		getSupportFragmentManager().beginTransaction().replace(
				R.id.frame_container, FragmentHome.newInstance(), "TAG_HOME").commit();

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
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

}
