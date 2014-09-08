package com.vtc.vtctube.category;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;
import com.vtc.vtctube.R;
import com.vtc.vtctube.R.drawable;

public class SliderTopFragmentAdapter extends FragmentStatePagerAdapter
		implements IconPagerAdapter {
	
	private int mCount = 4;

	public SliderTopFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		try {
			if (mCount > position)
				return SlideTopFragment.newInstance("http://newsen.vn/data/cnn360/2014/3/10/tin-doc-quyen-hot-girl-kha-ngan-bo-facebook-500-nghin-fan-chinh-thuc-rut-khoi-showbiz.jpg");
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public int getIconResId(int index) {
		return R.drawable.perm_group_calendar;
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
}