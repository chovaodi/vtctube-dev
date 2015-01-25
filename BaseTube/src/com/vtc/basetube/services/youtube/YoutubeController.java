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
import com.vtc.basetube.services.model.Id;
import com.vtc.basetube.services.model.Item;
import com.vtc.basetube.services.model.Result;
import com.vtc.basetube.services.model.ResultSearch;
import com.vtc.basetube.services.model.Status;
import com.vtc.basetube.services.request.RequestManager;
import com.vtc.basetube.services.volley.toolbox.GsonRequest;
import com.vtc.basetube.utils.Utils;

public class YoutubeController {
    private static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";// "AIzaSyDsQDuxjOZLCiwx9MKIa_LTPhYPHV293L8";
    private static final String QUANG_NINH_TV_CHANNEL = "UCgjeNGAHZI_X5vFeYYbKttA";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private static final String PLAYLISTS_URL = BASE_URL + "playlists?part=snippet,status";
    private static final String PLAYLIST_ITEMS_URL = BASE_URL + "playlistItems?part=snippet,status,contentDetails";
    private static final String VIDEO_LIST_URL = BASE_URL + "videos?part=snippet,contentDetails,statistics";
    private static final String SEARCH_URL = BASE_URL + "search?part=id&type=video";
    private static final String RELATIVE_VIDEO_URL = BASE_URL + "search?part=id&type=video";

    private Context mContext;
    private String mApiKey;
    private String mChannelId;

    public YoutubeController(BaseTubeApplication app) {
        mContext = app.getApplicationContext();
        mApiKey = app.getApiKey();
        mChannelId = app.getChannelId();
    }

    public void requestPlaylists(final OnRequest<ArrayList<Category>> req) {
        StringBuilder url = new StringBuilder(PLAYLISTS_URL).append("&channelId=").append(mChannelId).append("&maxResults=50")
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
                        for (Item<String> item : result.items) {
                            Category cat = new Category();
                            cat.setId(item.id);
                            cat.setPlaylistId(item.id);
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
        StringBuilder url = new StringBuilder(PLAYLIST_ITEMS_URL).append("&playlistId=").append(playlistId).append("&key=")
                .append(mApiKey);
        ;
        GsonRequest<Result> request = new GsonRequest<Result>(Method.GET, url.toString(), Result.class, null,
                new Listener<Result>() {

                    @Override
                    public void onResponse(Result data) {
                        if (data.items == null) {
                            req.onError();
                            return;
                        }
                        ArrayList<Category> categories = new ArrayList<Category>();
                        StringBuilder videoIds = new StringBuilder();
                        for (Item<String> item : data.items) {
                            if (item.status != null && Status.PRIVATE.equals(item.status.privacyStatus)) {
                                continue;
                            }
                            if (item.contentDetails != null) {
                                videoIds.append(item.contentDetails.videoId).append(",");
                            }
                        }
                        String ids = videoIds.toString();
                        Log.d(Utils.TAG, "VIDEO_IDS: " + ids);
                        requestVideoList(mContext, playlistId, ids, req);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Log.d(Utils.TAG, "onErrorResponse: " + err.getMessage());
                    }
                });
        RequestManager.newInstance(context).addToRequestQueue(request);
    }

    public void requestVideoList(Context context, final String playlistId, final String videoId,
            final OnRequest<ArrayList<Category>> req) {
        StringBuilder url = new StringBuilder(VIDEO_LIST_URL).append("&id=").append(videoId).append("&key=").append(mApiKey);
        GsonRequest<Result> request = new GsonRequest<Result>(Method.GET, url.toString(), Result.class, null,
                new Listener<Result>() {

                    @Override
                    public void onResponse(Result data) {
                        Log.d(Utils.TAG, "requestPlaylistItems onResponse: ");
                        if (data.items == null) {
                            req.onError();
                            return;
                        }
                        ArrayList<Category> categories = new ArrayList<Category>();
                        for (Item<String> item : data.items) {
                            Category cat = new Category();
                            cat.setId(item.id);
                            if (item.snippet != null) {
                                cat.setDescription(item.snippet.description);
                                cat.setTitle(item.snippet.title);
                                cat.setPublishAt(item.snippet.publishedAt);
                                if (item.snippet.thumbnails != null && item.snippet.thumbnails.medium != null) {
                                    cat.setThumbnail(item.snippet.thumbnails.medium.url);
                                }
                            }
                            if (item.statistics != null) {
                                cat.setCountView(item.statistics.viewCount);
                            }
                            if (item.contentDetails != null) {
                                cat.setDuration(item.contentDetails.duration);
                            }
                            if (playlistId != null) {
                                cat.setPlaylistId(playlistId);
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

    public void requestSearchVideos(Context context, final String querry, final OnRequest<ArrayList<Category>> req) {
        StringBuilder url = new StringBuilder(SEARCH_URL).append("&q=").append(querry).append("&channelId=").append(mChannelId)
                .append("&maxResults=10").append("&key=").append(mApiKey);
        ;
        GsonRequest<ResultSearch> request = new GsonRequest<ResultSearch>(Method.GET, url.toString(), ResultSearch.class, null,
                new Listener<ResultSearch>() {

                    @Override
                    public void onResponse(ResultSearch data) {
                        if (data.items == null) {
                            req.onError();
                            return;
                        }
                        StringBuilder videoIds = new StringBuilder();
                        for (Item<Id> item : data.items) {
                            if (item.id != null) {
                                videoIds.append(item.id.videoId).append(",");
                            }
                        }
                        String ids = videoIds.toString();
                        Log.d(Utils.TAG, "VIDEO_IDS: " + ids);
                        requestVideoList(mContext, null, ids, req);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Log.d(Utils.TAG, "onErrorResponse: " + err.getMessage());
                    }
                });
        RequestManager.newInstance(context).addToRequestQueue(request);
    }

    public void requestRelatedVideos(Context context, final String videoId, final OnRequest<ArrayList<Category>> req) {
        StringBuilder url = new StringBuilder(RELATIVE_VIDEO_URL).append("&channelId=").append(mChannelId)
                .append("&maxResults=10").append("&relatedToVideoId=").append(videoId).append("&key=").append(mApiKey);
        ;
        GsonRequest<ResultSearch> request = new GsonRequest<ResultSearch>(Method.GET, url.toString(), ResultSearch.class, null,
                new Listener<ResultSearch>() {

                    @Override
                    public void onResponse(ResultSearch data) {
                        if (data.items == null) {
                            req.onError();
                            return;
                        }
                        StringBuilder videoIds = new StringBuilder();
                        for (Item<Id> item : data.items) {
                            if (item.id != null) {
                                videoIds.append(item.id.videoId).append(",");
                            }
                        }
                        videoIds.append(videoId);
                        String ids = videoIds.toString();
                        Log.d(Utils.TAG, "VIDEO_IDS: " + ids);
                        requestVideoList(mContext, null, ids, req);
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
