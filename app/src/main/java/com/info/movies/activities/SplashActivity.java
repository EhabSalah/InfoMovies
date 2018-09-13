package com.info.movies.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.info.movies.MainActivity;
import com.info.movies.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    Thread thread;
    AsyncTask task;
    private static boolean isRunning;
    private static boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "onCreate: ");
        TextView tx = findViewById(R.id.welcome_text_frist);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/Quicksand-Regular.ttf");
        tx.setTypeface(custom_font);

        TextView t = findViewById(R.id.welcome_text_second);
        Typeface t_font = Typeface.createFromAsset(getAssets(), "font/Quicksand-Bold.ttf");
        t.setTypeface(t_font);

        hideSystemUI();

        isRunning = true;
        task = new Task().execute();

//        thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    sleep(3500);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    Log.d(TAG, "run: ");
//                    isRun = true;
//                    if(isRunning)
//                    {
//                        Log.d(TAG, "run: 1");
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        SplashActivity.this.finish();
//                        isRun=false;
//                    }
//                }
//            }
//        };
//        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        hideSystemUI();
        Log.d(TAG, "onResume: ");
        if (isRun) {
            Log.d(TAG, "onResume: 1");
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            isRun = false;
            SplashActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (task != null) {
            task.cancel(true);
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
//            findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public class Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000*getResources().getInteger(R.integer.splash_duration_seconds));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isRun = true;
            if (isRunning) {
                Log.d(TAG, "run: 1");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
                isRun = false;
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
