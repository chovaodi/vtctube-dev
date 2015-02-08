package com.vtc.vtctube.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailymotion.websdk.DMWebVideoView;
import com.vtc.xemtivi.R;

public class FragmentVimeo extends Fragment {
	private DMWebVideoView mVideoView;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_vimeo, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// how plugin is enabled change in API 8
		mVideoView = ((DMWebVideoView) view.findViewById(R.id.dmWebVideoView));
		mVideoView.setVideoId("x2frsoi", false);
		super.onActivityCreated(savedInstanceState);
	}
}
