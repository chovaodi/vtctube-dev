package com.vtc.basetube.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.utils.OnDisplayVideo;

public class FragmentSearch extends Fragment {
	private static Fragment fragment = null;
	private String txtSearch;
	private OnDisplayVideo mOnDisplayVideo;

	public static Fragment newInstance(String txtSearch) {
		if (fragment == null)
			fragment = new FragmentSearch();
		Bundle args = new Bundle();
		args.putString("txtSearch", txtSearch);
		fragment.setArguments(args);
		return fragment;
	}

	public void updateValue(String txtSearch) {
		this.txtSearch = txtSearch;
		// call Api search
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		txtSearch = (String) (getArguments() != null ? getArguments()
				.getString("txtSearch") : 1);

	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof OnDisplayVideo) {
			mOnDisplayVideo = (OnDisplayVideo) activity;
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		VideoAdapter adapterVideo = new VideoAdapter(getActivity());
		View view = getView();
		ListView listvideo = (ListView) view.findViewById(R.id.listivideo);

		for (int i = 0; i < 10; i++) {
			ItemVideo item = new ItemVideo();
			item.setTitle("Search title" + i);
			item.setId("wwXj_a3Vjhc");
			item.setUploader("QuangNinhTV");
			item.setTime("");
			item.setCountView("");
			item.setThumbnail("");
			adapterVideo.addItem(item);
		}
		listvideo.setAdapter(adapterVideo);
		listvideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mOnDisplayVideo != null) {
					// mOnDisplayVideo.display(list.get(arg2));
				}
			}

		});

		updateValue(txtSearch);
	}

}
