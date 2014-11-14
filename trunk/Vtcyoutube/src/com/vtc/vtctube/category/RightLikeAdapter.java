package com.vtc.vtctube.category;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.internal.i;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.like.LichPhatsongAcitivity;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

public class RightLikeAdapter extends ArrayAdapter<ItemPost> {
	ViewHolder holder = null;
	private LayoutInflater mInflater;
	public static final int ITEM = 0;

	public static final int TYPE_VIEW_CATE = 0;
	public static final int TYPE_VIEW_DETAIL = 1;

	public static final int MOINHAT = 1;
	public static final int XEMNHIEU = 2;
	public static final int YEUTHICH = 3;

	private Context context;

	public static ImageLoader imageLoader = null;
	private IResult callBack;

	public RightLikeAdapter(int type, Context context, IResult callBack) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_infatuated, null);
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.lblTitlePost);
			holder.lblCountview = (TextView) convertView
					.findViewById(R.id.lblCountview);

			holder.imgIcon = (ImageView) convertView
					.findViewById(R.id.imageView1);

			holder.loadingBanner = (ProgressBar) convertView
					.findViewById(R.id.loadingBanner);
			holder.btnXoa = (TextView) convertView.findViewById(R.id.btnXoa);
		
			holder.lineFront = (LinearLayout) convertView
					.findViewById(R.id.front);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ItemPost item = getItem(position);
		holder.lblCountview.setText("Lượt xem: " + item.getCountview());

		holder.imgIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getVideoView(item);
			}
		});
		holder.txtTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getVideoView(item);
			}
		});

		
		holder.txtTitle.setText(Html.fromHtml(item.getTitle()));
		
		holder.btnXoa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (item.getKeyRemove() == Utils.LOAD_LIKE) {

					if (item.isLike()) {
						MainActivity.myDbHelper.deleteLikeVideo(
								DatabaseHelper.TB_LIKE, item.getIdPost());
						callBack.pushResutClickItem(item.getOption(),
								getPosition(item), false);

					} else {
						String sqlCheck = "SELECT * FROM "
								+ DatabaseHelper.TB_LIKE + " WHERE id='"
								+ item.getIdPost() + "'";
						if (MainActivity.myDbHelper.getCountRow(
								DatabaseHelper.TB_LIKE, sqlCheck) == 0) {
							MainActivity.myDbHelper.insertVideoLike(
									item.getIdPost(), item.getCateId(),
									item.getVideoId(), item.getUrl(),
									item.getStatus(), item.getTitle(),
									item.getSlug(), item.getCountview());
							callBack.pushResutClickItem(item.getOption(),
									getPosition(item), true);
						}

					}
				}

			}
		});
		imageLoader.displayImage(item.getUrl(), holder.imgIcon,
				Utils.getOptions(context, R.drawable.img_erorrs),
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// holder.loadingBanner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// holder.spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// holder.loadingBanner.setVisibility(View.GONE);
					}
				});
		// }

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

	// @Override
	// public boolean isItemViewTypePinned(int viewType) {
	// //return viewType == SECTION;
	// }

	public class ViewHolder {
		public TextView txtTitle;
		public TextView lblCountview;
		public ImageView imgIcon, ic_like;

		public LinearLayout lineFront, btnLike;
		public ProgressBar loadingBanner;
		private TextView btnXoa;
	}
	
	public void getVideoView(ItemPost item){
		if (item.getCateId().equals("1")) {
			Intent intent = new Intent(context,
					LichPhatsongAcitivity.class);
			intent.putExtra("content", item.getContent());
			context.startActivity(intent);

		} else {

			String sqlCheck = "SELECT * FROM "
					+ DatabaseHelper.TB_RESENT + " WHERE id='"
					+ item.getIdPost() + "'";
			if (MainActivity.myDbHelper.getCountRow(
					DatabaseHelper.TB_RESENT, sqlCheck) == 0) {
				MainActivity.myDbHelper.insertListVideo(
						DatabaseHelper.TB_RESENT, item.getCateId(),
						item.getTitle(), item.getVideoId(),
						item.getUrl(), item.getStatus(),
						item.getPageCount(), item.getIdPost(),
						item.getSlug(), item.getCountview());
			}
			callBack.onCLickView(item);
		}
	}

}
