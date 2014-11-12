package com.vtc.vtctube.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;

public class JSONParser {
	// private String formatDataError = "formatDataError";

	public JSONParser() {

	}

	/**
	 * dung de lam gi
	 * 
	 * @param url
	 * @return
	 * @throws NetworkErrorException
	 */
	public String makeHttpRequest(String url) {
		InputStream is = null;
		String json = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 10 * 000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (UnknownHostException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		} catch (ClientProtocolException e) {
			return "";
		} catch (IOException e) {
			return "";
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			new JSONObject(json);
		} catch (JSONException e1) {
			return "";
		} catch (Exception e) {
			return "";
		}

		return json;

	}

}
