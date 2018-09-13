package com.info.movies.constants.local_notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.info.movies.constants.Common;
import com.info.movies.db.DBHelper;

/**
 * Created by Ehab Salah on 3/27/2018.
 */

public class AddToWatchlistActionReceiver extends BroadcastReceiver {
    private static final String TAG = AddToWatchlistActionReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 1");
        int noti_id = intent.getIntExtra("noti_id", 0);
        int movie_id = intent.getIntExtra("movie_id", 0);
        String movie_poster = intent.getStringExtra("movie_poster");
        String movie_title = intent.getStringExtra("movie_title");
        String movie_genres = intent.getStringExtra("movie_genres");

        if (noti_id != 0 && movie_id != 0) {
            Log.d(TAG, "onReceive: 2");

            Log.d(TAG, "onReceive: noti_id = " + noti_id);
            Log.d(TAG, "onReceive: movie_id = " + movie_id);
            Log.d(TAG, "onReceive: movie_poster = " + movie_poster);
            Log.d(TAG, "onReceive: movie_title = " + movie_title);
            Log.d(TAG, "onReceive: movie_genres = " + movie_genres);
            if (Common.isMovieAddedToWatchlist(String.valueOf(movie_id), context)) { // MOVIE EXISTS IN WATCHLIST
                Log.d(TAG, "onReceive: 3");

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(noti_id);
                Toast toast = Toast.makeText(context, "Movie already exists in your Watchlist!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else { //// MOVIE NOT EXISTS IN WATCHLIST
                Log.d(TAG, "onReceive: 4");

                DBHelper dbHelber = new DBHelper(context); // db declaration
                SQLiteDatabase sqLiteDatabase = dbHelber.getWritableDatabase();

                if (dbHelber.insertMovie(String.valueOf(movie_id), movie_title, movie_poster, movie_genres, sqLiteDatabase)) {
                    Log.d(TAG, "onReceive: 5");
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(noti_id);
                    Toast toast = Toast.makeText(context, "Added to Watchlist!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Log.d(TAG, "onReceive: 6");
                }
                dbHelber.close(); // close connection to database.
            }
        }
    }
}
