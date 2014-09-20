package com.vtc.vtctube.services;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

public class AysnRequestHttp extends AsyncTask<String, Integer, String> {

	private int keyOption;
	private View process;
	private IResult iResult;
	private ViewGroup viewgruop;

	public AysnRequestHttp(ViewGroup viewgroup, int keyOption, View process,
			IResult iResult) {
		this.keyOption = keyOption;
		this.process = process;
		this.iResult = iResult;
		this.viewgruop = viewgroup;

	}

	public static String getUrlHttp(String host, String function) {
		return host + "/" + function;
	}

	@Override
	protected String doInBackground(String... params) {
		String json = "";
		JSONParser jsonParser = new JSONParser();
		try {
			json = jsonParser.makeHttpRequest(params[0]).toString();
		} catch (NetworkErrorException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void onPreExecute() {
		if (keyOption != Utils.AYSN_LOAD && process != null)
			process.setVisibility(View.VISIBLE);
		//Utils.disableEnableControls(false, viewgruop);
	}

	@Override
	protected void onPostExecute(String result) {
		//Utils.disableEnableControls(true, viewgruop);
		Log.d("result", result);
		if (process != null)
			process.setVisibility(View.GONE);
		this.iResult.getResult(keyOption, result);
	}
}