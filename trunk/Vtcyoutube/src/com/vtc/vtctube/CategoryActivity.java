package com.vtc.vtctube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.content.ClipData.Item;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.vtc.vtctube.connectserver.AysnRequestHttp;
import com.vtc.vtctube.connectserver.IResult;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CategoryActivity extends SherlockFragmentActivity implements
		OnRefreshListener, OnScrollListener {

	private View header;
	private View fotter;

	private ViewPager pager;
	private PinnedAdapter adapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView listvideo;

	public static SmoothProgressBar smooth;

	private int page = 1;
	private int pageSize = 5;
	private int pageCount = 0;
	private int countDataLocal;
	private int tabIndex = 1;

	private String cate;
	private String queryLoadVideo;

	private boolean isLoadding = false;
	private boolean isLoadLocal = true;

	private List<ItemPost> listViewNew = new ArrayList<ItemPost>();;
	private ResultCallBack callBack = new ResultCallBack();
	private List<ItemPost> listVideoLike = new ArrayList<>();
	private ResultOnclik callBackOnlick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.category_layout);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
		Intent intent = getIntent();
		cate = intent.getStringExtra("cate");
		String title = intent.getStringExtra("title");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.logo));
		getSupportActionBar().setTitle(title);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bgr_tasktop));

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		listvideo = (ListView) findViewById(R.id.listvideo);
		header = getLayoutInflater().inflate(R.layout.header_cate, null);
		fotter = getLayoutInflater().inflate(R.layout.fotter_loadmore, null);

		listvideo.addHeaderView(header);

		listvideo.setOnScrollListener(this);
		pager = (ViewPager) header.findViewById(R.id.pager);
		SliderTopFragmentAdapter adapterPg = new SliderTopFragmentAdapter(
				getSupportFragmentManager());

		pager.setAdapter(adapterPg);

		PageIndicator mIndicator = (PageIndicator) header
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);

		((PinnedSectionListView) listvideo).setShadowVisible(false);

		callBackOnlick = new ResultOnclik();

		adapter = new PinnedAdapter(CategoryActivity.this,

		callBackOnlick);

		final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
				.findViewById(android.R.id.content)).getChildAt(0);
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh
				.from(this)
				// We need to insert the PullToRefreshLayout into the Fragment's
				// ViewGroup
				.insertLayoutInto(viewGroup)
				// Here we mark just the ListView and it's Empty View as
				// pullable
				.theseChildrenArePullable(R.id.listvideo, android.R.id.empty)
				.listener(this).setup(mPullToRefreshLayout);
		queryLoadVideo = "SELECT * FROM tblListVideo where cateId='" + cate
				+ "'";
		countDataLocal = MainActivity.myDbHelper.getCountRow(
				DatabaseHelper.TB_LISTVIDEO, queryLoadVideo);
		if (countDataLocal > 0) {
			isLoadLocal = true;
			listViewNew = getVideoLocal(queryLoadVideo);
			if (listViewNew != null && listViewNew.size() > 0) {
				addViewPost(false, listViewNew, PinnedAdapter.MOINHAT);
			}
		} else {
			isLoadLocal = false;
			String url = "http://vtctube.vn/api/get_posts?count=5&page=1&cat="
					+ cate;
			new AysnRequestHttp(Utils.LOAD_FIRST_DATA, smooth, callBack)
					.execute(url);
		}
	}

	public ArrayList<ItemPost> getVideoLike(String sql) {
		Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_LIKE, null,
				null, null, null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<ItemPost> listAccount = new ArrayList<ItemPost>();

		if (c.moveToFirst()) {

			do {
				ItemPost item = new ItemPost();
				item.setIdPost(c.getInt(0));
				item.setCateId(c.getInt(1) + "");
				item.setVideoId(c.getString(2));
				item.setUrl(c.getString(3));
				item.setStatus(c.getString(4));
				item.setTitle(c.getString(5));
				listAccount.add(item);
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public ArrayList<ItemPost> getVideoLocal(String sql) {
		Cursor c = MainActivity.myDbHelper.query(DatabaseHelper.TB_LISTVIDEO,
				null, null, null, null, null, null);
		c = MainActivity.myDbHelper.rawQuery(sql);
		ArrayList<ItemPost> listAccount = new ArrayList<ItemPost>();

		if (c.moveToFirst()) {

			do {
				ItemPost item = new ItemPost();
				item.setCateId(c.getInt(0) + "");
				item.setTitle(c.getString(1));
				item.setVideoId(c.getString(2));
				item.setUrl(c.getString(3));
				item.setStatus(c.getString(4));
				pageCount = c.getInt(5);
				item.setIdPost(c.getInt(6));

				listAccount.add(item);
			} while (c.moveToNext());
		}
		return listAccount;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_in_bottom,
					R.anim.slide_out_bottom);
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
	}

	public class ResultOnclik implements IResult {

		@Override
		public void getResult(int type, String result) {
			tabIndex = type;

			switch (type) {
			case PinnedAdapter.MOINHAT:
				updateView(listViewNew);

				break;
			case PinnedAdapter.XEMNHIEU:

				break;
			case PinnedAdapter.YEUTHICH:
				listVideoLike = getVideoLike("SELECT * FROM "
						+ DatabaseHelper.TB_LIKE);
				updateView(listVideoLike);

				break;

			}

			// listData = new ArrayList<ItemPost>();
		}
	}

	public void updateView(List<ItemPost> list) {
		if (listViewNew != null & listViewNew.size() > 0) {
			for (int i = 0; i < listViewNew.size(); i++) {
				adapter.remove(listViewNew.get(i));
			}
		}
		if (listVideoLike != null & listVideoLike.size() > 0) {
			for (int i = 0; i < listVideoLike.size(); i++) {
				adapter.remove(listVideoLike.get(i));
			}
		}

		adapter.notifyDataSetChanged();

		adapter.getItem(0).setOption(tabIndex);

		for (int i = 0; i < list.size(); i++) {
			list.get(i).setType(PinnedAdapter.ITEM);
			adapter.add(list.get(i));
		}
		adapter.notifyDataSetChanged();

	}

	public void addViewPost(boolean isClearCache, List<ItemPost> listData,
			int currentTab) {

		if (isClearCache)
			MainActivity.myDbHelper.deleteAccount(DatabaseHelper.TB_LISTVIDEO,
					cate);
		ItemPost section = new ItemPost();
		section.setType(PinnedAdapter.SECTION);
		section.setOption(currentTab);
		adapter.add(section);

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
			if (isClearCache)
				MainActivity.myDbHelper.insertListVideo(
						DatabaseHelper.TB_LISTVIDEO, listData.get(i)
								.getCateId(), listData.get(i).getTitle(),
						listData.get(i).getVideoId(), listData.get(i).getUrl(),
						listData.get(i).getStatus(), listData.get(i)
								.getPageCount());

		}
		listvideo.setAdapter(adapter);
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result", result);
			isLoadding = false;
			if (type == Utils.REFRESH) {
				for (int i = 0; i < listViewNew.size(); i++) {
					adapter.remove(listViewNew.get(i));
				}
				adapter.notifyDataSetChanged();
				listViewNew = new ArrayList<ItemPost>();
				mPullToRefreshLayout.setRefreshComplete();
			}
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				int count_total = jsonObj.getInt("count_total");
				if (status.equals("ok") & count_total > 0) {
					List<ItemPost> listTmp = new ArrayList<ItemPost>();
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item.setIdPost(json.getInt("id"));
						item.setCateId(cate);
						item.setPageCount(pageCount);
						item.setTitle(json.getString("title"));
						item.setStatus(json.getString("status"));
						item.setVideoId(getIdVideo(json.getString("content")));
						JSONArray jsonAttachments = json
								.getJSONArray("attachments");
						JSONObject jsonImg1 = (JSONObject) jsonAttachments
								.get(0);
						JSONObject jsonImg = jsonImg1.getJSONObject("images");
						JSONObject jsonImgFull = jsonImg.getJSONObject("full");
						item.setUrl(jsonImgFull.getString("url"));
						listViewNew.add(item);
						listTmp.add(item);

					}

					if (type == Utils.LOAD_FIRST_DATA) {
						addViewPost(true, listViewNew, PinnedAdapter.MOINHAT);
					} else {
						if (listvideo.getFooterViewsCount() > 0)
							listvideo.removeFooterView(fotter);
						for (int i = 0; i < listTmp.size(); i++) {
							if (listViewNew.get(i).getStatus()
									.equals("publish")) {
								listTmp.get(i).setType(PinnedAdapter.ITEM);
								adapter.add(listTmp.get(i));
							}
						}
						adapter.notifyDataSetChanged();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getIdVideo(String content) {
		String[] value;
		try {
			value = content.split("data-video_id=");
			String[] data1 = value[1].split(" ");
			return data1[0].replace("\"", "");
		} catch (Exception e) {

		}
		return "";
	}

	@Override
	public void onRefreshStarted(View view) {
		// Hide the list

		/**
		 * Simulate Refresh with 4 seconds sleep
		 */
		if (isLoadLocal) {
			String url = "http://vtctube.vn/api/get_posts?count=5&page=1&cat="
					+ cate;

			new AysnRequestHttp(Utils.REFRESH, smooth, callBack).execute(url);

		} else {
			mPullToRefreshLayout.setRefreshComplete();
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount)) {
			page = 1 + (listViewNew.size() / pageSize);
			Log.d("page", page + " " + listViewNew.size());
			if (page >= pageCount)
				isLoadding = true;

			if (!isLoadding & tabIndex == PinnedAdapter.MOINHAT) {
				isLoadding = true;
				if (listvideo.getFooterViewsCount() == 0)
					listvideo.addFooterView(fotter);

				String url = "http://vtctube.vn/api/get_posts?count=5&page="
						+ page + "&cat=" + cate;
				int keyOption = Utils.LOAD_MORE;

				new AysnRequestHttp(keyOption, smooth, callBack).execute(url);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
