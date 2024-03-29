package com.vtc.xemtivi;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;
import com.vtc.xemtivi.R;

public class FragmentNewfeed extends Fragment {
	private static FragmentNewfeed frament = null;
	private WebView webview_fbview;
	private ProgressBar progressBar1;

	public static FragmentNewfeed newInstance() {
		frament = new FragmentNewfeed();
		return frament;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.newfeed, container, false);
		webview_fbview = (WebView) view.findViewById(R.id.contentView);
		progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
		settingWebview(webview_fbview);

		ResultCallBack callBack = new ResultCallBack();
		MainActivity.smooth.setVisibility(View.VISIBLE);
		new AysnRequestHttp((ViewGroup) view, Utils.LOAD_FIRST_DATA, null,
				callBack)
				.execute("http://vtctube.vn/api/get_page?slug=thong-bao");
		return view;
	}

	private void settingWebview(WebView webview_fbview) {
		webview_fbview.getSettings().setJavaScriptEnabled(true);
		webview_fbview.setLongClickable(false);

		webview_fbview.getSettings().setLoadWithOverviewMode(true);
		webview_fbview.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);

		webview_fbview.getSettings().setUseWideViewPort(true);
		webview_fbview.requestFocus(View.FOCUS_DOWN);
		webview_fbview.setPadding(0, 0, 0, 0);
		webview_fbview.setWebChromeClient(new webChromeClient());
		webview_fbview.setInitialScale(100);
		webview_fbview.clearCache(true);
		webview_fbview.clearHistory();
		webview_fbview.getSettings().setDefaultFontSize(18);
		webview_fbview.addJavascriptInterface(new JavaScriptInterface(
				getActivity()), "Android");
		webview_fbview.setVisibility(View.VISIBLE);
		webview_fbview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100 && progressBar1.getVisibility() == View.GONE) {
					progressBar1.setVisibility(View.VISIBLE);
				}
				if (progress == 100) {
					progressBar1.setVisibility(View.GONE);
				}
			}
		});

	}

	private class webChromeClient extends WebChromeClient {

		// display alert message in Web View
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			new android.app.AlertDialog.Builder(view.getContext())
					.setMessage(message).setCancelable(true).show();
			result.confirm();
			return true;
		}
	}

	public class JavaScriptInterface {
		Context mContext;

		// Instantiate the interface and set the context
		JavaScriptInterface(Context c) {
			mContext = c;
		}

		// using Javascript to call the finish activity
		public void closeMyActivity() {
			// finish();
		}
	}

	private String getHtmlLink(String content) {
		String linkhtml = "<html>"
				+ "<meta name='viewport',content=\"width=device-width, initial-scale=1.0\">"
				+ "<head>"
				+ "<style type='text/css'>"

				+ "p { text-align: left; width: auto; }"
				+ "@font-face {"
				+ "font-family: MyFont;"
				+ "src: url('file:///android_asset/fonts/Roboto-Light.ttf')}"

				+ " </style>"
				+ "</head>"

				+ "<body style=\"margin:0; padding: 0; font-size:18px; font-family: MyFont!important;\">"
				+ "<div style='padding: 10px;'>" + content + "</div>"
				+ "</body></html>";

		return linkhtml;
	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			MainActivity.smooth.setVisibility(View.GONE);
			try {
				JSONObject jsonObject = new JSONObject(result);
				String msg = jsonObject.getJSONObject("page").getString(
						"content");
				webview_fbview.loadDataWithBaseURL("http://www.wc.vtc.vn/",
						getHtmlLink(msg), "text/html", "utf-8", null);
			} catch (Exception e) {
				MainActivity.lblError.setVisibility(View.VISIBLE);
			}
		}

		

	}
}
