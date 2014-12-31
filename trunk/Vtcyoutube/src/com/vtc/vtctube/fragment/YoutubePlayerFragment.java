package com.vtc.vtctube.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.vtc.vtctube.R;

public abstract class YoutubePlayerFragment extends Fragment implements OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    protected abstract YouTubePlayer.Provider getYouTubePlayerProvider();

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        Log.i("VTCTube", "onDetach: " + this);
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        Log.i("VTCTube", "onAttach: " + this);
        super.onAttach(activity);
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.i("VTCTube", "onDestroy: " + this);
        super.onDestroy();
    }
}
