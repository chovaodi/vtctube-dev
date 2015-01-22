package com.vtc.basetube;

public class QuangNinhTvApplication extends BaseTubeApplication {
    private static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";
    private static final String CHANNEL_ID = "UCgjeNGAHZI_X5vFeYYbKttA";
    private static final String PLAYLISTS = BASE_CHANNEL + "playlists_quangninhtv";

    private static QuangNinhTvApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static BaseTubeApplication getInstance() {
        return sInstance;
    }

    @Override
    public String getApiKey() {
        return API_KEY;
    }

    @Override
    public String getChannelId() {
        return CHANNEL_ID;
    }

    @Override
    public String getPlaylistResource() {
        return PLAYLISTS;
    }

}
