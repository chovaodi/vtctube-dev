package com.vtc.vtctube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.vtc.vtctube.category.FragmentCategory;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.RightLikeAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.fragment.VideoPlayerFragment;
import com.vtc.vtctube.like.FragmentLike;
import com.vtc.vtctube.like.FragmentResent;
import com.vtc.vtctube.menu.MenuDrawer;
import com.vtc.vtctube.menu.MenuDrawer.OnDrawerStateChangeListener;
import com.vtc.vtctube.menu.Position;
import com.vtc.vtctube.model.AccountModel;
import com.vtc.vtctube.model.ItemMeu;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.search.FragmentSearchResult;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IClickCate;
import com.vtc.vtctube.utils.IRShareFeed;
import com.vtc.vtctube.utils.IRclickTocate;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.OnDisplayVideo;
import com.vtc.vtctube.utils.Utils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends SherlockFragmentActivity implements
		SearchView.OnQueryTextListener, SearchView.OnSuggestionListener,
		ConnectionCallbacks, OnConnectionFailedListener, OnDisplayVideo {
	public static String currentCate;

	private MenuDrawer leftMenu;
	private SuggestionsAdapter mSuggestionsAdapter;
	private static final String[] COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1, };

	private ListView listview;
	private SwipeListView listYeuthich;
	private View header;
	private SimpleFacebook mSimpleFacebook = null;
	private TextView lblUserName;
	private TextView lblAccountId;
	private TextView lblTitle;
	public static TextView lblError;
	private ImageView imgLogo;

	private ImageView imgAvata;
	private SearchView searchView;
	private ProgressBar prLoadLike;

	public static DatabaseHelper myDbHelper;
	public static SmoothProgressBar smooth;
	public static ResultCallBackCLick callBackCLick;
	public static ResultCallBackCate callBackCLickCate;
	public static ResultClickShare callClickShare;
	public static ViewGroup mainView;

	private ResultSearchCallBack callBackSearch;
	private List<ItemMeu> listItemMenu;
	public static List<ItemPost> listVideoRanDom = null;
	private List<String> listQuerySearch;

	private String queryCurent = "";
	private ItemPost itemActive = null;
	private GlobalApplication globalApp;
	private FragmentManager fragmentManager;
	private FragmentTransaction ft;
	private RightLikeAdapter adapter = null;
	private MenuDrawer rightMenu;
	private ResultItemClick callBackOnlick = new ResultItemClick();
	private EditText edSearch;
	private SlidingLayer mSlidingLayer;
	private Button btnFaceBook;
	private Button btnGoogle;
	private ResultCallBack callBack = new ResultCallBack();

	private int positionActive = Integer.MAX_VALUE;
	private int positionPreview = 0;
	private static final int RC_SIGN_IN = 0;

	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient = null;

	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private boolean isLoadding = false;
	private boolean isMenuCate = false;

	public static ProgressBar progressBar;
	public static String currentTag = "";

	private VideoPlayerFragment mVideoPlayerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainView = (ViewGroup) getWindow().getDecorView().findViewById(
				android.R.id.content);
		adapter = new RightLikeAdapter(PinnedAdapter.TYPE_VIEW_CATE,
				MainActivity.this, callBackOnlick);

		myDbHelper = new DatabaseHelper(MainActivity.this);
		callBackCLick = new ResultCallBackCLick();
		callBackCLickCate = new ResultCallBackCate();
		callClickShare = new ResultClickShare();

		fragmentManager = getSupportFragmentManager();
		ft = fragmentManager.beginTransaction();
		if (listVideoRanDom == null) {
			listVideoRanDom = new ArrayList<ItemPost>();
		}
		try {
			myDbHelper.createDataBase();
			myDbHelper.openDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			throw sqle;
		}
		listQuerySearch = getQuerySearch("SELECT * FROM "
				+ DatabaseHelper.TB_QUERY_SEARCH);
		globalApp = (GlobalApplication) getApplicationContext();

		listItemMenu = Utils.getMenu(MainActivity.this, R.menu.ribbon_menu);

		callBackSearch = new ResultSearchCallBack();

		leftMenu = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,
				Position.LEFT);
		leftMenu.setDropShadowColor(Color.parseColor("#993f3f3f"));
		leftMenu.setDropShadowSize(11);
		leftMenu.setAnimationCacheEnabled(true);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		leftMenu.setMenuSize(5 * width / 6);
		leftMenu.setMenuView(R.layout.leftmenu);

		rightMenu = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,
				Position.RIGHT);
		rightMenu.setDropShadowColor(Color.parseColor("#503f3f3f"));
		rightMenu.setDropShadowSize(8);
		rightMenu.setAnimationCacheEnabled(true);

		rightMenu.setMenuSize(5 * width / 6);
		rightMenu.setMenuView(R.layout.rightmenu);
		prLoadLike = (ProgressBar) findViewById(R.id.prLoadLike);

		listYeuthich = (SwipeListView) findViewById(R.id.listViewYeuthich);
		Utils.settingControlRemove(width, listYeuthich, MainActivity.this);

		listYeuthich.setAdapter(adapter);
		setContentView(R.layout.fragment_content);

		listview = (ListView) findViewById(R.id.listView1);
		header = getLayoutInflater().inflate(R.layout.account_layout, null);
		View fotter = getLayoutInflater().inflate(R.layout.footer, null);

		listview.addHeaderView(header);
		listview.addFooterView(fotter);

		lblUserName = (TextView) header.findViewById(R.id.lblName);
		lblAccountId = (TextView) header.findViewById(R.id.lblEmail);
		lblError = (TextView) findViewById(R.id.lblError);
		imgAvata = (ImageView) header.findViewById(R.id.imgAvata);

		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (globalApp.getAccountModel() == null) {
					mSlidingLayer.openLayer(true);
					positionActive = Integer.MAX_VALUE;
					leftMenu.toggleMenu();
				}
			}
		});
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

		MenuAdapter menuAdapter = new MenuAdapter(MainActivity.this);
		for (int i = 0; i < listItemMenu.size(); i++) {
			menuAdapter.addItem(listItemMenu.get(i));
		}
		listview.setAdapter(menuAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				positionActive = position - 1;
				leftMenu.toggleMenu();

			}

		});
		leftMenu.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

			@Override
			public void onDrawerStateChange(int oldState, int newState) {

				if (newState == MenuDrawer.STATE_CLOSED) {
					clickMenu(positionActive);
				}

				if (newState == MenuDrawer.STATE_OPEN) {
					zoominPlay();
				}

				Utils.hideSoftKeyboard(MainActivity.this);

				if (mGoogleApiClient.isConnected()) {
					getProfileInformation();
					return;
				}

				if (mSimpleFacebook.isLogin()) {
					getProfile();
					return;
				}

			}
		});

		rightMenu
				.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

					@Override
					public void onDrawerStateChange(int oldState, int newState) {
						if (mVideoPlayerFragment != null
								&& mVideoPlayerFragment.isMaximize()) {
							mVideoPlayerFragment.minimize();
						}

						if (newState == MenuDrawer.STATE_OPEN) {
							setDisplayView();
							zoominPlay();
						}

						if (itemActive != null
								&& newState == MenuDrawer.STATE_CLOSED) {
							Utils.getVideoView(itemActive, MainActivity.this,
									listVideoRanDom);
							itemActive = null;
							display();
						}

					}
				});

		smooth = (SmoothProgressBar) findViewById(R.id.google_now);
		smooth.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM
						| ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.icon_menuleft));
		getSupportActionBar().setCustomView(R.layout.header_task);
		imgLogo = (ImageView) findViewById(R.id.iconHeader);
		lblTitle = (TextView) findViewById(R.id.lblHeaderTile);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		Fragment newFragment = FragmentHome.newInstance(1);
		ft.replace(R.id.container, newFragment, Utils.TAG_HOME).commit();

		edSearch = (EditText) findViewById(R.id.edSearch);
		edSearch.setOnEditorActionListener(new DoneOnEditorActionListener());

		mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);
		LayoutParams rlp = (LayoutParams) mSlidingLayer.getLayoutParams();
		mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlp.width = LayoutParams.MATCH_PARENT;
		rlp.height = Utils.convertDpToPixel(140, MainActivity.this);

		mSlidingLayer.setLayoutParams(rlp);
		mSlidingLayer.setShadowWidthRes(R.dimen.shadow);
		mSlidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);

		btnGoogle = (Button) findViewById(R.id.btnGoogle);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		btnGoogle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSlidingLayer.closeLayer(true);

				signInWithGplus();
			}
		});

		btnFaceBook = (Button) findViewById(R.id.btnFacebook);
		btnFaceBook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mSimpleFacebook.isLogin()) {
					getProfile();
				} else {
					mSimpleFacebook.login(onLoginListener);
				}
				mSlidingLayer.closeLayer(true);

			}
		});

	}

	public void zoominPlay() {
		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMaximize()) {
			mVideoPlayerFragment.minimize();
		}
	}

	public void setViewTab() {
		String queryLikeVideo = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		List<ItemPost> list = Utils.getVideoLike(queryLikeVideo, 1);

		adapter.clear();
		adapter.notifyDataSetChanged();

		for (int i = 0; i < list.size(); i++) {
			list.get(i).setType(PinnedAdapter.ITEM);
			adapter.add(list.get(i));
		}
		adapter.notifyDataSetChanged();

	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	class DoneOnEditorActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				if (edSearch.getText().toString().length() == 0) {
					Utils.getDialogMessges(MainActivity.this, getResources()
							.getString(R.string.lblNullTk));

				} else {
					Utils.hideSoftKeyboard(MainActivity.this);
					leftMenu.toggleMenu();
					String query = edSearch.getText().toString().trim()
							.replaceAll("[-+.^:,@#$%&*()<>{}]", "");
					actionSearch(query.replaceAll("", "%20"));
				}
				return true;
			}
			return false;
		}
	}

	protected void sendEmail() {
		Uri uri = Uri.parse("market://details?id="
				+ MainActivity.this.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ MainActivity.this.getPackageName())));
		}
	}

	public class ResultItemClick implements IResult {

		@Override
		public void getResult(int type, String result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			if (type == Utils.HOANTAC) {
				listYeuthich.closeAnimate(postion);
			} else {
				listYeuthich.closeOpenedItems();
				setViewTab();
			}
		}

		@Override
		public void onCLickView(ItemPost item) {
			itemActive = item;
			rightMenu.toggleMenu();
		}
	}

	public void setDisplayView() {
		String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
		List<ItemPost> listData = Utils.getVideoLike(sqlLike,
				PinnedAdapter.YEUTHICH);
		if (listData.size() == 0) {
			String url = Utils.host + "get_posts?count=10&page=5";
			if (!isLoadding && listVideoRanDom.size() == 0) {
				prLoadLike.setVisibility(View.VISIBLE);
				isLoadding = true;
				new AysnRequestHttp(mainView, Utils.LOAD_FIRST_DATA, null,
						callBack).execute(url);
			}
		} else if (listData.size() != adapter.getCount()) {
			if (listVideoRanDom != null) {
				listVideoRanDom = new ArrayList<ItemPost>();
			}
			addViewData(listData);
		}

	}

	public class ResultCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			prLoadLike.setVisibility(View.INVISIBLE);
			isLoadding = false;
			Utils.disableEnableControls(true, (ViewGroup) mainView);

			try {
				listVideoRanDom = new ArrayList<ItemPost>();
				JSONObject jsonObj = new JSONObject(result);
				int count_total = jsonObj.getInt("count_total");
				if (count_total > 0) {
					JSONArray jsonArray = jsonObj.getJSONArray("posts");
					for (int i = 0; i < jsonArray.length(); i++) {
						ItemPost item = new ItemPost();

						JSONObject json = (JSONObject) jsonArray.get(i);
						item = Utils.getItemPost(json, 0, 0);
						item.setKeyRemove(Utils.LOAD_RADOM);
						listVideoRanDom.add(item);
					}

					addViewData(listVideoRanDom);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			// TODO Auto-generated method stub
		}
	}

	public void addViewData(List<ItemPost> list) {
		adapter.clear();
		for (int i = 0; i < list.size(); i++) {
			if ("publish".equals(list.get(i).getStatus())) {
				list.get(i).setType(PinnedAdapter.ITEM);
				adapter.add(list.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}

	public void clickMenu(int position) {
		MainActivity.lblError.setVisibility(View.GONE);
		isMenuCate = false;
		fragmentManager = getSupportFragmentManager();
		ft = fragmentManager.beginTransaction();

		if (positionActive == Integer.MAX_VALUE) {
			return;
		}
		int id = listItemMenu.get(position).getRegId();
		if (id == positionPreview) {
			return;
		}
		positionPreview = id;
		switch (id) {
		case R.id.menu_trangchu:
			setHome();
			break;

		case R.id.menu_video_moinhat:
			actionNewvideo();
			break;
		case R.id.menu_video_xemnhieu:
			actionXemnhieu();
			break;

		case R.id.menu_video_yeuthich:
			addFragmentLike(R.id.menu_video_yeuthich,
					getResources().getString(R.string.lblmenu_yeuthich));

			break;
		case R.id.menu_video_daxem:
			String sqlDaxem = "SELECT * FROM " + DatabaseHelper.TB_RESENT;
			if (myDbHelper.getCountRow(DatabaseHelper.TB_RESENT, sqlDaxem) > 0) {
				addFragmentResent(R.id.menu_video_daxem, getResources()
						.getString(R.string.lblmenu_daxem));
			} else {
				positionPreview = 0;
				positionActive = Integer.MAX_VALUE;
				setHome();
			}

			break;
		case R.id.menu_danhgia:
			sendEmail();
			break;

		case R.id.menu_nhataitro:
			addFragmentAbout();
			break;
		case R.id.menu_tivi_tructuyen:

			addNewFeed();
			break;
		}
		invalidateOptionsMenu();
		positionActive = Integer.MAX_VALUE;

	}

	public void setHome() {
		MainActivity.lblError.setVisibility(View.GONE);
		FragmentHome fragment = (FragmentHome) fragmentManager
				.findFragmentByTag(Utils.TAG_HOME);
		if (fragment == null) {
			// ft.addToBackStack(null);
			ft.replace(R.id.container, FragmentHome.newInstance(1),
					Utils.TAG_HOME);
		} else {
			ft.replace(R.id.container, fragment, Utils.TAG_HOME);
		}
		ft.commit();
		FragmentCategory.frament = null;
		MainActivity.callBackCLick.onClick(false, "");
	}

	public ArrayList<String> getQuerySearch(String sql) {
		Cursor c = myDbHelper.query(DatabaseHelper.TB_LISTVIDEO, null, null,
				null, null, null, null);
		c = myDbHelper.rawQuery(sql);
		ArrayList<String> listAccount = new ArrayList<String>();

		if (c.moveToFirst()) {
			do {
				listAccount.add(c.getString(0).replace("%20", " "));
			} while (c.moveToNext());
		}
		return listAccount;
	}

	public String getLinkAvataFace(String id) {
		return "https://graph.facebook.com/" + id + "/picture?type=normal";
	}

	public void setAccInfo() {
		if (globalApp.getAccountModel() != null) {

			if (lblUserName.getText().equals(
					getResources().getString(R.string.lbllogin))) {
				if (globalApp.getAccountModel().getType() == AccountModel.LOGIN_FACE) {

					Picasso.with(MainActivity.this)
							.load(getLinkAvataFace(globalApp.getAccountModel()
									.getUserID()))
							.placeholder(R.drawable.img_erorrs).into(imgAvata);

					lblAccountId.setText(globalApp.getAccountModel()
							.getUserID());

				} else {
					lblAccountId
							.setText(globalApp.getAccountModel().getEmail());
					Picasso.with(MainActivity.this)
							.load(globalApp.getAccountModel().getUrlPhoto())
							.placeholder(R.drawable.img_erorrs).into(imgAvata);
				}
			}
			lblUserName.setText(globalApp.getAccountModel().getUserName());

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		// adView.resume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode != 102)
				mSimpleFacebook.onActivityResult(this, requestCode, resultCode,
						data);
			if (requestCode == RC_SIGN_IN) {
				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
			}
		} catch (Exception e) {

		}

	}

	public class ResultClickShare implements IRShareFeed {

		@Override
		public void onShare(String title, String thumnail, String slug) {
			if (mSimpleFacebook.isLogin()) {
				setPostShare(title, thumnail, slug);
			} else {
				mSimpleFacebook.login(new OnLoginListener() {

					@Override
					public void onFail(String reason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onException(Throwable throwable) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onThinking() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onNotAcceptingPermissions(Type type) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLogin() {
						// TODO Auto-generated method stub

					}
				});
			}

		}
	}

	public void setPostShare(String title, String thumnail, String slug) {

		String shareLink = "http://vtctube.vn/" + slug + "-" + ".html";
		Feed feed = new Feed.Builder()
				.setMessage(title)
				.setName(getResources().getString(R.string.lblNameshare))
				.setCaption("")
				.setDescription(
						getResources().getString(R.string.lblDescription))
				.setPicture(thumnail).setLink(shareLink).build();
		SimpleFacebook.getInstance().publish(feed, true, onPublishListener);
	}

	OnPublishListener onPublishListener = new OnPublishListener() {
		@Override
		public void onComplete(String postId) {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.lblShare),
					Toast.LENGTH_LONG).show();
		}

		public void onException(Throwable throwable) {
			Toast.makeText(MainActivity.this, "onException", Toast.LENGTH_LONG)
					.show();
		};

		@Override
		public void onThinking() {

		};

		/*
		 * You can override other methods here: onThinking(), onFail(String
		 * reason), onException(Throwable throwable)
		 */
	};

	OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
		}

		@Override
		public void onException(Throwable throwable) {
		}

		@Override
		public void onThinking() {
		}

		@Override
		public void onLogin() {
			getProfile();

		}

		@Override
		public void onNotAcceptingPermissions(Type type) {

		}
	};

	public void getProfile() {

		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				String user_ID = profile.getId();// user id
				String profileName = profile.getName();// user's
				AccountModel account = new AccountModel();
				account.setUserID(user_ID);
				account.setUserName(profileName);
				account.setType(AccountModel.LOGIN_FACE);
				globalApp.setAccountModel(account);
				setAccInfo();
			}

			@Override
			public void onThinking() {
			}

			@Override
			public void onFail(String reason) {

				super.onFail(reason);
			}

		});

	}

	public class ResultCallBackCate implements IRclickTocate {

		@Override
		public void getCate(String title, String cate) {
			isMenuCate = true;
			addFragment(title, cate);
			invalidateOptionsMenu();
		}
	}

	public void addFragmentSearch(String json, String tag, int keyOption,
			String title) {

		Utils.hideSoftKeyboard(MainActivity.this);
		isMenuCate=false;
		MainActivity.callBackCLick.onClick(true, title);
		FragmentManager fragmentManager = MainActivity.this
				.getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentSearchResult fragment = null;
		currentTag = tag;
		fragment = (FragmentSearchResult) fragmentManager
				.findFragmentByTag(tag);
		if (fragment == null) {
			fragment = FragmentSearchResult.newInstance(json, queryCurent,
					keyOption);
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, tag);
		} else {
			FragmentSearchResult fragmentTmp = new FragmentSearchResult();
			fragmentTmp.setCate(json, queryCurent, keyOption);
			ft.show(fragment);
		}
		invalidateOptionsMenu();

		ft.commit();

	}

	public void addFragmentResent(int id, String title) {
		MainActivity.callBackCLick.onClick(true, title);
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		fragmentManager = getSupportFragmentManager();

		FragmentResent fragment = (FragmentResent) fragmentManager
				.findFragmentByTag(Utils.TAG_RESENT);
		currentTag = Utils.TAG_RESENT;
		if (fragment == null) {
			fragment = new FragmentResent();
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_RESENT);
		} else {
			// FragmentResent
			ft.replace(R.id.container, fragment, Utils.TAG_RESENT);
		}

		ft.commit();

	}

	public void addFragmentLike(int id, String title) {
		MainActivity.callBackCLick.onClick(true, title);
		FragmentTransaction ft = fragmentManager.beginTransaction();

		FragmentLike fragment = (FragmentLike) fragmentManager
				.findFragmentByTag(Utils.TAG_LIKE);
		currentTag = Utils.TAG_LIKE;
		if (fragment == null) {
			fragment = FragmentLike.newInstance();
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_LIKE);
		} else {

			ft.replace(R.id.container, fragment, Utils.TAG_LIKE);
		}

		ft.commit();

	}

	public void addFragment(String title, String cate) {
		MainActivity.callBackCLick.onClick(true, title);

		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentCategory fragment = (FragmentCategory) fragmentManager
				.findFragmentByTag(Utils.TAG_CATE);
		currentTag = Utils.TAG_CATE;
		if (fragment == null || !fragment.isInLayout()) {
			fragment = FragmentCategory.newInstance(cate, title);
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_CATE);

		} else {

			// ft.hide(getSupportFragmentManager().findFragmentByTag(
			// currentTag));
			// ft.replace(R.id.container, fragment, Utils.TAG_CATE);

		}

		ft.commit();

	}

	public void addFragmentAbout() {
		MainActivity.callBackCLick.onClick(true,
				getResources().getString(R.string.lblAbout));
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentAbout fragment = (FragmentAbout) fragmentManager
				.findFragmentByTag(Utils.TAG_ABOUT);
		currentTag = Utils.TAG_ABOUT;
		if (fragment == null) {
			fragment = FragmentAbout.newInstance();
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_ABOUT);
		} else {
			ft.replace(R.id.container, fragment, Utils.TAG_ABOUT);
		}

		ft.commit();

	}

	public void addNewFeed() {
		MainActivity.callBackCLick.onClick(true,
				getResources().getString(R.string.lblThongbaomoi));
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
		FragmentNewfeed fragment = (FragmentNewfeed) fragmentManager
				.findFragmentByTag(Utils.TAG_NEWFEED);
		currentTag = Utils.TAG_NEWFEED;
		if (fragment == null) {
			fragment = FragmentNewfeed.newInstance();
			// ft.addToBackStack(null);
			ft.replace(R.id.container, fragment, Utils.TAG_NEWFEED);
		} else {
			ft.replace(R.id.container, fragment, Utils.TAG_NEWFEED);
		}

		ft.commit();

	}

	public class ResultCallBackCLick implements IClickCate {

		@Override
		public void onClick(boolean isShowTitle, String title) {

			if (isShowTitle) {
				lblTitle.setVisibility(View.VISIBLE);
				imgLogo.setVisibility(View.GONE);
				lblTitle.setText(title);
				positionPreview = 0;
				positionActive = Integer.MAX_VALUE;
			} else {

				lblTitle.setVisibility(View.GONE);
				imgLogo.setVisibility(View.VISIBLE);
			}
			invalidateOptionsMenu();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		// Create the search view
		searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint(getResources().getString(R.string.lblTimkiem));
		searchView.setOnQueryTextListener(this);
		searchView.setOnSuggestionListener(this);

		if (mSuggestionsAdapter == null) {
			MatrixCursor cursor = new MatrixCursor(COLUMNS);
			for (int i = 0; i < listQuerySearch.size(); i++) {
				cursor.addRow(new String[] { String.valueOf(i),
						listQuerySearch.get(i) });
			}
			mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar()
					.getThemedContext(), cursor);
		}

		searchView.setSuggestionsAdapter(mSuggestionsAdapter);

		menu.add(1, 10000, Menu.NONE,
				getResources().getString(R.string.lblTimkiem))
				.setIcon(
						isLight ? R.drawable.icon_search
								: R.drawable.icon_search)
				.setActionView(searchView)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		if (isMenuCate) {
			SubMenu subMenu1 = menu.addSubMenu(getResources().getString(
					R.string.lblDanhmuc));
			for (int i = 0; i < FragmentHome.listData.size(); i++) {
				subMenu1.add(0, Integer.parseInt(FragmentHome.listData.get(i)
						.getIdCategory()), Menu.NONE, FragmentHome.listData
						.get(i).getTitle());
			}

			MenuItem subMenu1Item = subMenu1.getItem();
			subMenu1Item.setIcon(R.drawable.ic_feedmnu_o);
			subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			leftMenu.toggleMenu();
		} else if (id != 0 && id != 10000) {
			lblTitle.setText(item.getTitle());
			zoominPlay();
			FragmentCategory fragmentTmp = new FragmentCategory();
			fragmentTmp.setCate(String.valueOf(id));
		}
		return super.onOptionsItemSelected(item);
	}

	public class ResultSearchCallBack implements IResult {

		@Override
		public void getResult(int type, String result) {
			Log.d("result", result);
			try {
				JSONObject jsonObj = new JSONObject(result);
				String status = jsonObj.getString("status");
				int count_total = jsonObj.getInt("count_total");
				if (status.equals("ok") && count_total > 0) {
					switch (type) {
					case Utils.LOAD_SEARCH:
						String sqlCheck = "SELECT * FROM "
								+ DatabaseHelper.TB_QUERY_SEARCH
								+ " WHERE query='" + queryCurent + "'";

						if (myDbHelper.getCountRow(
								DatabaseHelper.TB_QUERY_SEARCH, sqlCheck) == 0) {
							myDbHelper.insertQuerySearch(queryCurent);
						}
						JSONObject json = new JSONObject(result);
						if (json.getString("status").equals("ok")) {
							addFragmentSearch(result, Utils.TAG_SEARCH,
									Utils.LOAD_SEARCH, getResources()
											.getString(R.string.lblkqtq));

						}
						
						zoominPlay();

						break;
					case Utils.LOAD_NEWVIDEO:

						addFragmentSearch(
								result,
								Utils.TAG_NEWVIDEO,
								Utils.LOAD_NEWVIDEO,
								getResources().getString(
										R.string.lblmenu_videomoi));
						break;
					case Utils.LOAD_XEMNHIEU:

						addFragmentSearch(
								result,
								Utils.TAG_XEMNHIEU,
								Utils.LOAD_XEMNHIEU,
								getResources().getString(
										R.string.lblmenu_xemnhieu));
						break;
					}

				} else {
					positionPreview = 0;
					positionActive = Integer.MAX_VALUE;
					Utils.getDialogMessges(MainActivity.this, getResources()
							.getString(R.string.lblMsgrong));

				}
			} catch (Exception e) {
				e.printStackTrace();
				positionPreview = 0;
				positionActive = Integer.MAX_VALUE;
				Utils.getDialogMessges(MainActivity.this, getResources()
						.getString(R.string.lblMsgrong));
			}
		}

		@Override
		public void pushResutClickItem(int type, int postion, boolean isLike) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCLickView(ItemPost item) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null)
			mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onBackPressed() {
		MainActivity.lblError.setVisibility(View.GONE);
		final int left = leftMenu.getDrawerState();
		if (left == MenuDrawer.STATE_OPEN || left == MenuDrawer.STATE_OPENING) {
			leftMenu.closeMenu();
			return;
		}
		final int right = rightMenu.getDrawerState();
		if (right == MenuDrawer.STATE_OPEN || right == MenuDrawer.STATE_OPENING) {
			rightMenu.closeMenu();
			return;
		}

		if (mVideoPlayerFragment != null && mVideoPlayerFragment.isMaximize()) {
			mVideoPlayerFragment.minimize();
			return;
		}

		positionPreview = 0;
		positionActive = Integer.MAX_VALUE;

		Fragment myAbout = getSupportFragmentManager().findFragmentByTag(
				currentTag);
		if (myAbout != null) {
			isMenuCate=false;
			MainActivity.lblError.setVisibility(View.GONE);
			FragmentHome fragment = new FragmentHome();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment).commit();
			FragmentCategory.frament = null;
			MainActivity.callBackCLick.onClick(false, "");
			invalidateOptionsMenu();
			return;
		}

		finish();
		System.exit(0);
	}

	public void onBackView() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStack();
		MainActivity.callBackCLick.onClick(false, "");

	}

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
		queryCurent = c.getString(c
				.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

		AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView
				.findViewById(R.id.abs__search_src_text);

		if (searchTextView != null) {
			searchTextView.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			searchTextView.setTypeface(Typeface.DEFAULT);
			searchTextView.setText(queryCurent);
		}
		queryCurent = queryCurent.replace(" ", "%20");
		actionSearch(queryCurent);
		return false;
	}

	public void actionSearch(String query) {
		String url = Utils.host + "get_search_results?search=" + query;
		Log.d("url", url);
		new AysnRequestHttp(mainView, Utils.LOAD_SEARCH, smooth, callBackSearch)
				.execute(url);
	}

	public void actionNewvideo() {
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// Date dateobj = new Date();
		// String currentDateandTime = df.format(dateobj).toString();
		String url = Utils.host + "get_posts?count=10&page=1";
		Log.d("actionNewvideo", url);
		new AysnRequestHttp(mainView, Utils.LOAD_NEWVIDEO, smooth,
				callBackSearch).execute(url);
	}

	public void actionXemnhieu() {
		String url = Utils.host + "get_posts?count=50&page=2";
		new AysnRequestHttp((ViewGroup) mainView, Utils.LOAD_XEMNHIEU,
				MainActivity.smooth, callBackSearch).execute(url);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		queryCurent = query.replace(" ", "%20");
		actionSearch(queryCurent);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		listQuerySearch = getQuerySearch("SELECT * FROM "
				+ DatabaseHelper.TB_QUERY_SEARCH);

		for (int i = 0; i < listQuerySearch.size(); i++) {
			if (listQuerySearch.get(i).contains(newText)) {
				String valueTmp = listQuerySearch.get(i);
				listQuerySearch.set(i, listQuerySearch.get(0));
				listQuerySearch.set(0, valueTmp);

				break;
			}
		}

		MatrixCursor cursor = new MatrixCursor(COLUMNS);
		for (int i = 0; i < listQuerySearch.size(); i++) {
			cursor.addRow(new String[] { String.valueOf(i),
					listQuerySearch.get(i) });
		}
		mSuggestionsAdapter.changeCursor(cursor);
		mSuggestionsAdapter.notifyDataSetChanged();

		return false;
	}

	private class SuggestionsAdapter extends CursorAdapter {

		public SuggestionsAdapter(Context context, Cursor c) {
			super(context, c, 0);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv = (TextView) view;
			final int textIndex = cursor
					.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
			tv.setText(cursor.getString(textIndex));
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				Log.d("mSignInClicked", "mSignInClicked");
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				// resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		btnGoogle.setEnabled(false);

	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2) + 400;
				AccountModel accountModel = new AccountModel();
				accountModel.setUserName(personName);
				accountModel.setEmail(email);
				accountModel.setUrlPhoto(personPhotoUrl);
				accountModel.setType(AccountModel.LOGIN_GOOGLE);
				globalApp.setAccountModel(accountModel);
				setAccInfo();

			} else {
				// Toast.makeText(getApplicationContext(),
				// "Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void display() {
		displayVideo();
	}

	private void displayVideo() {
		Context context = getApplicationContext();
		if (Utils.hasConnection(context) == false) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.not_available_while_offline),
					Toast.LENGTH_LONG).show();
			return;
		}
		Log.d("VTCTube", "displayVideo: " + mVideoPlayerFragment);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (mVideoPlayerFragment == null) {
			mVideoPlayerFragment = VideoPlayerFragment.newInstance();
			fragmentTransaction
					.replace(R.id.main_screen, mVideoPlayerFragment,
							Utils.TAG_VIEW_VIDEO).addToBackStack(null).commit();
		} else {
			mVideoPlayerFragment.updateData();
			mVideoPlayerFragment.maximize();
		}
	}
}
