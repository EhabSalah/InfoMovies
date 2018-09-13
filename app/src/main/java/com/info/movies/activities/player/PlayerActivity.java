package com.info.movies.activities.player;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.info.movies.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class PlayerActivity extends YouTubeBaseActivity {
    private static final String TAG = PlayerActivity.class.getSimpleName();
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    int noti_id;
    String video_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        receiveData(getIntent());
//        fab_back = findViewById(R.id.fab_back);
//        fab_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(video_key);
                youTubePlayer.setFullscreen(true);
                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        Log.d(TAG, "onFullscreen: b= " + b);
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                finish();
            }
        };
        try {
            youTubePlayerView.initialize(PlayerConfig.GOOGLE_API_KEY, onInitializedListener);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            onBackPressed();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        setIconTextColorOfRecentAppState(this);

    }

    private void receiveData(Intent intent) {
        video_key = intent.getStringExtra("trailer_key");
        noti_id = intent.getIntExtra("noti_id", 0);
        if (noti_id != 0) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert nm != null;
            nm.cancel(noti_id);
            Log.d(TAG, "receiveData: noti_id = " + noti_id);
        }
        Log.d(TAG, "receiveData: video_key = " + video_key);
    }
}
