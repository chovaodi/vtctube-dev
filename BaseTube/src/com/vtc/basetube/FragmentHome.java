/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vtc.basetube;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.vtc.basetube.adapter.VideoAdapter;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.youtube.OnRequest;
import com.vtc.basetube.services.youtube.YoutubeController;
import com.vtc.basetube.utils.OnDisplayVideo;

public class FragmentHome extends SherlockFragment {
    private ListView listvideo;
    private View view;
    private VideoAdapter adapterVideo = null;
    private OnDisplayVideo mOnDisplayVideo;
    private final YoutubeController mController;

    private static FragmentHome framgnent = null;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static FragmentHome newInstance(BaseTubeApplication app) {
        if (framgnent == null)
            framgnent = new FragmentHome(app);
        return framgnent;
    }

    public FragmentHome(BaseTubeApplication app) {
        mController = new YoutubeController(app);
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnDisplayVideo) {
            mOnDisplayVideo = (OnDisplayVideo) activity;
        }
        super.onAttach(activity);
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listvideo = (ListView) view.findViewById(R.id.listivideo);
		View header = getActivity().getLayoutInflater().inflate(
				R.layout.header_home, null);
		ImageView imgBanner=(ImageView)header.findViewById(R.id.imgBanner);
		
		listvideo.addHeaderView(header);
		addview();
		listvideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adp, View view, int pos,
					long id) {
			    int index = pos-1;
				if (mOnDisplayVideo != null && index >= 0) {
					mOnDisplayVideo.display(adapterVideo.getItem(index));
				}
			}

		});

	}

    public void addview() {
        mController.requestPlaylists(new OnRequest<ArrayList<Category>>() {

            @Override
            public void onSuccess(ArrayList<Category> data) {
                setUpAdapter(data);
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        });
    }

    public void setUpAdapter(ArrayList<Category> data) {
        if (adapterVideo == null)
            adapterVideo = new VideoAdapter(getActivity());
        adapterVideo.RemoveData();
        for (int i = 0; i < data.size(); i++) {
            final Category cat = data.get(i);

            mController.requestPlaylistItems(getActivity(), cat.getId(), new OnRequest<ArrayList<Category>>() {

                @Override
                public void onSuccess(ArrayList<Category> data) {
                    MainActivity.progressBar.setVisibility(View.GONE);
                    ItemVideo itemCate = new ItemVideo();
                    itemCate.setTitle(cat.getTitle());
                    adapterVideo.addSeparatorItem(itemCate);
                    for (Category dt : data) {
                        ItemVideo item = new ItemVideo();
                        item.setTitle(dt.getTitle());
                        item.setId(dt.getId());
                        item.setTime(dt.getPublishAt());
                        item.setViewCount(dt.getViewCount()+" lượt xem");
                        item.setThumbnail(dt.getThumbnail());
                        item.setPlaylistId(dt.getPlaylistId());
                        // Log.d("dt.getThumbnail()",dt.getThumbnail());
                        adapterVideo.addItem(item);
                    }
                    listvideo.setAdapter(adapterVideo);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub
                }

            });
        }

    }
}
