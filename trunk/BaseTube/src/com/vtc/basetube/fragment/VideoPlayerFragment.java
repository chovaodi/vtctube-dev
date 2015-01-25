package com.vtc.basetube.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.pedrovgs.draggablepanel.DraggableListener;
import com.pedrovgs.draggablepanel.DraggableView;
import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.MainActivity;
import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.youtube.OnRequest;
import com.vtc.basetube.services.youtube.YoutubeController;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class VideoPlayerFragment extends YoutubePlayerFragment {
	private String mVideoId = "";

	public static ViewGroup mainView;

	private YouTubePlayerSupportFragment mYoutubeFragment;
	private YouTubePlayer mPlayer;

	private DraggableView mView;
	private BaseTubeApplication mApp;
	private ListView mListvideo;

	private TextView mTvTitle;
	private TextView mTvViewCount;
	private TextView mTvPublishAt;
	private TextView mTvDescription;

	private YoutubeController mController;
	private VideoAdapter mAdapterVideo;

	private static VideoPlayerFragment sInstance = null;

	public static VideoPlayerFragment newInstance() {
		if (sInstance == null)
			sInstance = new VideoPlayerFragment();

		return sInstance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnDisplayVideo) {
			mApp = ((OnDisplayVideo) activity).getTubeApplication();
			mController = new YoutubeController(mApp);
		}
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
				.replace(R.id.youtube_player_fragment, mYoutubeFragment)
				.addToBackStack(null).commit();
		View view = getView();
		mListvideo = (ListView) view.findViewById(R.id.listView1);
		View header = getActivity().getLayoutInflater().inflate(
				R.layout.header_itemvideo, null);
		View fotter = getActivity().getLayoutInflater().inflate(
				R.layout.fotter_detailt, null);

		mListvideo.addHeaderView(header);
	//	mListvideo.addFooterView(fotter);
		mTvTitle = (TextView) header.findViewById(R.id.lblName);
		mTvDescription = (TextView) header.findViewById(R.id.lblDescription);
		mTvPublishAt = (TextView) header.findViewById(R.id.lbPublishAt);
		mTvViewCount = (TextView) header.findViewById(R.id.lblCountview);

		mAdapterVideo = new VideoAdapter(getActivity());
		mListvideo.setAdapter(mAdapterVideo);
		MainActivity.lblMessage.setVisibility(View.GONE);

		// for (int i = 0; i < 10; i++) {
		// ItemVideo item = new ItemVideo();
		// item.setTitle("Demo" + i);
		// item.setId(i + "");
		// item.setUploader("QuangNinhTV");
		// item.setTime("");
		// item.setCountView("");
		// item.setThumbnail("https://lh5.googleusercontent.com/-t_AUpjgQDnU/VMCRQop2SAI/AAAAAAAACPk/V5uOHoCqqfQ/w426-h323/522054_381988935294614_2598873010533268703_n.jpg");
		// adapterVideo.addItem(item);
		// }
		// listvideo.setAdapter(adapterVideo);
		Bundle bundle = this.getArguments();
		if (bundle == null) {
			return;
		}
		mVideoId = bundle.getString("VIDEO_ID");
		updateData(mVideoId);
		String playlistId = bundle.getString("PLAYLIST_ID");
		updateList(playlistId);
	}

	public void updateData(String videoId) {
		Log.d(Utils.TAG, "VIDEO_ID: " + videoId);
		if (TextUtils.isEmpty(videoId)) {
			return;
		}
		mVideoId = videoId;
		if (mPlayer != null) {
			mPlayer.cueVideo(videoId);
		} else {
			mYoutubeFragment.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);
		}
		updateDataDetail();
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		this.mPlayer = player;
		Log.d(Utils.TAG, "videoId: " + mVideoId);
		if (!wasRestored && mVideoId.length() > 0) {
			player.cueVideo(mVideoId);
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

	private void updateDataDetail() {
		mController.requestVideoList(mApp, mVideoId,
				new OnRequest<ArrayList<Category>>() {

					@Override
					public void onSuccess(ArrayList<Category> data) {
						Category item = data.get(0);
						mTvTitle.setText(item.getTitle());
						mTvDescription.setText(item.getDescription());
						mTvPublishAt.setText(item.getPublishAt());
						mTvViewCount.setText(item.getViewCount() + " lượt xem");
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
	}

	public void updateList(String playlistId) {
		mController.requestPlaylistItems(getActivity(), playlistId,
				new OnRequest<ArrayList<Category>>() {

					@Override
					public void onSuccess(ArrayList<Category> data) {
						Log.d(Utils.TAG, "Data: " + data.size());
						mAdapterVideo.RemoveData();
						for (Category dt : data) {
							if (dt.getId().equals(mVideoId)) {
								continue;
							}
							Log.d(Utils.TAG, "Data: Title: " + dt.getTitle());
							ItemVideo item = new ItemVideo();
							item.setTitle(dt.getTitle());
							item.setId(dt.getId());
							item.setTime("3h trước");
							item.setUploader("QuangNinhTV");
							item.setCountView("30 lượt xem");
							item.setThumbnail(dt.getThumbnail());
							item.setPlaylistId(dt.getPlaylistId());
							// Log.d("dt.getThumbnail()",dt.getThumbnail());
							mAdapterVideo.addItem(item);
						}
						Log.d(Utils.TAG, "Data: mAdapterVideo: "
								+ mAdapterVideo.getCount());
						mAdapterVideo.notifyDataSetChanged();
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub
					}

				});
	}
}
