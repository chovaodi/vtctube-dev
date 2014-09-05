package com.vtc.vtcyoutube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.vtc.vtcyoutube.connectserver.AysnRequestHttp;
import com.vtc.vtcyoutube.connectserver.IResult;
import com.vtc.vtcyoutube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SearchResultActivity extends SherlockFragmentActivity implements
		OnScrollListener {
	private PinnedAdapter adapter;
	private ListView listvideo;

	public static SmoothProgressBar smooth;

	private int page = 1;
	private int pageSize = 5;
	private int pageCount = 0;
	private String json;
	private String keyword;

	private boolean isLoadding = false;

	private List<ItemPost> listData = new ArrayList<ItemPost>();;
	private ResultCallBack callBack = new ResultCallBack();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.category_layout);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
		Intent intent = getIntent();
		json = intent.getStringExtra("json");
		keyword = intent.getStringExtra("keyword");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.logo));
		getSupportActionBar().setTitle("Tìm kiếm");
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bgr_tasktop));

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		listvideo = (ListView) findViewById(R.id.listvideo);

		((PinnedSectionListView) listvideo).setShadowVisible(false);

		ResultOnclik callBackOnlick = new ResultOnclik();

		adapter = new PinnedAdapter(SearchResultActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				callBackOnlick);

		callBack.getResult(Utils.LOAD_FIRST_DATA, json);
		listvideo.setOnScrollListener(this);

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
		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
		}
		listvideo.setAdapter(adapter);
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result","result"+result);
			isLoadding = false;
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				pageSize = jsonObj.getInt("count");
				if (status.equals("ok")) {
					List<ItemPost> listTmp = new ArrayList<ItemPost>();
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item.setIdPost(json.getInt("id"));
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
						listData.add(item);
						listTmp.add(item);

					}

					if (type == Utils.LOAD_FIRST_DATA) {
						addViewPost();
					} else {

						for (int i = 0; i < listTmp.size(); i++) {
							if (listData.get(i).getStatus().equals("publish")) {
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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount)) {
			page = 1 + (listData.size() / pageSize);
			Log.d("page", page + " " + listData.size());
			if (page >= pageCount)
				isLoadding = true;

			if (!isLoadding) {
				isLoadding = true;

				String url = Utils.host + "get_search_results?search="
						+ keyword + "&count=" + pageSize + "&page=" + page;
				Log.d("urlurl", url);

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
