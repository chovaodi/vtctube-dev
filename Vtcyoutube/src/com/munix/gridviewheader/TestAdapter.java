package com.munix.gridviewheader;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vtc.vtcyoutube.ItemCategory;
import com.vtc.vtcyoutube.R;
import com.vtc.vtcyoutube.connectserver.Utils;

public class TestAdapter extends BaseAdapter {
	ViewHolder holder;
	Context mContext;
	List<ItemCategory> arr;
	private ImageLoader imageLoader;

	public TestAdapter(Context mContext, List<ItemCategory> arr) {
		this.mContext = mContext;
		this.arr = arr;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext
				.getApplicationContext()));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ViewHolder {
		public TextView text;
		public ImageView icon;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// Utilities.log("convert null " + position);
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_home, null);
			holder.text = (TextView) convertView.findViewById(R.id.lblTitle);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			// Utilities.log("convert not null " + position);
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(arr.get(position).getTitle());
		imageLoader.displayImage("http://vtctube.vn/category-thumbs/"
				+ arr.get(position).getSlug() + ".png", holder.icon,
				Utils.getOptions(mContext), new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
					}
				});
		return convertView;
	}

}
