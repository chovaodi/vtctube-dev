package com.vtc.basetube.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.youtube.OnRequest;
import com.vtc.basetube.services.youtube.YoutubeController;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class FragmentCategory extends Fragment implements OnScrollListener {
	private static FragmentCategory fragment = null;
	private boolean isLoadding = false;
	private YoutubeController mController;
	private BaseTubeApplication mApp;
	private VideoAdapter mAdapterVideo;
	private OnDisplayVideo mOnDisplayVideo;

	public static FragmentCategory newInstance() {
		if (fragment == null)
			fragment = new FragmentCategory();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDisplayVideo) {
            mOnDisplayVideo = (OnDisplayVideo)activity;
            mApp = mOnDisplayVideo.getTubeApplication();
            mController = new YoutubeController(mApp);
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapterVideo = new VideoAdapter(getActivity());
		View view = getView();
		ListView listvideo = (ListView) view.findViewById(R.id.listivideo);
		Bundle bundle = this.getArguments();
		if(bundle != null) {
		    String playlistId = bundle.getString(Utils.EXTRA_PLAYLIST_ID);
		    updateList(playlistId);
		}
		listvideo.setAdapter(mAdapterVideo);
		listvideo.setOnScrollListener(this);
		listvideo.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adp, View view, int pos,
                    long id) {
                if (mOnDisplayVideo != null) {
                    mOnDisplayVideo.display(mAdapterVideo.getItem(pos));
                }
            }

        });
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if (lastInScreen == totalItemCount && !isLoadding) {
			isLoadding = true;

			// /call more api
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void updateList(String playlistId) {
        mController.requestPlaylistItems(getActivity(), playlistId,
                new OnRequest<ArrayList<Category>>() {

                    @Override
                    public void onSuccess(ArrayList<Category> data) {
                        Log.d(Utils.TAG, "Data: " + data.size());
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
