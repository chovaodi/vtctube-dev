package com.vtc.xemtivi;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;
import com.vtc.xemtivi.R;

public class FragmentAbout extends Fragment {
	private static FragmentAbout frament = null;
	private TextView lblTextDesc;

	public static FragmentAbout newInstance() {
		if (frament == null)
			frament = new FragmentAbout();
		return frament;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.aboutus, container, false);
		lblTextDesc = (TextView) view.findViewById(R.id.lblTextDesc);
		ResultCallBack callBack = new ResultCallBack();
		MainActivity.smooth.setVisibility(View.VISIBLE);
		new AysnRequestHttp((ViewGroup) view, Utils.LOAD_FIRST_DATA, null,
				callBack)
				.execute("http://vtctube.vn/api/get_page?slug=gioi-thieu");
		return view;
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			MainActivity.smooth.setVisibility(View.GONE);
			try {
				JSONObject jsonObject = new JSONObject(result);
				String msg = jsonObject.getJSONObject("page").getString(
						"content");
				lblTextDesc.setText(Html.fromHtml(msg));
			} catch (Exception e) {
				MainActivity.lblError.setVisibility(View.VISIBLE);
			}
		}

	

	}
}
