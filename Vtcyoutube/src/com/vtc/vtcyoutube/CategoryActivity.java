package com.vtc.vtcyoutube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.vtc.vtcyoutube.connectserver.AysnRequestHttp;
import com.vtc.vtcyoutube.connectserver.IResult;
import com.vtc.vtcyoutube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CategoryActivity extends SherlockFragmentActivity implements
		OnRefreshListener, OnScrollListener {
	private View header;
	private ViewPager pager;
	private PinnedAdapter adapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView listvideo;
	public static SmoothProgressBar smooth;
	private int page = 1;
	private int pageSize = 1;

	private String cate;

	private boolean isLoadding = false;
	private List<ItemPost> listData = new ArrayList<ItemPost>();;
	private int pageCount = 0;
	ResultCallBack callBack = new ResultCallBack();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
		Intent intent = getIntent();
		String cate = intent.getStringExtra("cate");
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

		ResultOnclik callBackOnlick = new ResultOnclik();

		adapter = new PinnedAdapter(CategoryActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
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

		String url = "http://vtctube.vn/api/get_posts?count=5&page=3&cat="
				+ cate;
		Log.d("url", url);
		new AysnRequestHttp(Utils.LOAD_CATEGORY, smooth, callBack).execute(url);
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

		}
	}

	public void addViewPost() {
		ItemPost section = new ItemPost();
		section.setType(PinnedAdapter.SECTION);
		adapter.add(section);
		for (int i = 0; i < listData.size(); i++) {
			listData.get(i).setType(PinnedAdapter.ITEM);
			adapter.add(listData.get(i));
		}
		listvideo.setAdapter(adapter);
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result", result);
			isLoadding = false;
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				if (status.equals("ok")) {
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						Log.d("1111", "" + i);
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item.setIdPost(json.getInt("id"));

						item.setTitle(json.getString("title"));
						item.setContent(json.getString("content"));
						JSONArray jsonAttachments = json
								.getJSONArray("attachments");
						JSONObject jsonImg1 = (JSONObject) jsonAttachments
								.get(0);
						JSONObject jsonImg = jsonImg1.getJSONObject("images");
						JSONObject jsonImgFull = jsonImg
								.getJSONObject("featured-image");
						item.setUrl(jsonImgFull.getString("url"));
						listData.add(item);
					}
					if (type == Utils.LOAD_CATEGORY) {
						addViewPost();
					} else {
						for (int i = 0; i < listData.size(); i++) {
							listData.get(i).setType(PinnedAdapter.ITEM);
							adapter.add(listData.get(i));
						}
						adapter.notifyDataSetChanged();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		// Hide the list

		/**
		 * Simulate Refresh with 4 seconds sleep
		 */
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();

			}
		}.execute();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount)) {
			page = 1 + listData.size() / pageSize;
			if (page >= pageCount)
				isLoadding = true;

			if (!isLoadding) {
				isLoadding = true;

				String url = "http://vtctube.vn/api/get_posts?count=5&page="
						+ page + "&cat=" + cate;
				new AysnRequestHttp(Utils.LOAD_MORE, smooth, callBack)
						.execute(url);
			}

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
