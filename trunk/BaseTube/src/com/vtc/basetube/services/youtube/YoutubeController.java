package com.vtc.basetube.services.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.R;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.services.model.Playlist;
import com.vtc.basetube.services.model.Item;
import com.vtc.basetube.services.model.Result;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.GsonRequest;
import com.vtc.basetube.utils.Utils;

public class YoutubeController {
    private static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";// "AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";
    private static final String QUANG_NINH_TV_CHANNEL = "UCgjeNGAHZI_X5vFeYYbKttA";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private static final String PLAYLISTS_URL = BASE_URL + "playlists?part=snippet&key=" + API_KEY;
    private static final String PLAYLIST_ITEMS_URL = BASE_URL + "playlistItems?part=snippet&key=" + API_KEY;

    private Context mContext;
    private String mApiKey;
    private String mChannelId;
    private ArrayList<Category> mPlaylists;

    public YoutubeController() {
        mPlaylists = new ArrayList<Category>();
    }

    public YoutubeController(BaseTubeApplication app) {
        mContext = app.getApplicationContext();
        mApiKey = app.getApiKey();
        mChannelId = app.getChannelId();
        mPlaylists = new ArrayList<Category>();
        loadPlaylists(app.getPlaylistResource());
    }

    private void loadPlaylists(String file) {
        try {
            InputStream is = mContext.getAssets().open(file);
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            Gson gson = new Gson();
            Result result = gson.fromJson(reader, Result.class);
            if (result != null && result.items != null) {
                for (Item item : result.items) {
                    Category cat = new Category();
                    cat.setId(item.id);
                    if (item.snippet != null) {
                        cat.setTitle(item.snippet.title);
                        if (item.snippet.thumbnails != null && item.snippet.thumbnails.medium != null) {
                            cat.setThumbnail(item.snippet.thumbnails.medium.url);
                        }
                    }
                    mPlaylists.add(cat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Utils.TAG, e.getMessage());
        }
    }

    public ArrayList<Category> getPlaylists() {
        return mPlaylists;
    }

    public void requestPlaylists(Context context, final OnRequest<ArrayList<Category>> req) {
        String url = PLAYLISTS_URL + "&channelId=" + QUANG_NINH_TV_CHANNEL;
        GsonRequest<Playlist> request = new GsonRequest<Playlist>(Method.GET, url, Playlist.class, null,
                new Listener<Playlist>() {

                    @Override
                    public void onResponse(Playlist playlists) {
                        if (playlists.items == null) {
                            req.onError();
                            return;
                        }
                        ArrayList<Category> categories = new ArrayList<Category>();
                        for (Item playlist : playlists.items) {
                            Category cat = new Category();
                            cat.setId(playlist.id);
                            cat.setTitle(playlist.snippet.title);
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
