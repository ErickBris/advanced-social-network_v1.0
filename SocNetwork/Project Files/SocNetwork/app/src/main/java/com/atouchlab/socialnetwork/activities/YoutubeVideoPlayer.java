package com.atouchlab.socialnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.app.AppConst;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeVideoPlayer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private String videoID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_player);
        if (getIntent().hasExtra("videoID")) {
            videoID = getIntent().getExtras().getString("videoID");
        }
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtubeView);
        youTubeView.initialize(AppConst.APK_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "Oops", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored && videoID != null) {
            player.loadVideo(videoID);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(AppConst.APK_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtubeView);
    }
}
