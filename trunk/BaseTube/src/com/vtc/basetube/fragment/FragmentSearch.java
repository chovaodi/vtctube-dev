package com.vtc.basetube.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.youtube.OnRequest;
import com.vtc.basetube.services.youtube.YoutubeController;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class FragmentSearch extends Fragment {
	private static FragmentSearch fragment = null;
	private String mQuerry;
	private OnDisplayVideo mOnDisplayVideo;
	private YoutubeController mController;
    private BaseTubeApplication mApp;
    private VideoAdapter mAdapterVideo;
    private TextView lblMessage;
    private ProgressBar loadding;

	public static FragmentSearch newInstance(String querry) {
		if (fragment == null) {
			fragment = new FragmentSearch();
    		Bundle args = new Bundle();
    		args.putString(Utils.EXTRA_QUERRY, querry);
    		fragment.setArguments(args);
		}
		return fragment;
	}

	public void updateValue(String txtSearch) {
		this.mQuerry = txtSearch;
		lblMessage.setVisibility(View.GONE);
		loadding.setVisibility(View.VISIBLE);
		 mController.requestSearchVideos(mApp.getApplicationContext(), mQuerry,
	                new OnRequest<ArrayList<Category>>() {

	                    @Override
	                    public void onSuccess(ArrayList<Category> data) {
	                    	loadding.setVisibility(View.GONE);
	                    	if(data.size()==0){
	                    		lblMessage.setVisibility(View.VISIBLE);
	                    		lblMessage.setText("Không tìm nội dung này!");
	                    	}
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
	                            item.setPlaylistId(dt.getPlaylistId());
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mQuerry = (String) (getArguments() != null ? getArguments()
				.getString(Utils.EXTRA_QUERRY) : 1);

	}

	@Override
	public void onAttach(Activity activity) {
	    if (activity instanceof OnDisplayVideo) {
            mOnDisplayVideo = (OnDisplayVideo)activity;
            mApp = mOnDisplayVideo.getTubeApplication();
            mController = new YoutubeController(mApp);
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

		mAdapterVideo = new VideoAdapter(getActivity());
		View view = getView();
		loadding=(ProgressBar)view.findViewById(R.id.loadding_home);
		lblMessage=(TextView)view.findViewById(R.id.lblThongbao);
		
		ListView listvideo = (ListView) view.findViewById(R.id.listivideo);

		listvideo.setAdapter(mAdapterVideo);
		listvideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (mOnDisplayVideo != null) {
					mOnDisplayVideo.display(mAdapterVideo.getItem(pos));
				}
			}

		});

		updateValue(mQuerry);
	}

}
