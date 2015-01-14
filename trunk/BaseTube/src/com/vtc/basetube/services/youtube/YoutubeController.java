package com.vtc.basetube.services.youtube;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.vtc.basetube.model.Category;
import com.vtc.basetube.services.model.Playlist;
import com.vtc.basetube.services.model.Item;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.GsonRequest;
import com.vtc.basetube.utils.Utils;

public class YoutubeController {
    private static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";//"AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";
    private static final String QUANG_NINH_TV_CHANNEL = "UCgjeNGAHZI_X5vFeYYbKttA";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private static final String PLAYLISTS_URL = BASE_URL + "playlists?part=snippet&key=" + API_KEY;
    private static final String PLAYLIST_ITEMS_URL = BASE_URL + "playlistItems?part=snippet&key=" + API_KEY;

    public void requestPlaylists(Context context, final OnRequest<ArrayList<Category>> req) {
        String url = PLAYLISTS_URL +"&channelId="+QUANG_NINH_TV_CHANNEL;
        GsonRequest<Playlist> request = new GsonRequest<Playlist>(Method.GET, url, Playlist.class, null, new Listener<Playlist>() {

            @Override
            public void onResponse(Playlist playlists) {
                if(playlists.items == null) {
                    req.onError();
                    return;
                }
                ArrayList<Category> categories = new ArrayList<Category>();
                for(Item playlist : playlists.items) {
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
        String url = PLAYLIST_ITEMS_URL +"&playlistId="+playlistId;
        GsonRequest<Playlist> request = new GsonRequest<Playlist>(Method.GET, url, Playlist.class, null, new Listener<Playlist>() {

            @Override
            public void onResponse(Playlist playlists) {
                Log.d(Utils.TAG, "requestPlaylistItems onResponse: ");
                if(playlists.items == null) {
                    req.onError();
                    return;
                }
                ArrayList<Category> categories = new ArrayList<Category>();
                for(Item playlist : playlists.items) {
                    Category cat = new Category();
                    cat.setId(playlist.id);
                    cat.setTitle(playlist.snippet.title);
                    if(playlist.snippet.thumbnails != null && playlist.snippet.thumbnails.medium != null) {
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
