package com.vtc.basetube;

public class QuangNinhTvApplication extends BaseTubeApplication {
    public static final String API_KEY = "AIzaSyCD3P8Bd1Nfo9GtMLyN53Qg7V3mQWMtrvo";
    public static final String CHANNEL_ID = "UCgjeNGAHZI_X5vFeYYbKttA";

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
    public int getPlaylistResource() {
        return R.raw.playlists_quangninhtv;
    }

}
