package com.vtc.xemtivi;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;
import com.vtc.xemtivi.R;

public class AcitivityLoadding extends Activity {
	public static ItemPost itemPost = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loadding);
		String url = "http://vtctube.vn/api/get_page?slug=vtctube-video-online";
		ResultCallBack callBack = new ResultCallBack();
		new AysnRequestHttp(null, Utils.LOAD_FIRST_DATA, MainActivity.smooth,
				callBack).execute(url);
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("resultresult", result);

			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				JSONObject page = jsonObj.getJSONObject("page");
				if (status.equals("ok")) {
					itemPost = new ItemPost();
					itemPost.setTitle(page.getString("title"));
					String[] content = page.getString("content")
							.split("embed/");
					String[] id = content[1].split("\\?");
					itemPost.setVideoId(id[0]);

				}

			} catch (Exception e) {
				itemPost = null;
			}
			Intent intent = new Intent(AcitivityLoadding.this,
					MainActivity.class);
			startActivity(intent);
			finish();
		}

		
	}
}
