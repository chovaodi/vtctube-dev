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

package com.vtc.basetube;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.ItemVideo;

public class FragmentHome extends SherlockFragment {
	private ListView listvideo;
	private View view;
	private VideoAdapter adapterVideo = null;

	private static FragmentHome framgnent = null;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static FragmentHome newInstance() {
		if (framgnent == null) 
			framgnent = new FragmentHome();
		return framgnent;
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
		super.onAttach(activity);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listvideo = (ListView) view.findViewById(R.id.listivideo);
		addview();

	}

	public void addview() {
		List<String> listCate = new ArrayList<String>();
		listCate.add("Sống");
		listCate.add("Chơi");
		listCate.add("Khỏe");
		listCate.add("Đẹp");
		List<ItemVideo> listVideo = new ArrayList<ItemVideo>();
		for (int i = 0; i < 10; i++) {
			ItemVideo item = new ItemVideo();
			item.setTitle("Demo " + i);
			item.setId("79iWPoJJnCw");
			item.setTime("3h trước");
			item.setUploader("QuangNinhTV");
			item.setCountView("30 lượt xem");
			listVideo.add(item);
		}
		setUpAdapter(listCate, listVideo);
	}

	public void setUpAdapter(List<String> listCate, List<ItemVideo> listVideo) {
		if (adapterVideo == null)
			adapterVideo = new VideoAdapter(getActivity());
		for (int i = 0; i < listCate.size(); i++) {
			ItemVideo itemCate = new ItemVideo();
			itemCate.setTitle(listCate.get(i));
			adapterVideo.addSeparatorItem(itemCate);
			for (int j = 0; j < listVideo.size(); j++) {
				adapterVideo.addItem(listVideo.get(j));
			}
		}

		listvideo.setAdapter(adapterVideo);
	}
}
