package com.vtc.vtctube.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchAdapter extends ArrayAdapter<Item> {
	private int resource;
	private LayoutInflater layoutInflater;
	private Context context;

	public SearchAdapter(Context mContext, int resource, List<Item> models) {
		super(mContext, resource, models);
		this.resource = resource;
		layoutInflater = LayoutInflater.from(mContext);
		this.context = mContext;
	}

	public class ViewHolder {
		public TextView txtTitle;
		public ImageView imgIcon;
		public RatingBar ratingBar;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(resource, null, true);

			// viewHolder.imgIcon = (ImageView)
			// convertView.findViewById(R.id.icon);
			// viewHolder.txtTitle = (TextView)
			// convertView.findViewById(R.id.title);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Item item = getItem(position);
		// viewHolder.checkBox.setClickable(false);
		// viewHolder.name.setClickable(false);
		// viewHolder.userImg.setClickable(false);

		// viewHolder.txtTitle.setText(item.getTitle());
		// /viewHolder.imgIcon.setImageResource(item.getIcon());

		return convertView;
	}

}
