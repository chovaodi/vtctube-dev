package com.vtc.basetube.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonRobotoNotBold extends Button {

	public ButtonRobotoNotBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ButtonRobotoNotBold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ButtonRobotoNotBold(Context context) {
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

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text.toString(), type);
	}

}
