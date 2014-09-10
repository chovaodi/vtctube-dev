package com.vtc.vtctube.like;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.vtc.vtctube.R;

public class CommentAcitivity extends Activity {
	private WebView webview_fbview;
	private ProgressBar loaddingcmt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
		setContentView(R.layout.comment);
		loaddingcmt = (ProgressBar) findViewById(R.id.loading);
		webview_fbview = (WebView) findViewById(R.id.contentView);
		settingWebView();
		loadComment("http://www.haivl.com/photo/4528925");
		// webview_fbview.loadDataWithBaseURL("http://www.haivl.com/photo/4528925",
		// getHtmlLink("http://www.haivl.com/photo/4528925"), "text/html",
		// "utf-8", null);
		//

	}

	private void settingWebView() {
		webview_fbview.getSettings().setJavaScriptEnabled(true);
		webview_fbview.setLongClickable(false);
		webview_fbview.getSettings().setBuiltInZoomControls(false);
		webview_fbview.getSettings().setLoadWithOverviewMode(true);
		webview_fbview.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);

		webview_fbview.getSettings().setUseWideViewPort(true);
		webview_fbview.requestFocus(View.FOCUS_DOWN);
		webview_fbview.setPadding(0, 0, 0, 0);
		webview_fbview.setWebViewClient(new webViewClient());
		webview_fbview.setWebChromeClient(new webChromeClient());
		webview_fbview.setInitialScale(100);
		webview_fbview.clearCache(true);
		webview_fbview.clearHistory();
		webview_fbview.getSettings().setDefaultFontSize(14);
		webview_fbview.addJavascriptInterface(new JavaScriptInterface(
				CommentAcitivity.this), "Android");

		webview_fbview.setVisibility(View.VISIBLE);
		webview_fbview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100
						&& loaddingcmt.getVisibility() == ProgressBar.GONE) {
					loaddingcmt.setVisibility(ProgressBar.VISIBLE);
				}
				// Pbar.setProgress(progress);
				if (progress == 100) {
					loaddingcmt.setVisibility(ProgressBar.GONE);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);

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

	private void loadComment(String url) {
		String script = "<div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&appId=648492845199272&version=v2.0\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>";

		String commentBox = "<div class=\"fb-comments\" data-href=\"" + url
				+ "\" data-numposts=\"30\" data-colorscheme=\"light\"></div>";

		String html = "<html><head><style type='text/css'>img { max-width: 100%%; width: auto; height: auto; } p { text-align: justify; width: auto; } </style></head><body style=\"margin: 0; padding: 0\">"
				+ script + commentBox + "</body></html>";

		webview_fbview.loadDataWithBaseURL("http://9gag.tv", html, "text/html",
				null, null);
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

	private class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			webview_fbview.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			int pos = url.indexOf("code=");
			if (pos > 0) {
				// webView.loadDataWithBaseURL("", WEB_DATA_LOADING,
				// "text/html",
				// "UTF-8", "");
				// String tmp = url.split("&")[0];
				// String code = tmp.substring(pos + 5);

			} else {
				try {
					// dialogLoading.dismiss();
				} catch (Exception exception) {
				}
			}
			// loaddingcmt.setVisibility(View.GONE);

			super.onPageFinished(view, url);
		}
	}
}
