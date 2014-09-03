package com.vtc.vtcyoutube;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;

public class CategoryActivity extends SherlockFragmentActivity {
	private View header;
	private ViewPager pager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);
		
		ListView listvideo=(ListView)findViewById(R.id.listvideo);
		header =getLayoutInflater().inflate(
				R.layout.fragment_home, null);
		listvideo.addHeaderView(header);
		listvideo.setAdapter(null);
		pager = (ViewPager) header.findViewById(R.id.pager);
		SliderTopFragmentAdapter adapterPg = new SliderTopFragmentAdapter(
				getSupportFragmentManager());

		pager.setAdapter(adapterPg);

		PageIndicator mIndicator = (PageIndicator) header
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);
	}
}
