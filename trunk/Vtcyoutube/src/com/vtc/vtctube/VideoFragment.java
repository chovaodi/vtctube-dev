package com.vtc.vtctube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.vtc.vtctube.utils.Utils;

public class VideoFragment extends YouTubePlayerSupportFragment {

	private String currentVideoID = "video_id";
	private YouTubePlayer activePlayer;

	public static VideoFragment newInstance(String url) {

		VideoFragment playerYouTubeFrag = new VideoFragment();

		Bundle bundle = new Bundle();
		bundle.putString("url", url);

		playerYouTubeFrag.setArguments(bundle);

		return playerYouTubeFrag;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.playerview_demo, container, false);

		YouTubePlayerSupportFragment youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getFragmentManager()
				.findFragmentById(R.id.youtube_view);

		youTubePlayerSupportFragment.initialize(Utils.DEVELOPER_KEY_YOUTUBE,
				new OnInitializedListener() {

					@Override
					public void onInitializationSuccess(Provider arg0,
							YouTubePlayer arg1, boolean arg2) {
						// TODO Auto-generated method stub
						arg1.cueVideo("k7rFeXDPjqg");
					}

					@Override
					public void onInitializationFailure(Provider arg0,
							YouTubeInitializationResult arg1) {
						// TODO Auto-generated method stub

					}
				});

		return view;
	}

}