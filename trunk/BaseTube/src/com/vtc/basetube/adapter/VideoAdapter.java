package com.vtc.basetube.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;
import com.vtc.basetube.R;
import com.vtc.basetube.model.ItemVideo;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.BitmapLruCache;
import com.vtc.basetube.services.volley.util.BitmapUtil;

public class VideoAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private LayoutInflater mInflater;
	private Context context;
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private List<ItemVideo> mData = new ArrayList<ItemVideo>();
	private ImageLoader mImageLoader;

	public VideoAdapter(Context mContext) {
	    int max_cache_size = 1000000;
        mImageLoader = new ImageLoader(RequestManager.newInstance(mContext).getRequestQueue(), new BitmapLruCache(max_cache_size));
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

				holder.thumnail = (NetworkImageView) convertView
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
			holder.lblUploader.setText(item.getUploader());
			holder.lblMetadata.setText(item.getTime() + ","
					+ item.getCountView());
			if(item.getThumbnail() != null) {
    	         holder.thumnail.setImageUrl(item.getThumbnail(), mImageLoader);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView txtTitle;
		public TextView lblNameCate;
		public TextView lblXemthem;

		public NetworkImageView thumnail;
		public TextView lblUploader;
		public TextView lblMetadata;

	}
	public  class DiskBitmapCache extends DiskBasedCache implements ImageCache {
        
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
     
            return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
        }
     
        public void putBitmap(String url, Bitmap bitmap) {
            
            final Entry entry = new Entry();
            
/*          //Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
            // If w & h is set during image request ( using ImageLoader ) then this is not required.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
            
            downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            entry.data = data ; */
            
            entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
            put(url, entry);
        }
    }
}