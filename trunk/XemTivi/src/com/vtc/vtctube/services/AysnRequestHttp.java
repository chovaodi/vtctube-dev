package com.vtc.vtctube.services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;
import com.vtc.xemtivi.MainActivity;

public class AysnRequestHttp extends AsyncTask<String, Integer, String> {

	private int keyOption;
	private View process;
	private IResult iResult;

	public AysnRequestHttp(ViewGroup viewgroup, int keyOption, View process,
			IResult iResult) {
		this.keyOption = keyOption;
		this.process = process;
		this.iResult = iResult;

	}

	public static String getUrlHttp(String host, String function) {
		return host + "/" + function;
	}

	@Override
	protected String doInBackground(String... params) {
		String json = "";
		JSONParser jsonParser = new JSONParser();
		json = jsonParser.makeHttpRequest(params[0]).toString();
		return json;
	}

	@Override
	protected void onPreExecute() {
		if (keyOption != Utils.AYSN_LOAD && process != null)
			MainActivity.progressBar.setVisibility(View.VISIBLE);
		// Utils.disableEnableControls(false, viewgruop);
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d("result",result.length()+"222");
		if (process != null)
			MainActivity.progressBar.setVisibility(View.GONE);
		this.iResult.getResult(keyOption, result);
	}
}