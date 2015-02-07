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

package com.vtc.xemtivi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Picasso;
import com.vtc.vtctube.adpter.GridViewWithHeaderAndFooter;
import com.vtc.vtctube.adpter.Item;
import com.vtc.vtctube.adpter.MenuHomeAdapter;
import com.vtc.vtctube.adpter.HomeAdapter;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.model.ItemCategory;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.OnDisplayVideo;
import com.vtc.vtctube.utils.Utils;

public class FragmentHome extends SherlockFragment {
	private GridViewWithHeaderAndFooter gridView;
	private View view;
	private FrameLayout frameLayout;
	// private PullToRefreshLayout mPullToRefreshLayout;
	ResultCallBack callBack = null;
	public static List<ItemCategory> listData = null;
	public static String[] cateName;
	private static int page = 1;
	private static int pageCount = 0;
	private static FragmentHome f = null;
	private OnDisplayVideo mOnDisplayVideo;
	private HomeAdapter adapter;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static FragmentHome newInstance(int num) {
		if (f == null) {
			f = new FragmentHome();
		}

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callBack = new ResultCallBack();
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
		view = inflater.inflate(R.layout.fragment_home, container, false);
		String url = Utils.host + "get_posts?count=20&page=1";
		Log.d("actionNewvideo", url);
		ResultCallBack callBack = new ResultCallBack();
		new AysnRequestHttp(null, Utils.LOAD_NEWVIDEO, null, callBack)
				.execute(url);
		return view;
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result",result);
			try {
				

				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				pageCount = jsonObj.getInt("pages");
				int count_total = jsonObj.getInt("count_total");
				JSONObject jsoncate = jsonObj.getJSONObject("query");
			//	String cate = jsoncate.getString("cat");
				if (status.equals("ok") && count_total > 0) {
					List<ItemPost> listTmp = new ArrayList<ItemPost>();
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();
						JSONObject json = (JSONObject) jsonArray.get(i);
						item = Utils.getItemPost(json, pageCount, 0);

						listTmp.add(item);

					}



					if(adapter==null){
						adapter=new HomeAdapter(getActivity(), R.layout.item_img_search, listTmp);
						gridView.setAdapter(adapter);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View headerView = getActivity().getLayoutInflater().inflate(
				R.layout.header_home, null);
		gridView = (GridViewWithHeaderAndFooter) view.findViewById(R.id.list);
		gridView.addHeaderView(headerView);
		gridView.setNumColumns(2);

		
		

	}

	public void addview() {
		ImageView img = (ImageView) view.findViewById(R.id.imageView1);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MainActivity.smooth.setVisibility(View.GONE);
				Utils.getVideoView(AcitivityLoadding.itemPost, getActivity(),
						null);
				if (mOnDisplayVideo != null
						&& AcitivityLoadding.itemPost != null) {
					mOnDisplayVideo.display();
				} else {
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.not_available_while_offline),
							Toast.LENGTH_LONG).show();

				}
			}
		});
		String idPostHome = "xxx";
		if (AcitivityLoadding.itemPost != null) {
			idPostHome = AcitivityLoadding.itemPost.getVideoId();
		}

		Picasso.with(getActivity())
				.load("http://img.youtube.com/vi/" + idPostHome
						+ "/maxresdefault.jpg")
				.placeholder(R.drawable.bgr_home_video).into(img);

		String url = Utils.getUrlHttp(Utils.host, "get_category_index");
		Log.d("url", url);
		if (GlobalApplication.dataCate.length() > 0) {
			showView(GlobalApplication.dataCate);
		} else {
			if (Utils.isExistFile(Utils.GET_CATE_INDEX)) {
				GlobalApplication.dataCate = Utils
						.readJsonFile(Utils.GET_CATE_INDEX);
				showView(GlobalApplication.dataCate);
			} else {
				new AysnRequestHttp((ViewGroup) view, Utils.LOAD_FIRST_DATA,
						MainActivity.smooth, callBack).execute(url);
			}
		}
		new AysnRequestHttp((ViewGroup) view, Utils.REFRESH, null, callBack)
				.execute(url);
	}

	public void showView(String result) {
		
	}

}
