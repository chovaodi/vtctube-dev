package com.vtc.vtctube.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.PinnedSectionListView;
import com.vtc.vtctube.category.SliderTopFragmentAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

public class FragmentSearchResult extends SherlockFragment implements
		OnScrollListener {
	private PinnedAdapter adapter;
	private ListView listvideo;
	private View header;
	private View fotter;
	private ViewPager pager;

	private int page = 1;
	private int pageSize = 5;
	private int pageCount = 0;
	public static String json;
	private static FragmentSearchResult frament = null;

	private String keyword;
	private String queryLikeVideo;

	private boolean isLoadding = false;

	private List<ItemPost> listData =null;
	private ResultCallBack callBack = new ResultCallBack();
	private List<ItemPost> listVideoLike = new ArrayList<ItemPost>();

	public void setCate(String json, String keyword) {
		if (!FragmentSearchResult.json.equals(json)) {
			Log.d("chovaodi", "11111111111");
			this.keyword=keyword;
			pageCount = 0;
			FragmentSearchResult.json = json;
			adapter.clear();
			adapter.notifyDataSetChanged();

		}
	}

	public static FragmentSearchResult newInstance(String num, String keyword) {
		if (frament == null)
			frament = new FragmentSearchResult();
		
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString("num", num);
		args.putString("keyword", keyword);
		frament.setArguments(args);

		return frament;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		json = (String) (getArguments() != null ? getArguments().getString(
				"num") : 1);
		keyword = (String) (getArguments() != null ? getArguments().getString(
				"keyword") : 1);
		callBack = new ResultCallBack();
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.category_layout, container, false);
		listData= new ArrayList<ItemPost>();
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		// setContentView(R.layout.category_layout);
		// overridePendingTransition(R.anim.slide_in_bottom,
		// R.anim.slide_out_bottom);
		// Intent intent = getIntent();

		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);
		// getSupportActionBar().setIcon(
		// getResources().getDrawable(R.drawable.logo));
		// getSupportActionBar().setTitle("Tìm kiếm");
		// getSupportActionBar().setBackgroundDrawable(
		// getResources().getDrawable(R.drawable.bgr_tasktop));
		//
		// smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		// smooth.setVisibility(View.GONE);

		listvideo = (ListView) view.findViewById(R.id.listvideo);
		listvideo.setAdapter(null);
		header = getActivity().getLayoutInflater().inflate(
				R.layout.header_cate, null);
		listvideo.addHeaderView(header);
		fotter = getActivity().getLayoutInflater().inflate(
				R.layout.fotter_loadmore, null);

		pager = (ViewPager) header.findViewById(R.id.pager);
		SliderTopFragmentAdapter adapterPg = new SliderTopFragmentAdapter(
				getActivity().getSupportFragmentManager());

		pager.setAdapter(adapterPg);
		((PinnedSectionListView) listvideo).setShadowVisible(false);

		adapter = new PinnedAdapter(getActivity(), callBack);

		callBack.getResult(Utils.LOAD_FIRST_DATA, json);
		listvideo.setOnScrollListener(this);
		return view;
	}

	public void addViewPost() {
		listVideoLike = Utils.getVideoLike(queryLikeVideo, 0);
		listData = Utils.checkLikeVideo(listData, listVideoLike);

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
			Log.d("result", "result" + result);
			isLoadding = false;
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				pageSize = jsonObj.getInt("count");
				if (status.equals("ok")&&pageSize>0) {
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
						item.setUrl(json.getString("thumbnail"));
						
						
						listData.add(item);
						listTmp.add(item);

					}

					if (type == Utils.LOAD_FIRST_DATA) {
						addViewPost();
					} else {
						if (listvideo.getFooterViewsCount() > 0)
							listvideo.removeFooterView(fotter);
						listVideoLike = Utils.getVideoLike(queryLikeVideo, 0);
						listTmp = Utils.checkLikeVideo(listTmp, listVideoLike);
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

		@Override
		public void pushResutClickItem(int type, int position, boolean isLike) {
			adapter.getItem(position).setLike(isLike);
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onCLickView(int type, String idYoutube) {
			Utils.getVideoView(idYoutube, getActivity());

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
				if (listvideo.getFooterViewsCount() == 0)
					listvideo.addFooterView(fotter);

				String url = Utils.host + "get_search_results?search="
						+ keyword + "&count=" + pageSize + "&page=" + page;
				Log.d("urlurl", url);

				new AysnRequestHttp(Utils.LOAD_MORE, MainActivity.smooth,
						callBack).execute(url);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}