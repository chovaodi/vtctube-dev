package com.vtc.basetube.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.vtc.basetube.MainActivity;
import com.vtc.basetube.R;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.BitmapLruCache;
import com.vtc.basetube.services.volley.util.BitmapUtil;
import com.vtc.basetube.utils.DatabaseHelper;
import com.vtc.basetube.utils.ICategoryMore;
import com.vtc.basetube.utils.Utils;

public class VideoAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private LayoutInflater mInflater;
	private Context context;
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private List<ItemVideo> mData = new ArrayList<ItemVideo>();
	private ImageLoader mImageLoader;
	private ICategoryMore iCategoryMore = null;

	public VideoAdapter(Context mContext) {
		int max_cache_size = 1000000;
		mImageLoader = new ImageLoader(RequestManager.newInstance(mContext)
				.getRequestQueue(), new BitmapLruCache(max_cache_size));
		mInflater = LayoutInflater.from(mContext);
		context = mContext;
		if (context instanceof ICategoryMore)
			iCategoryMore = (ICategoryMore) context;

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
				holder.lineMore = (LinearLayout) convertView
						.findViewById(R.id.lineMore);

				break;

			case TYPE_ITEM:

				convertView = mInflater.inflate(R.layout.item_video, null);
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.lblTitle);
				holder.lblThoigian = (TextView) convertView
						.findViewById(R.id.lblThoigian);
				holder.lblUploader = (TextView) convertView
						.findViewById(R.id.lblUploader);
				holder.lblMetadata = (TextView) convertView
						.findViewById(R.id.lblMetadata);

				holder.thumnail = (NetworkImageView) convertView
						.findViewById(R.id.thumnail);
				holder.option = (LinearLayout) convertView
						.findViewById(R.id.option);

				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ItemVideo item = getItem(position);
		if (type == TYPE_SEPARATOR) {
			holder.lblNameCate.setText(item.getTitle().toUpperCase());
			holder.lineMore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					iCategoryMore.viewAll(121);// category Id
				}
			});
		} else {
			holder.lblThoigian.setText("2:40:10");
			holder.txtTitle.setText(item.getTitle());
			holder.lblPublishAt.setText(Utils.getTime(item.getTime()));
			holder.lblMetadata.setText(item.getCountView());
			if (item.getThumbnail() != null) {
				holder.thumnail.setImageUrl(item.getThumbnail(), mImageLoader);
			}

			holder.option.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					CreatePopupMenu(arg0, item);
				}
			});
		}
		return convertView;
	}

	public void CreatePopupMenu(View v, final ItemVideo itemvd) {

		PopupMenu mypopupmenu = new PopupMenu(context, v);

		MenuInflater inflater = mypopupmenu.getMenuInflater();

		inflater.inflate(R.menu.popup_menu, mypopupmenu.getMenu());

		mypopupmenu.show();
		mypopupmenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				int id = item.getItemId();
				switch (id) {
				case R.id.popup_like:

					if (MainActivity.myDbHelper.getCountRow("SELECT * FROM "
							+ DatabaseHelper.TB_DATA + " WHERE videoId='"
							+ itemvd.getId() + "' and type='" + Utils.LIKE
							+ "'") == 0) {
						MainActivity.myDbHelper.insertVideoLike(itemvd,
								Utils.LIKE);
						Toast.makeText(context, "Thêm vào danh sách yêu thích",
								Toast.LENGTH_LONG).show();
					}

					break;
				case R.id.popup_share:
					Utils.shareButton(itemvd, context);
					break;
				}
				return false;
			}
		});

	}

	public static class ViewHolder {
		public TextView txtTitle;
		public TextView lblNameCate;
		public TextView lblXemthem;

		public NetworkImageView thumnail;
		public TextView lblPublishAt;
		public TextView lblMetadata;
		public TextView lblThoigian;
		public LinearLayout option;
		public LinearLayout lineMore;
	}

	public class DiskBitmapCache extends DiskBasedCache implements ImageCache {

		public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
			super(rootDirectory, maxCacheSizeInBytes);
		}

		public DiskBitmapCache(File cacheDir) {
			super(cacheDir);
		}

		public Bitmap getBitmap(String url) {
			final Entry requestedItem = get(url);

			if (requestedItem == null)
				return null;

			return BitmapFactory.decodeByteArray(requestedItem.data, 0,
					requestedItem.data.length);
		}

		public void putBitmap(String url, Bitmap bitmap) {

			final Entry entry = new Entry();

			/*
			 * //Down size the bitmap.If not done, OutofMemoryError occurs while
			 * decoding large bitmaps. // If w & h is set during image request (
			 * using ImageLoader ) then this is not required.
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); Bitmap
			 * downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
			 * 
			 * downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos); byte[]
			 * data = baos.toByteArray(); entry.data = data ;
			 */

			entry.data = BitmapUtil.convertBitmapToBytes(bitmap);
			put(url, entry);
		}
	}
}