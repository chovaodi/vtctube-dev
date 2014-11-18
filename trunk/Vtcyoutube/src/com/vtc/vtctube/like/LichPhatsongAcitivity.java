package com.vtc.vtctube.like;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.vtc.vtctube.R;

public class LichPhatsongAcitivity extends SherlockActivity {
	private String content, title;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
		setContentView(R.layout.comment);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.icon_back_lps));
		getSupportActionBar().setTitle("Lịch phát sóng");

		// webview_fbview.loadDataWithBaseURL("http://www.haivl.com/photo/4528925",
		// getHtmlLink("http://www.haivl.com/photo/4528925"), "text/html",
		// "utf-8", null);
		//

		Intent intent = getIntent();
		content = intent.getStringExtra("content");
		title = intent.getStringExtra("title");
		
		TextView lblContent = (TextView) findViewById(R.id.content);
		lblContent.setText(Html.fromHtml("<b>"+title+"</b><br>"+content));

		// loadComment();
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_in_bottom,
					R.anim.slide_out_bottom);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_bottom,
				R.anim.slide_out_bottom);
	}

	

	// private void loadComment() {
	// String script =
	// "<div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&appId=648492845199272&version=v2.0\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>";
	//
	// String commentBox = "<div class=\"fb-comments\" data-href=\""
	// + "http://haivainoi.com/photo/2694"
	// + "\" data-numposts=\"30\" data-colorscheme=\"light\"></div>";
	//
	// String html =
	// "<html><head><style type='text/css'>img { max-width: 100%%; width: auto; height: auto; } p { text-align: justify; width: auto; } </style></head><body style=\"margin: 0; padding: 0\">"
	// + script + commentBox + "</body></html>";
	//
	// webview_fbview.loadDataWithBaseURL("http://www.24h.com.vn/", html,
	// "text/html", "UTF-8", null);
	// }

}
