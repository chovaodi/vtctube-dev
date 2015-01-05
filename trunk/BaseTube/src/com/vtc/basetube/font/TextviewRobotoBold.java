package com.vtc.basetube.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextviewRobotoBold extends TextView {

	public TextviewRobotoBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextviewRobotoBold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextviewRobotoBold(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			try {
				Typeface tf = Typeface.createFromAsset(
						getContext().getAssets(), "fonts/Roboto-Bold.ttf");
				setTypeface(tf);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
