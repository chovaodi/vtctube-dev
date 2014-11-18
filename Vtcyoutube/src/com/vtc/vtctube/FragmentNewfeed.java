package com.vtc.vtctube;

import org.json.JSONObject;

import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentNewfeed extends Fragment {
	private static FragmentNewfeed frament = null;
	private TextView lblTextDesc;

	public static FragmentNewfeed newInstance() {
		if (frament == null)
			frament = new FragmentNewfeed();
		return frament;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.aboutus, container, false);
		lblTextDesc = (TextView) view.findViewById(R.id.lblTextDesc);

		ResultCallBack callBack = new ResultCallBack();
		new AysnRequestHttp((ViewGroup) view, Utils.LOAD_FIRST_DATA, null,
				callBack)
				.execute("http://vtctube.vn/api/get_page?slug=thong-bao");
		return view;
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String msg = jsonObject.getJSONObject("page").getString(
						"content");
				lblTextDesc.setText(Html.fromHtml(msg));
			} catch (Exception e) {
				MainActivity.lblError.setVisibility(View.VISIBLE);
			}
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
}
