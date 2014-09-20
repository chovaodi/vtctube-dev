package com.vtc.vtctube.like;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.viewpagerindicator.PageIndicator;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.PinnedSectionListView;
import com.vtc.vtctube.category.SliderTopFragmentAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentResent extends Fragment {
	private static PinnedAdapter adapter = null;
	private static ListView listvideo;
	private View header;

	private ViewPager pager;

	public static SmoothProgressBar smooth;
	private String queryResent;
	private ResultOnclickTab callBackOnlick = null;

	private List<ItemPost> listData = null;
	private int key;

	int mNum;
	private View v;
	private static FragmentResent f = null;

	// private PullToRefreshLayout mPullToRefreshLayout;
	public static String[] cateName;

	
	public void onResumeData(int key) {
		this.key = key;
		init();
		adapter.clear();
		addViewPost();
	}

	public void init() {
		if (callBackOnlick == null) {
			callBackOnlick = new ResultOnclickTab();
		}


		if (listData == null) {
			listData = new ArrayList<ItemPost>();
		}
	}

	public static FragmentResent newInstance(int num) {
		if (f == null)
			f = new FragmentResent();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		key = getArguments() != null ? getArguments().getInt("num") : 1;
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		init();
		v = inflater.inflate(R.layout.category_layout, container, false);
		adapter = new PinnedAdapter(getActivity(), callBackOnlick);
		// if (key == R.id.menu_video_yeuthich) {
		// getSupportActionBar().setTitle("Video yêu thích");
		// } else {
		// getSupportActionBar().setTitle("Video đã xem");
		// }

		smooth = (SmoothProgressBar) v.findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		adapter = new PinnedAdapter(getActivity(), callBackOnlick);
		listvideo = (ListView) v.findViewById(R.id.listvideo);
		header = getActivity().getLayoutInflater().inflate(
				R.layout.header_cate, null);
		listvideo.addHeaderView(header);

		pager = (ViewPager) header.findViewById(R.id.pager);
		SliderTopFragmentAdapter adapterPg = new SliderTopFragmentAdapter(
				getActivity().getSupportFragmentManager());

		pager.setAdapter(adapterPg);

		PageIndicator mIndicator = (PageIndicator) header
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);

		((PinnedSectionListView) listvideo).setShadowVisible(false);

		addViewPost();
		return v;

	}

	public class ResultOnclickTab implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int position, boolean isLike) {
			switch (type) {
			case PinnedAdapter.YEUTHICH:
				if (!isLike) {
					adapter.remove(listData.get(position));
				} else {
					adapter.getItem(position).setLike(isLike);
				}
				break;
			case PinnedAdapter.MOINHAT:
				adapter.getItem(position).setLike(isLike);
				break;
			}

			adapter.notifyDataSetChanged();
		}

		@Override
		public void onCLickView(int type, String idYoutube) {
			Utils.getVideoView(idYoutube, getActivity());

		}
	}

	public void addViewPost() {
		String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		if (key == R.id.menu_video_yeuthich) {
			listData = Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH);

		} else {
			Log.d("1111111111", "222222222222222");
			queryResent = "SELECT * FROM " + DatabaseHelper.TB_RESENT;
			listData = Utils.getVideoLocal(queryResent, PinnedAdapter.MOINHAT);
			listData = Utils.checkLikeVideo(listData,
					Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH));
		}

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
		}

		listvideo.setAdapter(adapter);
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

}