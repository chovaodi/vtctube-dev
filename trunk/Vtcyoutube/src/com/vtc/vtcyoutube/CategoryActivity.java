package com.vtc.vtcyoutube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.vtc.vtcyoutube.connectserver.AysnRequestHttp;
import com.vtc.vtcyoutube.connectserver.IResult;
import com.vtc.vtcyoutube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CategoryActivity extends SherlockFragmentActivity implements
		OnRefreshListener {
	private View header;
	private ViewPager pager;
	private PinnedAdapter adapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView listvideo;
	public static SmoothProgressBar smooth;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);
		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		listvideo = (ListView) findViewById(R.id.listvideo);
		header = getLayoutInflater().inflate(R.layout.header_cate, null);
		listvideo.addHeaderView(header);

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

		ResultCallBack callBack = new ResultCallBack();
		String url = "http://vtctube.vn/api/get_posts?count=5&page=3&cat=6";

		new AysnRequestHttp(Utils.LOAD_CATEGORY, smooth, callBack).execute(url);
	}

	public class ResultOnclik implements IResult {

		@Override
		public void getResult(int type, String result) {

		}
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result", result);
			List<ItemPost> listData = null;
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");

				if (status.equals("ok")) {
					Log.d("status", status);
					ItemPost section = new ItemPost();
					section.setType(PinnedAdapter.SECTION);
					adapter.add(section);

					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					listData = new ArrayList<ItemPost>();
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item.setIdPost(json.getInt("id"));
						item.setTitle(json.getString("title"));
						item.setContent(json.getString("content"));
						JSONArray jsonAttachments = json
								.getJSONArray("attachments");
						JSONObject jsonImg = jsonAttachments.getJSONObject(0)
								.getJSONObject("images");
						JSONObject jsonImgFull = jsonImg
								.getJSONObject("featured-image");
						item.setUrl(jsonImgFull.getString("url"));
						item.setType(PinnedAdapter.ITEM);
						adapter.add(item);
						listData.add(item);
					}

					listvideo.setAdapter(adapter);

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
}
