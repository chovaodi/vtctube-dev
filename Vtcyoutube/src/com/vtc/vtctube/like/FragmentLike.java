package com.vtc.vtctube.like;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.OnDisplayVideo;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentLike extends Fragment {
	private static PinnedAdapter adapter = null;
	private static ListView listvideo;

	public static SmoothProgressBar smooth;
	private ResultOnclickTab callBackOnlick = null;
	private ResultSearchCallBack callBackSearch;

	private List<ItemPost> listData = null;

	int mNum;
	private View v;
	private static FragmentLike f = null;
	private OnDisplayVideo mOnDisplayVideo;

	// private PullToRefreshLayout mPullToRefreshLayout;
	public static String[] cateName;

	public static FragmentLike newInstance() {
		if (f == null) {
			f = new FragmentLike();
		}
		return f;
	}

	public void onResumeData(int key) {
		init();
		adapter.clear();
		addViewPost();
	}

	public void init() {
		if (callBackOnlick == null) {
			callBackOnlick = new ResultOnclickTab();
		}
		if (callBackSearch == null) {
			callBackSearch = new ResultSearchCallBack();
		}

		if (listData == null) {
			listData = new ArrayList<ItemPost>();
		}

	}

	public class ResultSearchCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result", result);
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				int count_total = jsonObj.getInt("count_total");
				if (status.equals("ok") && count_total > 0) {
					listData = new ArrayList<ItemPost>();
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item = Utils.getItemPost(json, 0, 0);
						listData.add(item);
					}

				}
			} catch (Exception e) {
			}
			addView();
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

	public static FragmentLike newInstance(int num) {
		f = new FragmentLike();
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
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof OnDisplayVideo) {
			mOnDisplayVideo = (OnDisplayVideo) activity;
		}
		super.onAttach(activity);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		init();
		v = inflater.inflate(R.layout.category_layout, container, false);
		adapter = new PinnedAdapter(PinnedAdapter.TYPE_VIEW_CATE,
				getActivity(), callBackOnlick);

		smooth = (SmoothProgressBar) v.findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		listvideo = (ListView) v.findViewById(R.id.listvideo);
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
			adapter.getItem(position).setLike(isLike);
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onCLickView(ItemPost item) {
			Utils.getVideoView(item, getActivity(), listData);
			if (mOnDisplayVideo != null) {
				mOnDisplayVideo.display();
			}
		}
	}

	public void addViewPost() {
		String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		listData = Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH);
		if (listData.size() == 0) {
			String url = Utils.host + "get_posts?count=20&page=5";
			new AysnRequestHttp((ViewGroup) v, Utils.LOAD_XEMNHIEU,
					MainActivity.smooth, callBackSearch).execute(url);
		} else {
			addView();
		}

	}

	public void addView() {
		if (listData == null || listData.size() == 0) {
			MainActivity.lblError.setVisibility(View.VISIBLE);
			return;
		}

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				Log.d("demo", "demo" + i);
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
