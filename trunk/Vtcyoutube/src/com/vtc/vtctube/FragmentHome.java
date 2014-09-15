/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vtc.vtctube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.vtc.vtctube.adpter.MenuHomeAdapter;
import com.vtc.vtctube.model.ItemCategory;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.GridView;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

public class FragmentHome extends SherlockFragment implements
		YouTubePlayer.OnInitializedListener {
	int mNum;
	private GridView list;
	private View v;
	private YouTubePlayerView youTubeView;

	// private PullToRefreshLayout mPullToRefreshLayout;
	ResultCallBack callBack = null;
	public static List<ItemCategory> listData = null;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static FragmentHome newInstance(int num) {
		FragmentHome f = new FragmentHome();

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
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		callBack = new ResultCallBack();
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_home, container, false);

		String url = Utils.getUrlHttp(Utils.host, "get_category_index");
		if (GlobalApplication.dataCate.length() > 0) {
			showView(GlobalApplication.dataCate);
		} else {
			if (Utils.isExistFile(Utils.GET_CATE_INDEX)) {
				GlobalApplication.dataCate = Utils
						.readJsonFile(Utils.GET_CATE_INDEX);
				showView(GlobalApplication.dataCate);
			} else {
				
				new AysnRequestHttp(Utils.LOAD_FIRST_DATA, MainActivity.smooth,
						callBack).execute(url);
			}
		}
		new AysnRequestHttp(Utils.REFRESH, null, callBack)
				.execute(url);
		return v;
	}

	static class CustomisedHeaderTransformer extends HeaderTransformer {

		private View mHeaderView;
		private TextView mMainTextView;
		private TextView mProgressTextView;

		@Override
		public void onViewCreated(Activity activity, View headerView) {
			mHeaderView = headerView;
			mMainTextView = (TextView) headerView.findViewById(R.id.ptr_text);
			mProgressTextView = (TextView) headerView
					.findViewById(R.id.ptr_text_secondary);
		}

		@Override
		public void onReset() {
			mMainTextView.setVisibility(View.VISIBLE);
			mMainTextView.setText(R.string.pull_to_refresh_pull_label);

			mProgressTextView.setVisibility(View.GONE);
			mProgressTextView.setText("");
		}

		@Override
		public void onPulled(float percentagePulled) {
			mProgressTextView.setVisibility(View.VISIBLE);
			mProgressTextView
					.setText(Math.round(100f * percentagePulled) + "%");
		}

		@Override
		public void onRefreshStarted() {
			mMainTextView.setText(R.string.pull_to_refresh_refreshing_label);
			mProgressTextView.setVisibility(View.GONE);
		}

		@Override
		public void onReleaseToRefresh() {
			mMainTextView.setText(R.string.pull_to_refresh_release_label);
		}

		@Override
		public void onRefreshMinimized() {
			// In this header transformer, we will ignore this call
		}

		@Override
		public boolean showHeaderView() {
			final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;
			if (changeVis) {
				mHeaderView.setVisibility(View.VISIBLE);
			}
			return changeVis;
		}

		@Override
		public boolean hideHeaderView() {
			final boolean changeVis = mHeaderView.getVisibility() == View.VISIBLE;
			if (changeVis) {
				mHeaderView.setVisibility(View.GONE);
			}
			return changeVis;
		}
	}

	public void showView(String result) {

		try {
			JSONObject jsonObj = new JSONObject(result);
			String status = jsonObj.getString("status");
			if (status.equals("ok")) {
				JSONArray jsonArray = jsonObj.getJSONArray("categories");
				listData = new ArrayList<ItemCategory>();
				for (int i = 0; i < jsonArray.length(); i++) {
					ItemCategory item = new ItemCategory();
					JSONObject json = (JSONObject) jsonArray.get(i);
					item.setIdCategory(json.getString("id"));
					item.setTitle(json.getString("title"));
					item.setSlug(json.getString("slug"));
					item.setPostcount(json.getInt("post_count"));
					listData.add(item);
				}
			}

			list = (GridView) v.findViewById(R.id.list);
			View header = getActivity().getLayoutInflater().inflate(
					R.layout.header, null);

			// youTubeView = (YouTubePlayerView) header
			// .findViewById(R.id.youtube_view);
			// youTubeView.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);

			list.addHeaderView(header);
			list.setNumColumns(2);
			list.setPadding(Utils.convertDpToPixel(20, getActivity()), 0,
					Utils.convertDpToPixel(20, getActivity()), 0);

			MenuHomeAdapter adapter = new MenuHomeAdapter(getActivity(),
					listData);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Utils.hideSoftKeyboard(getActivity());
					MainActivity.callBackCLickCate.getCate(listData.get(arg2)
							.getTitle(), listData.get(arg2).getIdCategory());

				}

			});

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	// @Override
	// public void onRefreshStarted(View view) {
	// // Hide the list
	//
	// /**
	// * Simulate Refresh with 4 seconds sleep
	// */
	// new AsyncTask<String, Integer, String>() {
	//
	// @Override
	// protected String doInBackground(String... params) {
	// String json = "";
	// JSONParser jsonParser = new JSONParser();
	// try {
	// json = jsonParser.makeHttpRequest(Utils.getUrlHttp(
	// Utils.host, Utils.GET_CATE_INDEX));
	// } catch (NetworkErrorException e) {
	// e.printStackTrace();
	// }
	// return json;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	//
	// // Notify PullToRefreshLayout that the refresh has finished
	// mPullToRefreshLayout.setRefreshComplete();
	//
	// }
	// }.execute();
	// }

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			GlobalApplication.dataCate = result;
			if (result.length() > 0)
				Utils.writeJsonFile(result, false, Utils.GET_CATE_INDEX);
			if (type == Utils.LOAD_FIRST_DATA) {
				showView(result);
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

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo("wKJ9KzGQq0w");
		}
	}
}
