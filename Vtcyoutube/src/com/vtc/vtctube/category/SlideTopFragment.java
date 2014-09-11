package com.vtc.vtctube.category;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vtc.vtctube.R;
import com.vtc.vtctube.utils.Utils;

public final class SlideTopFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	public static ImageLoader imageLoader = null;

	public static SlideTopFragment newInstance(String content) {
		SlideTopFragment fragment = new SlideTopFragment();

		StringBuilder builder = new StringBuilder();
		builder.append(content).append(" ");
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(getActivity().getApplicationContext()));
		}

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
		Log.d("mContent", mContent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bannerlayout, container, false);
		ImageView imageView = (ImageView) view.findViewById(R.id.bannerid);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		imageLoader.displayImage(mContent, imageView,
				Utils.getOptions(getActivity()),
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// holder.spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// holder.spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						try {
							// WorldCupActivity.listNews.get(position)
							// .setImgThumailbm(loadedImage);
						} catch (Exception exception) {

						}
						// holder.spinner.setVisibility(View.GONE);
					}
				});
		return view;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
