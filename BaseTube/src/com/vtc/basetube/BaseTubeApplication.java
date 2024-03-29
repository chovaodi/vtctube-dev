package com.vtc.basetube;

import android.app.Application;

public abstract class BaseTubeApplication extends Application {
    public static final String DEFAULT_API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";
    protected static final String BASE_CHANNEL = "channel/";
    

    public abstract String getApiKey();

    public abstract String getChannelId();

    public abstract String getPlaylistResource();
    
    
}
