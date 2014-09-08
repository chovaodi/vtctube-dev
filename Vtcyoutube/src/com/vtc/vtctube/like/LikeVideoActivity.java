package com.vtc.vtctube.like;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.PinnedSectionListView;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LikeVideoActivity extends SherlockFragmentActivity {
	private PinnedAdapter adapter;
	private ListView listvideo;

	public static SmoothProgressBar smooth;
	private String queryResent;
	private ResultOnclickTab callBackOnlick;

	private List<ItemPost> listData = new ArrayList<ItemPost>();
	private int key;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		key = intent.getIntExtra("key", 0);

		setContentView(R.layout.category_layout);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.logo));
		getSupportActionBar().setTitle("Tìm kiếm");
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bgr_tasktop));

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);
		callBackOnlick = new ResultOnclickTab();
		adapter = new PinnedAdapter(LikeVideoActivity.this, callBackOnlick);
		listvideo = (ListView) findViewById(R.id.listvideo);
		((PinnedSectionListView) listvideo).setShadowVisible(false);

		addViewPost();

	}

	public class ResultOnclickTab implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int position, boolean isLike) {
			switch (type) {
			case PinnedAdapter.YEUTHICH:
				if (!isLike) {
					adapter.remove(listData.get(position));
				} else {
					adapter.getItem(position).setLike(isLike);
				}
				break;
			case PinnedAdapter.MOINHAT:
				Log.d("chovaodi", "111111111111");
				adapter.getItem(position).setLike(isLike);
				break;
			}

			adapter.notifyDataSetChanged();
		}

		@Override
		public void onCLickView(int type, String idYoutube) {
			Utils.getVideoView(idYoutube, LikeVideoActivity.this);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_in_bottom,
					R.anim.slide_out_bottom);
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
	}

	public void addViewPost() {
		String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		if (key == R.id.menu_video_yeuthich) {
			listData = Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH);

		} else {
			queryResent = "SELECT * FROM " + DatabaseHelper.TB_RESENT;
			listData = Utils.getVideoLocal(queryResent,
					PinnedAdapter.MOINHAT);
			listData = Utils.checkLikeVideo(listData,
					Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH));
		}

		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getStatus().equals("publish")) {
				listData.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(listData.get(i));
			}
		}

		listvideo.setAdapter(adapter);
	}

	public String getIdVideo(String content) {
		String[] value;
		try {
			value = content.split("data-video_id=");
			String[] data1 = value[1].split(" ");
			return data1[0].replace("\"", "");
		} catch (Exception e) {

		}
		return "";
	}

}
