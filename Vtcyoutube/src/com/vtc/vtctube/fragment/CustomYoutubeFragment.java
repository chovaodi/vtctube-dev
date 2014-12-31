package com.vtc.vtctube.fragment;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.vtc.vtctube.utils.Utils;

public class CustomYoutubeFragment extends YouTubePlayerSupportFragment {
    private YouTubePlayer activePlayer;

    public static CustomYoutubeFragment newInstance(String url) {
        CustomYoutubeFragment fragment = new CustomYoutubeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        fragment.init();
        return fragment;
    }

    private void init() {

        initialize(Utils.DEVELOPER_KEY_YOUTUBE, new OnInitializedListener() {

            @Override
            public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                Bundle bundle = getArguments();
                if (!wasRestored && bundle != null) {
                    String videoId = bundle.getString("url");
                    if (videoId.isEmpty() == false) {
                        activePlayer.cueVideo(videoId); // .loadVideo(getArguments().getString("url"),
                                                        // 0);
                    }
                }
            }
        });
    }
}
