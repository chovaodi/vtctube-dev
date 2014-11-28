package com.vtc.vtctube.category;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class PinnedAdapter extends ArrayAdapter<ItemPost> {
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
	private int typeView;

	public PinnedAdapter(int type, Context context, IResult callBack) {
		super(context, 0);
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.callBack = callBack;
		this.typeView = type;

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

			convertView = mInflater.inflate(R.layout.item_post, null);
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.lblTitlePost);
			holder.lblCountview = (TextView) convertView
					.findViewById(R.id.lblCountview);
			holder.iconLike = (ImageButton) convertView
					.findViewById(R.id.ic_like);

			holder.imgIcon = (ImageView) convertView
					.findViewById(R.id.imageView1);

			holder.btnComment = (LinearLayout) convertView
					.findViewById(R.id.btnComment);

			holder.btnLike = (LinearLayout) convertView
					.findViewById(R.id.btnLike);
			holder.btnShare = (LinearLayout) convertView
					.findViewById(R.id.btnShare);
			holder.lineClick = (LinearLayout) convertView
					.findViewById(R.id.onClickItem);
			holder.loadingBanner = (ProgressBar) convertView
					.findViewById(R.id.loadingBanner);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ItemPost item = getItem(position);

		if (typeView == TYPE_VIEW_DETAIL) {
			holder.btnLike.setVisibility(View.GONE);
			holder.btnShare.setVisibility(View.GONE);
			holder.lblCountview.setText("Lượt xem: " + item.getCountview());
		} else {
			holder.lblCountview.setText(item.getCountview());
			holder.lblCountview.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.eye, 0, 0, 0);
		}

		if (item.isLike()) {
			holder.iconLike.setSelected(true);
		} else {
			holder.iconLike.setSelected(false);
		}

		holder.lineClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("item.getCateId()", item.getCateId());
				if (item.getCateId().equals("1")) {
					Intent intent = new Intent(context,
							LichPhatsongAcitivity.class);
					intent.putExtra("content", item.getContent());
					intent.putExtra("title", item.getTitle());
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
		});

		holder.txtTitle.setText(Html.fromHtml(item.getTitle()));

		// Bitmap bmp = imageLoader.loadImageSync(item.getUrl(),
		// Utils.getOptions(context, R.drawable.img_erorrs));
		// if (bmp != null) {
		// holder.imgIcon.setImageBitmap(bmp);
		// } else {

		if (item.getCateId().equals("1")) {
			holder.imgIcon.setImageDrawable(context.getResources().getDrawable(
					R.drawable.error_home));
		} else {

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
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// holder.loadingBanner.setVisibility(View.GONE);
						}
					});
		}

		holder.btnLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (item.isLike()) {
					MainActivity.myDbHelper.deleteLikeVideo(
							DatabaseHelper.TB_LIKE, item.getIdPost());
					callBack.pushResutClickItem(item.getOption(),
							getPosition(item), false);
				} else {
					String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_LIKE
							+ " WHERE id='" + item.getIdPost() + "'";
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
		});
		holder.btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// MainActivity.callClickShare.onShare(item.getTitle(),
				// item.getUrl(), item.getSlug());
				//
				Utils.shareButton(item, (Activity) context);

			}
		});

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
		public ImageView imgIcon;
		public ImageView submenu;

		public ImageButton iconLike;

		public Button btnMoinhat;
		public Button btnXemnhieu;
		public Button btnYeuthich;

		public LinearLayout btnComment;
		public LinearLayout btnShare;
		public LinearLayout btnLike;

		public LinearLayout lineClick;
		public ProgressBar loadingBanner;

	}

}
