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

import com.vtc.basetube.R;
import com.vtc.basetube.model.Item;

public class MenuLeftAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private LayoutInflater mInflater;
	private Context context;
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private List<Item> mData = new ArrayList<Item>();

	public MenuLeftAdapter(Context mContext) {
		mInflater = LayoutInflater.from(mContext);
		context = mContext;


	}

	public void addItem(final Item item) {
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

	public void addSeparatorItem(final Item item) {
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
	public Item getItem(int position) {
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

				convertView = mInflater
						.inflate(R.layout.drawer_list_item, null);
				holder.imgIcon = (ImageView) convertView
						.findViewById(R.id.icon);
				holder.submenu = (ImageView) convertView
						.findViewById(R.id.issubmenu);

				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.title);

				break;

			case TYPE_ITEM:

				convertView = mInflater.inflate(R.layout.item_sub_menu, null);
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.title);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Item item = getItem(position);
		if (type == TYPE_SEPARATOR) {
			holder.txtTitle.setText(item.getTitle());
			holder.imgIcon.setImageResource(item.getIcon());
			if (item.isSubmenu()) {
				holder.submenu.setVisibility(View.VISIBLE);
			}

		} else {
			holder.txtTitle.setText(item.getTitle());
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView txtTitle;
		public ImageView imgIcon;
		public ImageView submenu;

	}

}