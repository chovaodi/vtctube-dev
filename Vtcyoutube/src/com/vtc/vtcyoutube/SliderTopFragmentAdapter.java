package com.vtc.vtcyoutube;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.facebook.android.Util;
import com.viewpagerindicator.IconPagerAdapter;

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
				return TestFragment.newInstance("http://anhdep.pro/wp-content/uploads/2014/06/tuyen-tap-bo-hinh-nen-hoa-nhai-dep-nhat-cho-may-tinh-4.jpg");
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