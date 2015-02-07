package com.vtc.vtctube.category;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.like.LichPhatsongAcitivity;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;
import com.vtc.xemtivi.MainActivity;
import com.vtc.xemtivi.R;

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

	private IResult callBack;

	public RightLikeAdapter(int type, Context context, IResult callBack) {
		super(context, 0);
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.callBack = callBack;

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
			holder.btnHoantac = (TextView) convertView
					.findViewById(R.id.btnHoantac);

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
		holder.btnHoantac.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				remove(item);

			}
		});

		holder.txtTitle.setText(Html.fromHtml(item.getTitle()));

		holder.btnXoa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				remove(item);

			}
		});

		if (item.getCateId().equals("1")) {
			holder.imgIcon.setImageDrawable(context.getResources().getDrawable(
					R.drawable.error_home));
		} else {

			Picasso.with(context).load(item.getUrl())
					.placeholder(R.drawable.img_erorrs).into(holder.imgIcon);

		}

		return convertView;
	}

	public void remove(ItemPost item) {

		if (item.getKeyRemove() == Utils.LOAD_LIKE) {

			if (item.isLike()) {
				MainActivity.myDbHelper.deleteLikeVideo(DatabaseHelper.TB_LIKE,
						item.getIdPost());
//				callBack.pushResutClickItem(item.getOption(),
//						getPosition(item), false);

			} else {
				String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_LIKE
						+ " WHERE id='" + item.getIdPost() + "'";
				if (MainActivity.myDbHelper.getCountRow(DatabaseHelper.TB_LIKE,
						sqlCheck) == 0) {
					MainActivity.myDbHelper.insertVideoLike(item.getIdPost(),
							item.getCateId(), item.getVideoId(), item.getUrl(),
							item.getStatus(), item.getTitle(), item.getSlug(),
							item.getCountview());
//					callBack.pushResutClickItem(item.getOption(),
//							getPosition(item), true);
				}

			}
		}

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
		private TextView btnHoantac;
	}

	public void getVideoView(ItemPost item) {
		if (item.getCateId().equals("1")) {
			Intent intent = new Intent(context, LichPhatsongAcitivity.class);
			intent.putExtra("content", item.getContent());
			intent.putExtra("title", item.getTitle());
			context.startActivity(intent);

		} else {

			String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_RESENT
					+ " WHERE id='" + item.getIdPost() + "'";
			if (MainActivity.myDbHelper.getCountRow(DatabaseHelper.TB_RESENT,
					sqlCheck) == 0) {
				MainActivity.myDbHelper.insertListVideo(
						DatabaseHelper.TB_RESENT, item.getCateId(),
						item.getTitle(), item.getVideoId(), item.getUrl(),
						item.getStatus(), item.getPageCount(),
						item.getIdPost(), item.getSlug(), item.getCountview());
			}
			//callBack.onCLickView(item);
		}
	}

}
