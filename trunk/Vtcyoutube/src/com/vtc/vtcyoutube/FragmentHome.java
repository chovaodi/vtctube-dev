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

package com.vtc.vtcyoutube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.munix.gridviewheader.TestAdapter;
import com.vtc.vtcyoutube.connectserver.AysnRequestHttp;
import com.vtc.vtcyoutube.connectserver.IResult;
import com.vtc.vtcyoutube.utils.Utils;

public class FragmentHome extends SherlockFragment {
	int mNum;
	private GridView list;
	private View v;

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
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_home, container, false);

		ResultCallBack callBack = new ResultCallBack();
		new AysnRequestHttp(Utils.LOAD_CATEGORY, MainActivity.smooth, callBack)
				.execute("http://vtctube.vn/api/get_category_index");

		return v;
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			List<ItemCategory> listData = null;
			Log.d("result", result);
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
						getActivity().startActivity(intent);

					}

				});

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
}
