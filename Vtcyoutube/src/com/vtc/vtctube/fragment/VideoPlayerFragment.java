package com.vtc.vtctube.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.internal.mf;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.pedrovgs.draggablepanel.DraggableListener;
import com.pedrovgs.draggablepanel.DraggableView;
import com.vtc.vtctube.MainActivity;
import com.vtc.vtctube.R;
import com.vtc.vtctube.category.PinnedAdapter;
import com.vtc.vtctube.category.RightLikeAdapter;
import com.vtc.vtctube.database.DatabaseHelper;
import com.vtc.vtctube.menu.MenuDrawer;
import com.vtc.vtctube.menu.MenuDrawer.OnDrawerStateChangeListener;
import com.vtc.vtctube.menu.Position;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.Utils;

public class VideoPlayerFragment extends YoutubePlayerFragment {
    String videoId = "";
    private String title;
    private String countview;
    private String url = "";
    private String slug;
    private String cate;

    private int id;
    private String status;

    private Button btnLienquan;
    private Button btnChitiet;
    private LinearLayout lineChitiet;
    private LinearLayout lineBack;
    private ProgressBar prLoadLike;

    private TextView lblYeuthich;
    private TextView lblTitle;
    private TextView lblTaskTitle;
    private TextView lblCountView;
    private TextView lblShare;

    public static ViewGroup mainView;
    private ListView listvideo;
    private WebView webview_fbview;
    private ProgressBar loadingListview;
    // private YouTubePlayerView youTubeView;
    private YouTubePlayerSupportFragment mYoutubeFragment;
    private YouTubePlayer mPlayer;
    private MenuDrawer rightMenu;
    private RightLikeAdapter adapter = null;
    private PinnedAdapter adapterTab;
    private SwipeListView listYeuthich;
    private ResultItemClick callBackOnlick = new ResultItemClick();
    private ItemPost itemActive = null;
    private boolean isLoadding = false;
    private int inPostActive;
    private ProgressBar progressBar1;
    private FragmentActivity mActivity;

    private DraggableView mView;

    @Override
    public void onAttach(Activity activity) {
        mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (DraggableView) inflater.inflate(R.layout.playerview_demo, container, false);
        hookDraggablePanelListeners();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        mainView = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);

        adapter = new RightLikeAdapter(PinnedAdapter.TYPE_VIEW_CATE, mActivity, callBackOnlick);

        mActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

        rightMenu = MenuDrawer.attach(mActivity, MenuDrawer.MENU_DRAG_WINDOW, Position.RIGHT);
        rightMenu.setDropShadowColor(Color.parseColor("#503f3f3f"));
        rightMenu.setDropShadowSize(8);
        rightMenu.setAnimationCacheEnabled(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        rightMenu.setMenuSize(5 * width / 6);
        rightMenu.setMenuView(R.layout.rightmenu);
        prLoadLike = (ProgressBar) rightMenu.findViewById(R.id.prLoadLike);

        listYeuthich = (SwipeListView) rightMenu.findViewById(R.id.listViewYeuthich);
        Utils.settingControlRemove(width, listYeuthich, mActivity);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);

        listYeuthich.setAdapter(adapter);

