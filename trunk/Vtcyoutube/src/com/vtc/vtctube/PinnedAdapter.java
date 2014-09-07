package com.vtc.vtctube;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vtc.vtctube.PinnedSectionListView.PinnedSectionListAdapter;
import com.vtc.vtctube.connectserver.IResult;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.utils.Utils;

class PinnedAdapter extends ArrayAdapter<ItemPost> implements
		PinnedSectionListAdapter {
	ViewHolder holder = null;
	private LayoutInflater mInflater;
	public static final int ITEM = 0;
	public static final int SECTION = 1;

	public static final int MOINHAT = 1;
	public static final int XEMNHIEU = 2;
	public static final int YEUTHICH = 3;

	private Context context;

	public static ImageLoader imageLoader = null;
	private List<ItemPost> mData = new ArrayList<ItemPost>();
	private IResult callBack;

	public PinnedAdapter(Context context, IResult callBack) {
		super(context, 0);
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.callBack = callBack;

		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context
					.getApplicationContext()));
		}

	}

	public void addItem(final ItemPost item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case SECTION:

				convertView = mInflater.inflate(R.layout.header_pinned, null);
				holder.btnMoinhat = (Button) convertView
						.findViewById(R.id.button1);
				holder.btnXemnhieu = (Button) convertView
						.findViewById(R.id.button2);
				holder.btnYeuthich = (Button) convertView
						.findViewById(R.id.button3);

				break;

			case ITEM:

				convertView = mInflater.inflate(R.layout.item_post, null);
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.lblTitlePost);
				holder.imgIcon = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.btnComment = (LinearLayout) convertView
						.findViewById(R.id.btnComment);

				holder.btnLike = (LinearLayout) convertView
						.findViewById(R.id.btnLike);
				holder.btnShare = (LinearLayout) convertView
						.findViewById(R.id.btnShare);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (type == SECTION) {
			Log.d("position", position + "chovaodi");
			final ItemPost item = getItem(position);
			if (item.getOption() == MOINHAT) {
				holder.btnMoinhat.setSelected(true);
				holder.btnXemnhieu.setSelected(false);
				holder.btnYeuthich.setSelected(false);
			} else if (item.getOption() == XEMNHIEU) {
				holder.btnMoinhat.setSelected(false);
				holder.btnXemnhieu.setSelected(true);
				holder.btnYeuthich.setSelected(false);
			} else {
				holder.btnMoinhat.setSelected(false);
				holder.btnXemnhieu.setSelected(false);
				holder.btnYeuthich.setSelected(true);
			}

			holder.btnMoinhat.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					callBack.getResult(PinnedAdapter.MOINHAT, "");
				}
			});
			holder.btnXemnhieu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					callBack.getResult(PinnedAdapter.XEMNHIEU, "");
				}
			});
			holder.btnYeuthich.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					callBack.getResult(PinnedAdapter.YEUTHICH, "");
				}
			});

		} else {
			final ItemPost item = getItem(position);
			holder.txtTitle.setText(item.getTitle());
			imageLoader.displayImage(item.getUrl(), holder.imgIcon,
					Utils.getOptions(context),
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
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							try {
								// WorldCupActivity.listNews.get(position)
								// .setImgThumailbm(loadedImage);
							} catch (Exception exception) {

							}
							// holder.spinner.setVisibility(View.GONE);
						}
					});

			holder.btnLike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_LIKE
							+ " WHERE id='" + item.getIdPost() + "'";
					Log.d("sqlCheck",sqlCheck);
					if (MainActivity.myDbHelper.getCountRow(
							DatabaseHelper.TB_LIKE, sqlCheck) == 0) {
						MainActivity.myDbHelper.insertVideoLike(
								item.getIdPost(), item.getCateId(),
								item.getVideoId(), item.getUrl(),
								item.getStatus(), item.getTitle());
					}
				}
			});

		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).type;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == SECTION;
	}

	public class ViewHolder {
		public TextView txtTitle;
		public ImageView imgIcon;
		public ImageView submenu;

		public Button btnMoinhat;
		public Button btnXemnhieu;
		public Button btnYeuthich;

		public LinearLayout btnComment;
		public LinearLayout btnShare;
		public LinearLayout btnLike;

	}

}
