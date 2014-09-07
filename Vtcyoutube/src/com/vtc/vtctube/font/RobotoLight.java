package com.vtc.vtctube.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLight extends TextView {

	public RobotoLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RobotoLight(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RobotoLight(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			try {
				Typeface tf = Typeface.createFromAsset(
						getContext().getAssets(), "fonts/Roboto-Light.ttf");
				setTypeface(tf);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
