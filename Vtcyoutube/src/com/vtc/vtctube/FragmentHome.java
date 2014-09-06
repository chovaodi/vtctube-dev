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

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.munix.gridviewheader.TestAdapter;
import com.vtc.vtctube.connectserver.AysnRequestHttp;
import com.vtc.vtctube.connectserver.IResult;
import com.vtc.vtctube.connectserver.JSONParser;
import com.vtc.vtctube.utils.Utils;

public class FragmentHome extends SherlockFragment implements OnRefreshListener {
	int mNum;
	private GridView list;
	private View v;
	private PullToRefreshLayout mPullToRefreshLayout;
	ResultCallBack callBack = null;
	List<ItemCategory> listData = null;
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

		mPullToRefreshLayout = (PullToRefreshLayout) v
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).options(Options.create()
		// Here we make the refresh scroll distance to 75% of the GridView
		// height
				.scrollDistance(.75f)
				// Here we define a custom header layout which will be inflated
				// and used
				.headerLayout(R.layout.customised_header)
				// Here we define a custom header transformer which will alter
				// the header
				// based on the current pull-to-refresh state
				.headerTransformer(new CustomisedHeaderTransformer()).build())
				.allChildrenArePullable().listener(this)
				// Here we'll set a custom ViewDelegate
				.useViewDelegate(GridView.class, new AbsListViewDelegate())
				.setup(mPullToRefreshLayout);

		if (GlobalApplication.dataCate.length() > 0) {
			showView(GlobalApplication.dataCate);
		} else {
			if (Utils.isExistFile(Utils.GET_CATE_INDEX)) {
				GlobalApplication.dataCate = Utils
						.readJsonFile(Utils.GET_CATE_INDEX);
				showView(GlobalApplication.dataCate);
			} else {
				String url = Utils.getUrlHttp(Utils.host, "get_category_index");

				new AysnRequestHttp(Utils.LOAD_FIRST_DATA, MainActivity.smooth,
						callBack).execute(url);
			}
		}

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
			TestAdapter adapter = new TestAdapter(getActivity(), listData);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(getActivity(),
							CategoryActivity.class);
					intent.putExtra("cate", listData.get(arg2).getIdCategory());
					intent.putExtra("title", listData.get(arg2).getTitle());
					
					getActivity().startActivity(intent);

				}

			});

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Override
	public void onRefreshStarted(View view) {
		// Hide the list

		/**
		 * Simulate Refresh with 4 seconds sleep
		 */
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... params) {
				String json = "";
				JSONParser jsonParser = new JSONParser();
				try {
					json = jsonParser.makeHttpRequest(Utils.getUrlHttp(
							Utils.host, Utils.GET_CATE_INDEX));
				} catch (NetworkErrorException e) {
					e.printStackTrace();
				}
				return json;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();

			}
		}.execute();
	}

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
	}
}
