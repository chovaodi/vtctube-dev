package com.vtc.basetube.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.pedrovgs.draggablepanel.DraggableListener;
import com.pedrovgs.draggablepanel.DraggableView;
import com.vtc.basetube.R;
import com.vtc.basetube.utils.Utils;

public class VideoPlayerFragment extends YoutubePlayerFragment {
	String videoId = "";

	public static ViewGroup mainView;

	private YouTubePlayerSupportFragment mYoutubeFragment;
	private YouTubePlayer mPlayer;

	private DraggableView mView;

	private static VideoPlayerFragment sInstance = null;

	public static VideoPlayerFragment newInstance() {
		if (sInstance == null)
			sInstance = new VideoPlayerFragment();

		return sInstance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = (DraggableView) inflater.inflate(R.layout.playerview_demo,
				container, false);
		hookDraggablePanelListeners();
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mYoutubeFragment = new YouTubePlayerSupportFragment();
		getFragmentManager().beginTransaction()
				.replace(R.id.youtube_player_fragment, mYoutubeFragment).addToBackStack(null)
				.commit();
		updateData();
	}

	public void updateData() {

		if (mPlayer != null) {
			mPlayer.cueVideo(videoId);
		} else {
			mYoutubeFragment.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);
		}
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		this.mPlayer = player;
		if (!wasRestored && videoId.length() > 0) {
			player.cueVideo(videoId);
		}
	}

	@Override
	protected Provider getYouTubePlayerProvider() {
		return (YouTubePlayerSupportFragment) getFragmentManager()
				.findFragmentById(R.id.youtube_player_fragment);
	}

	public void maximize() {
		mView.maximize();
	}

	public void minimize() {
		mView.minimize();
	}

	public boolean isMaximize() {
		return mView.isMaximized();
	}

	public boolean isMinimize() {
		return mView.isMinimized();
	}

	/**
	 * Hook the DraggableListener to DraggablePanel to pause or resume the video
	 * when the DragglabePanel is maximized or closed.
	 */
	private void hookDraggablePanelListeners() {
		mView.setDraggableListener(new DraggableListener() {
			@Override
			public void onMaximized() {
				playVideo();
			}

			@Override
			public void onMinimized() {
				playVideo();
			}

			@Override
			public void onClosedToLeft() {
				pauseVideo();
			}

			@Override
			public void onClosedToRight() {
				pauseVideo();
			}
		});
	}

	/**
	 * Pause the video reproduced in the YouTubePlayer.
	 */
	private void pauseVideo() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.pause();
		}
	}

	/**
	 * Resume the video reproduced in the YouTubePlayer.
	 */
	private void playVideo() {
		if (mPlayer != null && !mPlayer.isPlaying()) {
			mPlayer.play();
		}
	}
}
