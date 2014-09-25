package com.vtc.vtctube.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
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

public class FragmentCategory extends SherlockFragment implements
		OnRefreshListener, OnScrollListener {
	private View v;
	List<ItemCategory> listData = null;
	private View header;
	private View fotter;

	private ViewPager pager;
	public static PinnedAdapter adapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	public static ListView listvideo;

	private int page = 1;
	private int pageSize = 5;
	private int pageCount = 0;
	private int countDataLocal;
	private int tabIndex = PinnedAdapter.MOINHAT;

	private String queryVideoLocal;
	protected String queryLikeVideo;

	private boolean isLoadding = false;
	private boolean isLoadLocal = true;

	private List<ItemPost> listViewNew = new ArrayList<ItemPost>();
	private ResultCallBack callBack = null;
	private List<ItemPost> listVideoLike = new ArrayList<ItemPost>();
	private ResultOnclikTab callBackOnlick = null;
	public static FragmentCategory frament = null;
	private Random random = new Random();
	private List<ItemPost> listVideoXemnhieu=null;
	public FragmentCategory() {
		if (callBack == null)
			callBack = new ResultCallBack();

		if (callBackOnlick == null)
			callBackOnlick = new ResultOnclikTab();
		listVideoLike = new ArrayList<ItemPost>();
		listVideoXemnhieu = new ArrayList<ItemPost>();

		
		page = 1;
		pageCount = 0;

	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 * 
	 * @return
	 */
	public void setCate(String cate) {
		if (!cate.equals(MainActivity.currentCate)) {
			MainActivity.currentCate = cate;
			tabIndex = PinnedAdapter.MOINHAT;
			if (listViewNew != null) {
				listViewNew.removeAll(listViewNew);
				listViewNew = null;
			}
			if (listVideoLike != null) {
				listVideoLike.removeAll(listVideoLike);
				listVideoLike = null;
			}
			if(listVideoXemnhieu!=null){
				listVideoXemnhieu=new ArrayList<ItemPost>();
			}
			
			if (listvideo != null) {
				removeFotter();
			}
			onLoadData(false);
		}
	}

	public static FragmentCategory newInstance(String num, String title) {
		if (adapter != null) {
			adapter.clear();
		}
		if (frament == null)
			frament = new FragmentCategory();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString("num", num);
		args.putString("title", title);
		frament.setArguments(args);

		return frament;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity.currentCate = (String) (getArguments() != null ? getArguments()
				.getString("num") : 1);

	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.category_layout, container, false);

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

		adapter = new PinnedAdapter(PinnedAdapter.TYPE_VIEW_CATE, getActivity(), callBackOnlick);

		mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
		ActionBarPullToRefresh.from(getActivity())
				.insertLayoutInto((ViewGroup) v)
				.theseChildrenArePullable(R.id.listvideo, android.R.id.empty)
				.listener(this).setup(mPullToRefreshLayout);
		onLoadData(true);

		return v;
	}

	public void onLoadData(boolean isLoad) {
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE cateId='" + MainActivity.currentCate + "'";

		listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);

		queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
				+ MainActivity.currentCate + "'";

		countDataLocal = MainActivity.myDbHelper.getCountRow(
				DatabaseHelper.TB_LISTVIDEO, queryVideoLocal);
		Log.d("queryLikeVideo", countDataLocal + "");

		if (countDataLocal > 0) {

			isLoadLocal = true;
			listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
					queryVideoLocal, tabIndex);
			if (listViewNew != null && listViewNew.size() > 0) {
				pageCount = listViewNew.get(0).getPageCount();
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				addViewPost(false, listViewNew, PinnedAdapter.MOINHAT);
			}
		} else {
			isLoadLocal = false;
			String url = Utils.host + "get_posts?count=5&page=1&cat="
					+ MainActivity.currentCate;
			Log.d("url", url);

			new AysnRequestHttp((ViewGroup) v, Utils.LOAD_FIRST_DATA,
					MainActivity.smooth, callBack).execute(url);
		}
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
			queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
					+ " WHERE cateId='" + MainActivity.currentCate + "'";
			listVideoLike = Utils.getVideoLike(queryLikeVideo, type);
			
			switch (type) {
			case PinnedAdapter.MOINHAT:
				queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
						+ MainActivity.currentCate + "'";

				listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
						queryVideoLocal, tabIndex);
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				setViewTab(listViewNew);
				
				break;
			case PinnedAdapter.XEMNHIEU:
				queryVideoLocal = "SELECT * FROM tblListXemnhieu where cateId='"
						+ MainActivity.currentCate + "'";

				listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTXEMNHIEU,
						queryVideoLocal, tabIndex);
				
				resetTab();
				if (listVideoXemnhieu != null && listVideoXemnhieu.size() > 0) {
					for (int i = 0; i < listVideoXemnhieu.size(); i++) {
						listVideoXemnhieu.get(i).setType(PinnedAdapter.ITEM);
						adapter.add(listVideoXemnhieu.get(i));
					}
					adapter.notifyDataSetChanged();
				} else if (!isLoadding) {
					isLoadding = true;
					int page = random.nextInt(pageCount);
					String url = Utils.host + "get_posts?count=5&page=" + page
							+ "&cat=" + MainActivity.currentCate;

					new AysnRequestHttp((ViewGroup) v, Utils.LOAD_MORE,
							MainActivity.smooth, callBack).execute(url);
				}
				

				break;
			case PinnedAdapter.YEUTHICH:
				removeFotter();
				resetTab();
				mPullToRefreshLayout.setRefreshComplete();
				setViewTab(listVideoLike);
				break;
			}

		}

		@Override
		public void pushResutClickItem(int type, int position, boolean isLike) {
			queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
					+ " WHERE cateId='" + MainActivity.currentCate + "'";
			listVideoLike = Utils.getVideoLike(queryLikeVideo, type);
			switch (type) {
			case PinnedAdapter.MOINHAT:
				isLoadding=true;
				queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
						+ MainActivity.currentCate + "'";

				listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
						queryVideoLocal, tabIndex);
				
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				setViewTab(listViewNew);

				break;
			case PinnedAdapter.XEMNHIEU:
				listVideoXemnhieu = Utils.checkLikeVideo(listVideoXemnhieu,
						listVideoLike);
				setViewTab(Utils.checkLikeVideo(listVideoXemnhieu,
						listVideoLike));
				break;
			case PinnedAdapter.YEUTHICH:
				adapter.clear();
				adapter.notifyDataSetChanged();

				setViewTab(listVideoLike);

				break;
			}

		}

		@Override
		public void onCLickView(ItemPost item) {
			Utils.getVideoView(item, getActivity());
		}
	}

	public void resetTab() {
		adapter.clear();
		ItemPost section = new ItemPost();
		section.setType(PinnedAdapter.SECTION);
		section.setOption(tabIndex);
		adapter.add(section);
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
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE cateId='" + MainActivity.currentCate + "'";
		listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
		listData = Utils.checkLikeVideo(listData, listVideoLike);
		adapter.clear();
		ItemPost section = new ItemPost();
		section.setType(PinnedAdapter.SECTION);
		section.setOption(currentTab);
		adapter.add(section);

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
			Log.d("tabIndex1", tabIndex + "");
			showMessage();
			MainActivity.smooth.setVisibility(View.GONE);
			isLoadding = false;
			if (type == Utils.REFRESH) {
				if (result.length() > 0) {
					for (int i = 0; i < listViewNew.size(); i++) {
						adapter.remove(listViewNew.get(i));
					}
					MainActivity.myDbHelper.deleteAccount(
							DatabaseHelper.TB_LISTVIDEO,
							MainActivity.currentCate);

					adapter.notifyDataSetChanged();
					listViewNew = new ArrayList<ItemPost>();
				}

				mPullToRefreshLayout.setRefreshComplete();
			}
			if (type == PinnedAdapter.XEMNHIEU) {
				MainActivity.myDbHelper.deleteAccount(
						DatabaseHelper.TB_LISTXEMNHIEU,
						MainActivity.currentCate);
			}

			try {
				if (listViewNew == null)
					listViewNew = new ArrayList<ItemPost>();
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				int count_total = jsonObj.getInt("count_total");
				JSONObject jsoncate = jsonObj.getJSONObject("query");
				String cate = jsoncate.getString("cat");
				if (status.equals("ok") && count_total > 0
						&& cate.equals(MainActivity.currentCate)) {
					List<ItemPost> listTmp = new ArrayList<ItemPost>();
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item.setIdPost(json.getInt("id"));
						item.setStatus(json.getString("slug"));
						item.setCateId(MainActivity.currentCate);
						item.setPageCount(pageCount);
						item.setTitle(json.getString("title"));
						item.setStatus(json.getString("status"));
						item.setVideoId(getIdVideo(json.getString("content")));
						item.setUrl(json.getString("thumbnail"));
						item.setOption(tabIndex);
						listTmp.add(item);
						listViewNew.add(item);

						if (tabIndex == PinnedAdapter.MOINHAT) {

							saveData(item, DatabaseHelper.TB_LISTVIDEO);
						} else if (tabIndex == PinnedAdapter.XEMNHIEU) {
							saveData(item, DatabaseHelper.TB_LISTXEMNHIEU);
						}

					}

					if (type == Utils.LOAD_FIRST_DATA) {
						addViewPost(true, listViewNew, tabIndex);
					} else {
						removeFotter();
						listVideoLike = Utils.getVideoLike(queryLikeVideo,
								tabIndex);
						listTmp = Utils.checkLikeVideo(listTmp, listVideoLike);
						for (int i = 0; i < listTmp.size(); i++) {
							if (listTmp.get(i).getStatus().equals("publish")) {
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
			showMessage();
		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			// TODO Auto-generated method stub

		}
	}

	public void saveData(ItemPost item, String tblName) {
		MainActivity.myDbHelper.insertListVideo(tblName, item.getCateId(),
				item.getTitle(), item.getVideoId(), item.getUrl(),
				item.getStatus(), item.getPageCount(), item.getIdPost(),
				item.getSlug());
	}

	public void showMessage() {
		if (adapter == null || adapter.getCount() == 0) {
			MainActivity.lblError.setVisibility(View.VISIBLE);
		} else {
			MainActivity.lblError.setVisibility(View.GONE);
		}
	}

	public void removeFotter() {
		if (listvideo.getFooterViewsCount() > 0)
			listvideo.removeFooterView(fotter);
	}

	public void removeHeader() {
		if (listvideo.getHeaderViewsCount() > 0)
			listvideo.removeHeaderView(fotter);
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
			String url = Utils.host + "get_posts?count=5&page=1&cat="
					+ MainActivity.currentCate;

			new AysnRequestHttp((ViewGroup) v, Utils.REFRESH, null, callBack)
					.execute(url);

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

			if (!isLoadding & tabIndex == PinnedAdapter.MOINHAT
					& Utils.isOnline(getActivity())) {
				isLoadding = true;
				if (listvideo.getFooterViewsCount() == 0)
					listvideo.addFooterView(fotter);
				String url = Utils.host + "get_posts?count=5&page=" + page
						+ "&cat=" + MainActivity.currentCate;
				int keyOption = Utils.LOAD_MORE;

				new AysnRequestHttp((ViewGroup) v, keyOption,
						MainActivity.smooth, callBack).execute(url);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
