package com.vtc.basetube.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vtc.basetube.MainActivity;
import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.utils.DatabaseHelper;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class FragmentLike extends Fragment {
	private static Fragment fragment = null;
	private OnDisplayVideo mOnDisplayVideo;

	public static Fragment newInstance() {
		if (fragment == null)
			fragment = new FragmentLike();

		return fragment;
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
		final List<ItemVideo> list = Utils.getVideoData("SELECT * FROM "
				+ DatabaseHelper.TB_DATA + " WHERE type='" + Utils.LIKE + "'",
				MainActivity.myDbHelper);
		MainActivity.lblMessage.setVisibility(View.GONE);
		if (list == null || list.size() == 0)
			MainActivity.lblMessage.setVisibility(View.VISIBLE);

		for (int i = 0; i < list.size(); i++) {
			ItemVideo item = new ItemVideo();
			item.setTitle(list.get(i).getTitle());
			item.setId(list.get(i).getId());
			item.setUploader("QuangNinhTV");
			item.setTime("");
			item.setViewCount("");
			item.setThumbnail(list.get(i).getThumbnail());
			adapterVideo.addItem(item);
		}
		listvideo.setAdapter(adapterVideo);
		listvideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mOnDisplayVideo != null) {
					mOnDisplayVideo.display(list.get(arg2));
				}
			}

		});
	}
}
