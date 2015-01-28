package com.vtc.basetube.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private VideoAdapter mAdapterVideo = null;
	private ItemVideo itemVideo;
	private ProgressBar mLoading;

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
		//View footer = getActivity().getLayoutInflater().inflate(
			//	R.layout.fotter_detailt, null);

		mListvideo.addHeaderView(header);
		//mListvideo.addFooterView(footer); 
		LinearLayout option = (LinearLayout) header.findViewById(R.id.option);
		option.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.CreatePopupMenu(getActivity(), arg0, itemVideo);
			}
		});
		mTvTitle = (TextView) header.findViewById(R.id.lblName);
		mTvDescription = (TextView) header.findViewById(R.id.lblDescription);
		mTvPublishAt = (TextView) header.findViewById(R.id.lbPublishAt);
		mTvViewCount = (TextView) header.findViewById(R.id.lblCountview);
		mLoading=(ProgressBar)mView.findViewById(R.id.loading);

		MainActivity.lblMessage.setVisibility(View.GONE);
		Bundle bundle = this.getArguments();
		if (bundle == null) {
			return;
		}
		mVideoId = bundle.getString(Utils.EXTRA_VIDEO_ID);
		String playlistId = bundle.getString(Utils.EXTRA_PLAYLIST_ID);
		playVideo(mVideoId);
		displayRelatedVideo(mVideoId);
		mListvideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adt, View view, int pos,
					long id) {
				int index = pos - 1;
				if (index < 0) {
					return;
				}
				ItemVideo video = mAdapterVideo.getItem(index);
				playVideo(video.getId());
				updateDataDetail(video);
				displayRelatedVideo(video.getId());
			}
		});
	}

	public void playVideo(String videoId) {
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

	private void updateDataDetail(ItemVideo item) {
		itemVideo = item;
		mTvTitle.setText(item.getTitle());
		mTvDescription.setText(item.getDescription());
		mTvPublishAt.setText(Utils.getTime(item.getTime()));
		mTvViewCount.setText(item.getViewCount() + " lượt xem");
	}

	public void displayRelatedVideo(String videoId) {
		mLoading.setVisibility(View.VISIBLE);
		mController.requestRelatedVideos(getActivity(), videoId,
				new OnRequest<ArrayList<Category>>() {

					@Override
					public void onSuccess(ArrayList<Category> data) {
						mLoading.setVisibility(View.GONE);
						
						if (mAdapterVideo == null) {
							mAdapterVideo = new VideoAdapter(getActivity());
							mListvideo.setAdapter(mAdapterVideo);
						}
						mAdapterVideo.RemoveData();
						for (Category dt : data) {
							Log.d(Utils.TAG, "Data: Title: " + dt.getTitle());
							ItemVideo item = new ItemVideo();
							item.setTitle(dt.getTitle());
							item.setId(dt.getId());
							item.setTime(dt.getPublishAt());
							item.setViewCount(dt.getViewCount() + " lượt xem");
							item.setThumbnail(dt.getThumbnail());
							item.setPlaylistId(dt.getPlaylistId());
							item.setDescription(dt.getDescription());
							item.setDuration(dt.getDuration());
							mAdapterVideo.addItem(item);
							if (item.getId().equals(mVideoId)) {
								updateDataDetail(item);
							}
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