        btnLienquan = (Button) view.findViewById(R.id.btnLienquan);
        btnChitiet = (Button) view.findViewById(R.id.btnChitiet);
        listvideo = (ListView) view.findViewById(R.id.listvideo);
        lblTitle = (TextView) view.findViewById(R.id.lblTitle);
        lblTaskTitle = (TextView) view.findViewById(R.id.lblTaskTitle);
        lblCountView = (TextView) view.findViewById(R.id.lblLuotxem);
        lblShare = (TextView) view.findViewById(R.id.btnShareDetailt);
        lblShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.shareButton(itemActive, mActivity);
            }
        });

        lblYeuthich = (TextView) view.findViewById(R.id.lblYeuthich);
        lblYeuthich.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!itemActive.isLike()) {
                    actionLike();
                    itemActive.setLike(true);
                    lblYeuthich.setSelected(true);
                } else {
                    itemActive.setLike(false);
                    MainActivity.myDbHelper.deleteLikeVideo(DatabaseHelper.TB_LIKE, itemActive.getIdPost());
                    lblYeuthich.setSelected(false);
                }

            }
        });

        ImageButton imgLike = (ImageButton) view.findViewById(R.id.btnLike);
        imgLike.setSelected(true);
        imgLike.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rightMenu.toggleMenu();

            }
        });
        lineChitiet = (LinearLayout) view.findViewById(R.id.lineChitiet);
        lineBack = (LinearLayout) view.findViewById(R.id.lineBack);
        lineBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });

        lblTitle.setText(title);

        listvideo.setVisibility(View.VISIBLE);
        lineChitiet.setVisibility(View.GONE);

        btnLienquan.setSelected(true);
        btnChitiet.setSelected(false);

        btnLienquan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnLienquan.setSelected(true);
                btnChitiet.setSelected(false);
                listvideo.setVisibility(View.VISIBLE);
                lineChitiet.setVisibility(View.GONE);

            }
        });

        btnChitiet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnLienquan.setSelected(false);
                btnChitiet.setSelected(true);
                if (btnChitiet.isSelected()) {
                    loadComment("http://vtctube.vn/" + itemActive.getSlug());
                }

                listvideo.setVisibility(View.GONE);
                lineChitiet.setVisibility(View.VISIBLE);
            }
        });
        ResultOnclikTab callBackOnlick = new ResultOnclikTab();
        adapterTab = new PinnedAdapter(PinnedAdapter.TYPE_VIEW_DETAIL, mActivity, callBackOnlick);
        listvideo.setAdapter(adapterTab);

        loadingListview = (ProgressBar) view.findViewById(R.id.loadingListview);
        loadingListview.setVisibility(View.GONE);

        webview_fbview = (WebView) view.findViewById(R.id.contentView);
        settingWebView();
        mYoutubeFragment = new YouTubePlayerSupportFragment();// (YouTubePlayerSupportFragment)
                                                              // getFragmentManager().findFragmentById(R.id.youtube_player_fragment);
        getFragmentManager().beginTransaction().replace(R.id.youtube_player_fragment, mYoutubeFragment).commit();
        rightMenu.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_OPEN) {
                    setData();
                }
                if (newState == MenuDrawer.STATE_CLOSED) {
                    if (inPostActive != itemActive.getIdPost() && itemActive != null && !itemActive.getVideoId().equals(id)) {
                        mPlayer.cueVideo(itemActive.getVideoId());
                        setDataview(itemActive);
                    }
                }
            }
        });
        updateData();
    }

    public void updateData() {
        setData();
        lblYeuthich.setSelected(Utils.itemCurrent.isLike());
        if (Utils.listLienquan != null && Utils.listLienquan.size() > 0) {
            addViewItemLienquan(Utils.listLienquan);
        } else {
            ResultCallBackLoad callBackLoad = new ResultCallBackLoad();
            loadingListview.setVisibility(View.VISIBLE);
            String url = Utils.host + "get_posts?count=5&page=6";
            new AysnRequestHttp((ViewGroup) mainView, Utils.LOAD_XEMNHIEU, MainActivity.smooth, callBackLoad).execute(url);
        }
        setDataview(Utils.itemCurrent);
        if (mPlayer != null) {
            mPlayer.cueVideo(videoId);
        } else {
            mYoutubeFragment.initialize(Utils.DEVELOPER_KEY_YOUTUBE, this);
        }
    }

    public class ResultCallBackLoad implements IResult {

        @Override
        public void getResult(int type, String result) {
            loadingListview.setVisibility(View.GONE);
            try {
                List<ItemPost> listViewNew = new ArrayList<ItemPost>();
                JSONObject jsonObj = new JSONObject(result);
                String status = jsonObj.getString("status");
                if (status.equals("ok")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("posts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemPost item = new ItemPost();
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        item = Utils.getItemPostRandom(json);
                        listViewNew.add(item);
                    }
                    addViewItemLienquan(listViewNew);
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

    public void addViewItemLienquan(List<ItemPost> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus().equals("publish")) {
                list.get(i).setType(PinnedAdapter.ITEM);
                adapterTab.add(list.get(i));
            }
        }
        adapterTab.notifyDataSetChanged();
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

    public void setData() {
        String sqlLike = "SELECT * FROM " + DatabaseHelper.TB_LIKE;
        List<ItemPost> listData = Utils.getVideoLike(sqlLike, PinnedAdapter.YEUTHICH);

        if (listData.size() == 0) {
            String url = Utils.host + "get_posts?count=20&page=2";
            ResultCallBack callBack = new ResultCallBack();
            if (!isLoadding && MainActivity.listVideoRanDom.size() == 0) {
                prLoadLike.setVisibility(View.VISIBLE);
                isLoadding = true;
                new AysnRequestHttp(mainView, Utils.LOAD_FIRST_DATA, null, callBack).execute(url);
            } else if (MainActivity.listVideoRanDom.size() != adapter.getCount()) {
                addViewData(MainActivity.listVideoRanDom);
            }
        } else if (listData.size() != adapter.getCount()) {
            addViewData(listData);
        }
    }

    public void addViewData(List<ItemPost> list) {
        adapter.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus().equals("publish")) {
                list.get(i).setType(PinnedAdapter.ITEM);
                adapter.add(list.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    public class ResultCallBack implements IResult {

        @Override
        public void getResult(int type, String result) {
            prLoadLike.setVisibility(View.INVISIBLE);
            isLoadding = false;
            Utils.disableEnableControls(true, (ViewGroup) mainView);

            try {
                MainActivity.listVideoRanDom = new ArrayList<ItemPost>();
                JSONObject jsonObj = new JSONObject(result);
                int count_total = jsonObj.getInt("count_total");
                if (count_total > 0) {
                    JSONArray jsonArray = jsonObj.getJSONArray("posts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemPost item = new ItemPost();
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        item = Utils.getItemPost(json, 0, 0);
                        item.setKeyRemove(Utils.LOAD_RADOM);

                        MainActivity.listVideoRanDom.add(item);

                    }
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    addViewData(MainActivity.listVideoRanDom);

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
            rightMenu.toggleMenu();
            itemActive = item;

        }

    }

    public void actionLike() {
        String sqlCheck = "SELECT * FROM " + DatabaseHelper.TB_LIKE + " WHERE id='" + itemActive.getIdPost() + "'";
        if (MainActivity.myDbHelper.getCountRow(DatabaseHelper.TB_LIKE, sqlCheck) == 0) {
            MainActivity.myDbHelper.insertVideoLike(id, cate, videoId, url, status, title, slug, countview);
        }
        // Utils.getDialogMessges(PlayerViewActivity.this,
        // "Video vừa được thêm vào danh sách yêu thích");

    }

    private void setDataview(ItemPost item) {
        itemActive = item;
        cate = item.getCateId();
        slug = item.getSlug();
        url = item.getUrl();
        title = item.getTitle();
        id = item.getIdPost();
        countview = item.getCountview();
        status = item.getStatus();
        videoId = item.getVideoId();
        inPostActive = id;

        lblTaskTitle.setText(Html.fromHtml(item.getTitle()));
        lblTitle.setText(Html.fromHtml(item.getTitle()));
        lblCountView.setText("Lượt xem: " + item.getCountview());

    }

    public class ResultOnclikTab implements IResult {

        @Override
        public void getResult(int type, String result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void pushResutClickItem(int type, int postion, boolean isLike) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCLickView(ItemPost item) {
            try {
                if (!item.getVideoId().equals(title)) {
                    mPlayer.cueVideo(item.getVideoId());
                    setDataview(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void settingWebView() {
        webview_fbview.getSettings().setJavaScriptEnabled(true);
        webview_fbview.setLongClickable(false);
        webview_fbview.getSettings().setBuiltInZoomControls(false);
        webview_fbview.getSettings().setLoadWithOverviewMode(true);
        webview_fbview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview_fbview.getSettings().setUseWideViewPort(true);
        webview_fbview.requestFocus(View.FOCUS_DOWN);
        webview_fbview.setPadding(0, 0, 0, 0);
        webview_fbview.setWebViewClient(new webViewClient());
        webview_fbview.setWebChromeClient(new webChromeClient());
        webview_fbview.setInitialScale(100);
        webview_fbview.clearCache(true);
        webview_fbview.clearHistory();
        webview_fbview.getSettings().setDefaultFontSize(14);
        webview_fbview.addJavascriptInterface(new JavaScriptInterface(mActivity), "Android");

        webview_fbview.setVisibility(View.VISIBLE);
        // webview_fbview.setWebViewClient(new WebViewClient() {
        // @Override
        // public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Log.d("url", url);
        //
        // if
        // (url.equalsIgnoreCase("https://m.facebook.com/plugins/login_success.php?refsrc=https%3A%2F%2Fm.facebook.com%2Fplugins%2Fcomments.php&refid=9&_rdr#_=_"))
        // {
        // loadComment("http://vtctube.vn/" + itemActive.getSlug());
        // }
        // return super.shouldOverrideUrlLoading(view, url);
        // }
        //
        //
        // });
    }

    public class JavaScriptInterface {
        Context mContext;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        // using Javascript to call the finish activity
        public void closeMyActivity() {
            // finish();
        }
    }

    private void loadComment(String url) {
        String script = "<div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&appId="
                + getResources().getString(R.string.app_id)
                + "&version=v2.0\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>";

        String commentBox = "<div class=\"fb-comments\" data-href=\"" + url
                + "\" data-numposts=\"30\" data-colorscheme=\"light\"></div>";

        String html = "<html><head><style type='text/css'>img { max-width: 100%%; width: auto; height: auto; } p { text-align: justify; width: auto; } </style></head><body style=\"margin: 0; padding: 0\">"
                + script + commentBox + "</body></html>";

        webview_fbview.loadDataWithBaseURL("http://9gag.tv", html, "text/html", null, null);
    }

    private class webChromeClient extends WebChromeClient {

        // display alert message in Web View
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new android.app.AlertDialog.Builder(view.getContext()).setMessage(message).setCancelable(true).show();
            result.confirm();
            return true;
        }
    }

    private class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar1.setVisibility(View.VISIBLE);
            webview_fbview.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.equalsIgnoreCase("https://m.facebook.com/plugins/login_success.php?refsrc=https%3A%2F%2Fm.facebook.com%2Fplugins%2Fcomments.php&refid=9&_rdr#_=_")) {
                loadComment("http://vtctube.vn/" + itemActive.getSlug());
            }
            progressBar1.setVisibility(View.GONE);

            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.mPlayer = player;
        if (!wasRestored && videoId.length() > 0) {
            player.cueVideo(videoId);
        }
    }

    @Override
    protected Provider getYouTubePlayerProvider() {
        return (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);
    }

    public void maximize() {
        mView.maximize();
    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video
     * when the DragglabePanel is maximized or closed.
     */
    private void hookDraggablePanelListeners() {
        mView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
            }

            @Override
            public void onMinimized() {
                playVideo();
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
            }
        });
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
    private void pauseVideo() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!mPlayer.isPlaying()) {
            mPlayer.play();
        }
    }
}
