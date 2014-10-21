package com.vtc.vtctube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentAbout extends Fragment {
	private static FragmentAbout frament = null;

	public static FragmentAbout newInstance() {
		if (frament == null)
			frament = new FragmentAbout();
		return frament;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.aboutus, container, false);
		TextView lblTextDesc = (TextView) view.findViewById(R.id.lblTextDesc);
		lblTextDesc.setText(Html.fromHtml(getActivity().getResources()
				.getString(R.string.aboutus)));
		return view;
	}
}
