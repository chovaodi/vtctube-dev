package com.vtc.basetube.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.ItemVideo;

public class FragmentLike extends Fragment {
	private static Fragment fragment = null;

	public static Fragment newInstance() {
		if (fragment == null)
			fragment = new FragmentLike();
		return fragment;
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
			item.setTitle("Video " + i);
			item.setId("79iWPoJJnCw");
			item.setTime("3h trước");
			item.setUploader("QuangNinhTV");
			item.setCountView("30 lượt xem");
			// item.setThumbnail(dt.getThumbnail());
			adapterVideo.addItem(item);
		}
		listvideo.setAdapter(adapterVideo);
	}

}
