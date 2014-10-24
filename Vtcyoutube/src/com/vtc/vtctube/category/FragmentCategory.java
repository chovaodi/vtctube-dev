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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
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
	// private View header;
	private View fotter;

	public static PinnedAdapter adapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	private static Button btnMoinhat;
	private static Button btnXemnhieu;
	private static Button btnYeuthich;
	private static LinearLayout lineTab;

	public static ListView listvideo;

	private static int page = 1;
	private static int pageCount = 0;
	private int countDataLocal;
	private static int tabIndex = PinnedAdapter.MOINHAT;
	private int pageSize = 5;

	private String queryVideoLocal;
	private String queryLikeVideo;

	private static boolean isLoadding = false;
	private boolean isLoadLocal = true;

	private ResultOnclikTab callBackOnlick = new ResultOnclikTab();
	private ResultCallBack callBack = new ResultCallBack();;

	private static List<ItemPost> listViewNew = null;
	private static List<ItemPost> listVideoLike = null;
	private static List<ItemPost> listVideoXemnhieu = null;
	private Random random = new Random();
	public static FragmentCategory frament = null;

	public FragmentCategory() {
		page = 1;
		pageCount = 0;
	}

	public void setCate(String cate) {
		if (!cate.equals(MainActivity.currentCate)) {
			MainActivity.currentCate = cate;

			initData();

			resetTab();
			onLoadData(false);
		}
	}

	public static void initData() {
		isLoadding = false;
		listVideoLike = new ArrayList<ItemPost>();
		listVideoXemnhieu = new ArrayList<ItemPost>();
		listViewNew = new ArrayList<ItemPost>();
		tabIndex = PinnedAdapter.MOINHAT;
		page = 1;
		pageCount = 0;
		if (adapter != null) {
			adapter.clear();
			adapter.notifyDataSetChanged();
		}

	}

	public static FragmentCategory newInstance(String num, String title) {
		initData();
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
		lineTab = (LinearLayout) v.findViewById(R.id.lineTab);
		lineTab.setVisibility(View.VISIBLE);

		listvideo = (ListView) v.findViewById(R.id.listvideo);
		fotter = getActivity().getLayoutInflater().inflate(
				R.layout.fotter_loadmore, null);

		listvideo.setOnScrollListener(this);

		adapter = new PinnedAdapter(PinnedAdapter.TYPE_VIEW_CATE,
				getActivity(), callBackOnlick);

		mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
		ActionBarPullToRefresh.from(getActivity())
				.insertLayoutInto((ViewGroup) v)
				.theseChildrenArePullable(R.id.listvideo, android.R.id.empty)
				.listener(this).setup(mPullToRefreshLayout);
		onLoadData(true);

		btnMoinhat = (Button) v.findViewById(R.id.btnChitiet);
		btnXemnhieu = (Button) v.findViewById(R.id.button2);
		btnYeuthich = (Button) v.findViewById(R.id.button3);
		resetTab();

		btnMoinhat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tabIndex = PinnedAdapter.MOINHAT;
				btnXemnhieu.setSelected(false);
				btnYeuthich.setSelected(false);
				if (!btnMoinhat.isSelected())
					callBackOnlick.getResult(PinnedAdapter.MOINHAT, "");
				btnMoinhat.setSelected(true);
			}
		});
		btnXemnhieu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tabIndex = PinnedAdapter.XEMNHIEU;
				btnMoinhat.setSelected(false);
				btnYeuthich.setSelected(false);
				if (!btnXemnhieu.isSelected())
					callBackOnlick.getResult(PinnedAdapter.XEMNHIEU, "");
				btnXemnhieu.setSelected(true);
			}
		});

		btnYeuthich.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			
				if (!btnYeuthich.isSelected())
					callBackOnlick.getResult(PinnedAdapter.YEUTHICH, "");
			
			}
		});

		return v;
	}

	public void resetTab() {
		btnMoinhat.setSelected(true);
		btnXemnhieu.setSelected(false);
		btnYeuthich.setSelected(false);
	}

	public void onLoadData(boolean isLoad) {
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE cateId='" + MainActivity.currentCate + "'";

		listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);

		queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
				+ MainActivity.currentCate + "'";

		countDataLocal = MainActivity.myDbHelper.getCountRow(
				DatabaseHelper.TB_LISTVIDEO, queryVideoLocal);

		if (countDataLocal > 0) {

			isLoadLocal = true;
			listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
					queryVideoLocal, tabIndex);
			if (listViewNew != null && listViewNew.size() > 0) {
				pageCount = listViewNew.get(0).getPageCount();
				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				addViewPost(listViewNew, PinnedAdapter.MOINHAT);
			}
		} else {
			isLoadLocal = false;
			String url = Utils.host + "get_posts?count=5&page=1&cat="
					+ MainActivity.currentCate;

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
				listVideoLike = new ArrayList<ItemPost>();
				listVideoXemnhieu = new ArrayList<ItemPost>();
				listViewNew = new ArrayList<ItemPost>();

				Utils.disableEnableControls(false, (ViewGroup) v);
				int page = 1;
				if (pageCount != 0)
					page = random.nextInt(pageCount);
				String url = Utils.host + "get_posts?count=5&page=" + page
						+ "&cat=" + MainActivity.currentCate;
				Log.d("url", url);

				new AysnRequestHttp((ViewGroup) v, Utils.LOAD_XEMNHIEU,
						MainActivity.smooth, callBack).execute(url);

				break;
			case PinnedAdapter.YEUTHICH:
				mPullToRefreshLayout.setRefreshComplete();
				if (listVideoLike.size() == 0) {
					Utils.getDialogMessges(getActivity(),
							"Danh sách yêu thích rỗng");
					return;
				}
				tabIndex = PinnedAdapter.YEUTHICH;
				btnMoinhat.setSelected(false);
				btnXemnhieu.setSelected(false);
				btnYeuthich.setSelected(true);
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
				queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
						+ MainActivity.currentCate + "'";

				listViewNew = Utils.getVideoLocal(DatabaseHelper.TB_LISTVIDEO,
						queryVideoLocal, tabIndex);

				listViewNew = Utils.checkLikeVideo(listViewNew, listVideoLike);
				setViewTab(listViewNew);

				break;
			case PinnedAdapter.XEMNHIEU:
				// queryVideoLocal =
				// "SELECT * FROM tblListXemnhieu where cateId='"
				// + MainActivity.currentCate + "'";
				//
				// listVideoXemnhieu = Utils.getVideoLocal(
				// DatabaseHelper.TB_LISTXEMNHIEU, queryVideoLocal,
				// tabIndex);
				listVideoXemnhieu = Utils.checkLikeVideo(listVideoXemnhieu,
						listVideoLike);
				setViewTab(Utils.checkLikeVideo(listVideoXemnhieu,
						listVideoLike));
				break;
			case PinnedAdapter.YEUTHICH:
				adapter.clear();
				setViewTab(listVideoLike);

				break;
			}

		}

		@Override
		public void onCLickView(ItemPost item) {
			Utils.getVideoView(item, getActivity());
		}
	}

	public void setViewTab(List<ItemPost> list) {
		adapter.clear();
		adapter.notifyDataSetChanged();

		for (int i = 0; i < list.size(); i++) {
			list.get(i).setType(PinnedAdapter.ITEM);
			adapter.add(list.get(i));
		}
		adapter.notifyDataSetChanged();

	}

	public void addViewPost(List<ItemPost> listData, int currentTab) {
		queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE
				+ " WHERE cateId='" + MainActivity.currentCate + "'";
		listVideoLike = Utils.getVideoLike(queryLikeVideo, tabIndex);
		listData = Utils.checkLikeVideo(listData, listVideoLike);
		adapter.clear();
		adapter.notifyDataSetChanged();

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
			Log.d("result", result);
			isLoadding = false;
			Utils.disableEnableControls(true, (ViewGroup) v);

			showMessage();
			MainActivity.smooth.setVisibility(View.GONE);

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
						item = Utils.getItemPost(json, pageCount, tabIndex);

						listTmp.add(item);
						listViewNew.add(item);

						if (tabIndex == PinnedAdapter.MOINHAT) {
							saveData(item, DatabaseHelper.TB_LISTVIDEO);
						} else if (tabIndex == PinnedAdapter.XEMNHIEU) {
							listVideoXemnhieu.add(item);
						}

					}

					if (type == Utils.LOAD_FIRST_DATA) {
						addViewPost(listViewNew, tabIndex);
					} else {
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
				item.getSlug(), item.getCountview());
	}

	public void showMessage() {
		if (adapter == null || adapter.getCount() == 0) {
			MainActivity.lblError.setVisibility(View.VISIBLE);
		} else {
			MainActivity.lblError.setVisibility(View.GONE);
		}
	}

	public void removeHeader() {
		if (listvideo.getHeaderViewsCount() > 0)
			listvideo.removeHeaderView(fotter);
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
			Log.d("url", url);
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

			if (page >= pageCount) {
				isLoadding = true;
			} else {
				isLoadding = false;
			}
			Log.d("tabIndex", pageCount + " " + page);
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
