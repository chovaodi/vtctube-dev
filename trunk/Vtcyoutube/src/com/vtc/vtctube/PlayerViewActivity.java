/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.vtc.vtctube;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 */
public class PlayerViewActivity extends YouTubeFailureRecoveryActivity {
	String videoId = "";
	private String title;
	private Button btnLienquan;
	private Button btnChitiet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
	

		setContentView(R.layout.playerview_demo);

		Intent intent = getIntent();
		videoId = intent.getStringExtra("videoId");
		title = intent.getStringExtra("title");
		getActionBar().setTitle(title);
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);

		btnLienquan = (Button) findViewById(R.id.btnLienquan);
		btnChitiet = (Button) findViewById(R.id.btnChitiet);

		btnLienquan.setSelected(true);
		btnChitiet.setSelected(false);

		btnLienquan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				btnLienquan.setSelected(true);
				btnChitiet.setSelected(false);

			}
		});

		btnChitiet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				btnLienquan.setSelected(true);
				btnChitiet.setSelected(false);

			}
		});
		ResultOnclikTab callBackOnlick = new ResultOnclikTab();
		PinnedAdapter adapter = new PinnedAdapter(this, callBackOnlick);
		ListView listvideo = (ListView) findViewById(R.id.listvideo);
		String queryVideoLocal = "SELECT * FROM tblListVideo where cateId='"
				+ MainActivity.currentCate + "' and videoId !='"+videoId+"'";
		List<ItemPost> listData = Utils.getVideoLocal(
				DatabaseHelper.TB_LISTVIDEO, queryVideoLocal, 0);

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
		}
		listvideo.setAdapter(adapter);

	}

	public class ResultOnclikTab implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(videoId);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);

	}

}
