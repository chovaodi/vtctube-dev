package com.vtc.basetube.fragment;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vtc.basetube.R;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.utils.DatabaseHelper;
import com.vtc.basetube.utils.OnDisplayVideo;
import com.vtc.basetube.utils.Utils;

public class FragmentLike extends Fragment {
	private static Fragment fragment = null;
	private OnDisplayVideo mOnDisplayVideo;
	private ProgressBar loadding;
	private TextView lblMessage;
	private DatabaseHelper myDbHelper;

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
		myDbHelper = new DatabaseHelper(getActivity());
		try {
			myDbHelper.createDataBase();
			myDbHelper.openDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			throw sqle;
		}
		VideoAdapter adapterVideo = new VideoAdapter(getActivity());
		View view = getView();
		lblMessage = (TextView) view.findViewById(R.id.lblThongbao);
		loadding = (ProgressBar) view.findViewById(R.id.loadding_home);
		loadding.setVisibility(View.VISIBLE);
		ListView listvideo = (ListView) view.findViewById(R.id.listivideo);
		final List<ItemVideo> list = DatabaseHelper.getVideoData(
				"SELECT * FROM " + DatabaseHelper.TB_DATA + " WHERE type='"
						+ Utils.LIKE + "'", myDbHelper);
		loadding.setVisibility(View.GONE);
		if (list == null || list.size() == 0)
			lblMessage.setVisibility(View.VISIBLE);

		for (int i = 0; i < list.size(); i++) {
			ItemVideo item = new ItemVideo();
			item.setTitle(list.get(i).getTitle());
			item.setId(list.get(i).getId());
			item.setUploader("QuangNinhTV");
			item.setTime(list.get(i).getTime());
			item.setViewCount(list.get(i).getViewCount());
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
