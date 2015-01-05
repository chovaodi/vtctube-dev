package com.vtc.basetube.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vtc.basetube.R;
import com.vtc.basetube.model.Item;
import com.vtc.basetube.model.ItemVideo;

public class VideoAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private LayoutInflater mInflater;
	private Context context;
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private List<ItemVideo> mData = new ArrayList<ItemVideo>();

	public VideoAdapter(Context mContext) {
		mInflater = LayoutInflater.from(mContext);
		context = mContext;

	}

	public void addItem(final ItemVideo item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		for (int i = 9; i >= position; i--) {
			mData.remove(i);
		}
	}

	public void RemoveData() {
		mData.removeAll(mData);
		mSeparatorsSet.removeAll(mSeparatorsSet);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(final ItemVideo item) {
		mData.add(item);
		mSeparatorsSet.add(mData.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public ItemVideo getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_SEPARATOR:

				convertView = mInflater.inflate(R.layout.item_separater_video,
						null);

				holder.lblNameCate = (TextView) convertView
						.findViewById(R.id.lblNameCate);

				holder.lblXemthem = (TextView) convertView
						.findViewById(R.id.lblXemthem);

				break;

			case TYPE_ITEM:

				convertView = mInflater.inflate(R.layout.item_video, null);
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.lblTitle);
				holder.lblUploader = (TextView) convertView
						.findViewById(R.id.lblUploader);
				holder.lblMetadata = (TextView) convertView
						.findViewById(R.id.lblMetadata);

				holder.thumnail = (ImageView) convertView
						.findViewById(R.id.thumnail);

				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ItemVideo item = getItem(position);
		if (type == TYPE_SEPARATOR) {
			holder.lblNameCate.setText(item.getTitle().toUpperCase());

		} else {
			holder.txtTitle.setText(item.getTitle());

//			Picasso.with(context)
//					.load("http://img.youtube.com/vi/" + item.getId()
//							+ "/maxresdefault.jpg")
//					.placeholder(R.drawable.ic_launcher).into(holder.thumnail);
			holder.lblUploader.setText(item.getUploader());
			holder.lblMetadata.setText(item.getTime() + ","
					+ item.getCountView());

		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView txtTitle;
		public TextView lblNameCate;
		public TextView lblXemthem;

		public ImageView thumnail;
		public TextView lblUploader;
		public TextView lblMetadata;

	}

}