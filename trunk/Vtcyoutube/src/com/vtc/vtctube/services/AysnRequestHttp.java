package com.vtc.vtctube.services;

import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.view.View;

public class AysnRequestHttp extends AsyncTask<String, Integer, String> {

	private int keyOption;
	private View process;
	private IResult iResult;

	public AysnRequestHttp(int keyOption, View process, IResult iResult) {
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
	}

	@Override
	protected void onPostExecute(String result) {
		if (process != null)
			process.setVisibility(View.GONE);
		this.iResult.getResult(keyOption, result);
	}
}