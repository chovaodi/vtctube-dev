package com.vtc.vtctube.adpter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vtc.vtctube.R;
import com.vtc.vtctube.model.ItemCategory;

public class MenuHomeAdapter extends BaseAdapter {
	ViewHolder holder;
	Context mContext;
	List<ItemCategory> arr;

	public MenuHomeAdapter(Context mContext, List<ItemCategory> arr) {
		this.mContext = mContext;
		this.arr = arr;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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

		Picasso.with(mContext)
				.load("http://vtctube.vn/category-thumbs/"
						+ arr.get(position).getSlug() + ".png")
				.placeholder(R.drawable.error_home).into(holder.icon);

		return convertView;
	}

}
