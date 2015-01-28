package com.vtc.basetube.fragment;

import com.vtc.basetube.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentAbout extends Fragment {
	private static Fragment fragment = null;

	public static Fragment newInstance() {
		if (fragment == null)
			fragment = new FragmentAbout();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		TextView about = (TextView) view.findViewById(R.id.textView1);
		about.setText(Html.fromHtml(getResources().getString(R.string.aboutus)));
	}

}
