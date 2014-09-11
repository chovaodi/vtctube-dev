package com.vtc.vtctube.category;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemCategory;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.IResultOnclick;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentCategory extends SherlockFragment implements
		OnRefreshListener, OnScrollListener {
	String mNum;
	private View v;
	List<ItemCategory> listData = null;
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
	private int tabIndex = PinnedAdapter.MOINHAT;

	private String cate;
	private String queryLoadVideo;
	private String queryLikeVideo;

	private boolean isLoadding = false;
	private boolean isLoadLocal = true;

	private List<ItemPost> listViewNew = new ArrayList<ItemPost>();;
	private ResultCallBack callBack = new ResultCallBack();
	private List<ItemPost> listVideoLike = new ArrayList<ItemPost>();
	private ResultOnclikTab callBackOnlick;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static FragmentCategory newInstance(String num, String title) {
		FragmentCategory f = new FragmentCategory();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString("num", num);
		args.putString("title", title);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNum = (String) (getArguments() != null ? getArguments().getString("num") : 1);
		callBack = new ResultCallBack();
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.category_layout, container, false);
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE cateId='" + mNum + "'";

		listvideo = (ListView) v.findViewById(R.id.listvideo);
		header = getActivity().getLayoutInflater().inflate(
				R.layout.header_cate, null);
		fotter = getActivity().getLayoutInflater().inflate(
				R.layout.fotter_loadmore, null);

		listvideo.addHeaderView(header);

		listvideo.setOnScrollListener(this);
		pager = (ViewPager) header.findViewById(R.id.pager);
		SliderTopFragmentAdapter adapterPg = new SliderTopFragmentAdapter(
				getActivity().getSupportFragmentManager());

		pager.setAdapter(adapterPg);

		PageIndicator mIndicator = (PageIndicator) header
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);

		((PinnedSectionListView) listvideo).setShadowVisible(false);

		callBackOnlick = new ResultOnclikTab();

		adapter = new PinnedAdapter(getActivity(),

		callBackOnlick);

		mPullToRefreshLayout = new PullToRefreshLayout(getActivity());

		ActionBarPullToRefresh.from(getActivity()).insertLayoutInto(container)
				.theseChildrenArePullable(R.id.listvideo, android.R.id.empty)
				.listener(this).setup(mPullToRefreshLayout);

		queryLoadVideo = "SELECT * FROM tblListVideo where cateId='" + cate
				+ "'";
		countDataLocal = MainActivity.myDbHelper.getCountRow(
				DatabaseHelper.TB_LISTVIDEO, queryLoadVideo);
		if (countDataLocal > 0) {
			isLoadLocal = true;
			listViewNew = Utils.getVideoLocal(queryLoadVideo, tabIndex);
			if (listViewNew != null && listViewNew.size() > 0) {
				pageCount = listViewNew.get(0).getPageCount();
				listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				addViewPost(false, listViewNew, PinnedAdapter.MOINHAT);
			}
		} else {
			isLoadLocal = false;
			String url = "http://vtctube.vn/api/get_posts?count=5&page=1&cat="
					+ cate;
			new AysnRequestHttp(Utils.LOAD_FIRST_DATA, smooth, callBack)
					.execute(url);
		}
		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

		}
		return false;
	}

	public class ResultOnlickItem implements IResultOnclick {

		@Override
		public void getResult(int type, int postion, boolean isLike) {

		}

	}

	public class ResultOnclikTab implements IResult {

		@Override
		public void getResult(int type, String result) {
			tabIndex = type;

			switch (type) {
			case PinnedAdapter.MOINHAT:
				listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				setViewTab(listViewNew);
				break;
			case PinnedAdapter.XEMNHIEU:

				break;
			case PinnedAdapter.YEUTHICH:
				removeFotter();
				mPullToRefreshLayout.setRefreshComplete();
				listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
				setViewTab(listVideoLike);
				break;
			}

		}

		@Override
		public void pushResutClickItem(int type, int position, boolean isLike) {
			switch (type) {
			case PinnedAdapter.MOINHAT:
				listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
				setViewTab(Utils.checkLikeVideo(listViewNew, listVideoLike));

				break;
			case PinnedAdapter.XEMNHIEU:

				break;
			case PinnedAdapter.YEUTHICH:
				adapter.clear();
				adapter.notifyDataSetChanged();
				listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);

				ItemPost section = new ItemPost();
				section.setType(PinnedAdapter.SECTION);
				section.setOption(tabIndex);
				adapter.add(section);
				for (int i = 0; i < listVideoLike.size(); i++) {
					listVideoLike.get(i).setType(PinnedAdapter.ITEM);
					if (i == position) {
						listVideoLike.get(i).setLike(isLike);
					}
					adapter.add(listVideoLike.get(i));
				}
				adapter.notifyDataSetChanged();

				break;
			}

		}

		@Override
		public void onCLickView(int type, String idYoutube) {
			Utils.getVideoView(idYoutube, getActivity());
		}
	}

	public void setViewTab(List<ItemPost> list) {
		adapter.clear();
		adapter.notifyDataSetChanged();

		ItemPost section = new ItemPost();
		section.setType(PinnedAdapter.SECTION);
		section.setOption(tabIndex);
		adapter.add(section);

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
								.getPageCount(), listData.get(i).getIdPost());

		}
		listvideo.setAdapter(adapter);

	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
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
					} else if (tabIndex == PinnedAdapter.MOINHAT) {
						removeFotter();
						listVideoLike = Utils.getVideoLike(queryLikeVideo,
								tabIndex);
						listTmp = Utils.checkLikeVideo(listTmp, listVideoLike);
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

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(int type, String idYoutube) {
			// TODO Auto-generated method stub

		}
	}

	public void removeFotter() {
		if (listvideo.getFooterViewsCount() > 0)
			listvideo.removeFooterView(fotter);
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
