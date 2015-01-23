package com.vtc.basetube.services.youtube;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.services.model.Item;
import com.vtc.basetube.services.model.Playlist;
import com.vtc.basetube.services.model.Result;
import com.vtc.basetube.services.model.Status;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.GsonRequest;
import com.vtc.basetube.utils.Utils;

public class YoutubeController {
    private static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";// "AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";
    private static final String QUANG_NINH_TV_CHANNEL = "UCgjeNGAHZI_X5vFeYYbKttA";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private static final String PLAYLISTS_URL = BASE_URL + "playlists?part=snippet,status";
    private static final String PLAYLIST_ITEMS_URL = BASE_URL + "playlistItems?part=snippet,status&key=" + API_KEY;

    private Context mContext;
    private String mApiKey;
    private String mChannelId;

    public YoutubeController(BaseTubeApplication app) {
        mContext = app.getApplicationContext();
        mApiKey = app.getApiKey();
        mChannelId = app.getChannelId();
    }

    public void requestPlaylists(final OnRequest<ArrayList<Category>> req) {
        StringBuilder url = new StringBuilder(PLAYLISTS_URL)
                            .append("&channelId=").append(mChannelId)
                            .append("&maxResults=50")
                            .append("&key=").append(mApiKey);
        GsonRequest<Result> request = new GsonRequest<Result>(Method.GET, url.toString(), Result.class, null,
                new Listener<Result>() {

                    @Override
                    public void onResponse(Result result) {
                        if (result.items == null) {
                            req.onError();
                            return;
                        }
                        ArrayList<Category> categories = new ArrayList<Category>();
                        for (Item item : result.items) {
                            Category cat = new Category();
                            cat.setId(item.id);
                            if (item.snippet != null) {
                                cat.setTitle(item.snippet.title);
                            }
                            categories.add(cat);
                        }
                        req.onSuccess(categories);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Log.d(Utils.TAG, "onErrorResponse: " + err.getMessage());
                    }
                });
        RequestManager.newInstance(mContext).addToRequestQueue(request);
    }

    public void requestPlaylistItems(Context context, final String playlistId, final OnRequest<ArrayList<Category>> req) {
        String url = PLAYLIST_ITEMS_URL + "&playlistId=" + playlistId;
        GsonRequest<Playlist> request = new GsonRequest<Playlist>(Method.GET, url, Playlist.class, null,
                new Listener<Playlist>() {

                    @Override
                    public void onResponse(Playlist playlists) {
                        Log.d(Utils.TAG, "requestPlaylistItems onResponse: ");
                        if (playlists.items == null) {
                            req.onError();
                            return;
                        }
                        ArrayList<Category> categories = new ArrayList<Category>();
                        for (Item playlist : playlists.items) {
                            if(playlist.status != null && Status.PRIVATE.equals(playlist.status.privacyStatus)) {
                                continue;
                            }
                            Category cat = new Category();
                            cat.setId(playlist.id);
                            cat.setTitle(playlist.snippet.title);
                            if (playlist.snippet.thumbnails != null && playlist.snippet.thumbnails.medium != null) {
                                cat.setThumbnail(playlist.snippet.thumbnails.medium.url);
                            }
                            categories.add(cat);
                        }
                        req.onSuccess(categories);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Log.d(Utils.TAG, "onErrorResponse: " + err.getMessage());
                    }
                });
        RequestManager.newInstance(context).addToRequestQueue(request);
    }
}
