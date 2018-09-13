package com.info.movies.constants.local_notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.info.movies.MainActivity;
import com.info.movies.activities.player.PlayerActivity;
import com.info.movies.R;
import com.info.movies.constants.Common;
import com.info.movies.constants.GlideApp;
import com.info.movies.constants.Setting;
import com.info.movies.constants.Utils;
import com.info.movies.models.movie_details.DetailsResponse;
import com.info.movies.models.movie_details.Genre;
import com.info.movies.models.movie_details.Videos;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.models.movies_response.Result;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.ApiInterface;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.info.movies.constants.Common.getTomorrowDateSF;
import static com.info.movies.constants.local_notification.MyAlarm.cancelAlarm;

/**
 * Created by mohamedhussin on 2/6/18.
 */

public class MyAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = MyAlarmReceiver.class.getSimpleName();
    ApiInterface apiInterface;
    private CharSequence CHANNEL_NAME = "InfoMovies";
    String CHANNEL_ID = "info_movies";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: MyAlarmReceiver 1");
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.notification_data_key), MODE_PRIVATE);
        boolean b = prefs.getBoolean(context.getString(R.string.notification_switch_key), false);

        if (b) {
            Log.d(TAG, "onReceive:  1.1 ");

//            Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: MyAlarmReceiver 2");
            if (Utils.isNetworkAvailable(context)) {
                Log.d(TAG, "onReceive:  3");
                doRequest(context);
            } else // mn hna ana 3yz a3ml variable a2olo enta b true l'3'ayet m l user yfta3 el wifi awl m l wifi ychta3'al,, a3ml receiver, w a2olo l el variable da b true, roo7 e3ml request + setAlarm() b3d keda.
            {
                Log.d(TAG, "onReceive:  4");
                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.N) {
                    SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.notification_data_key), MODE_PRIVATE).edit();
                    editor.putBoolean(context.getString(R.string.notification_missed_name), true);
                    editor.apply();
                    Log.d(TAG, "onReceive: notification_missed_name = " + context.getSharedPreferences(context.getString(R.string.notification_data_key), MODE_PRIVATE).getBoolean(context.getString(R.string.notification_missed_name), false));
                    cancelAlarm(context);
                }
            }
        } else {
            Log.d(TAG, "onReceive: MyAlarmReceiver 5");
        }
    }

    public void doRequest(final Context context) {
        Log.d(TAG, "doRequest: ");
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call call = apiInterface.getDailyUpComming(getTomorrowDateSF(),getTomorrowDateSF(),getTomorrowDateSF(),getTomorrowDateSF());
//        Call call = apiInterface.getDailyUpComming("2018-5-18", "2018-6-18", "2018-5-18", "2018-6-18");
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Log.d(TAG, "onResponse: 1");
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 2");

                    checkFavouriteMovies(response.body(), context);


                } else  // server error
                {
                    Log.d(TAG, "onResponse: 3");
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t)  // network connection error
            {
                Log.d(TAG, "onFailure: 1");
                Log.d(TAG, "onFailure: 2 : " + t.getMessage());
            }
        });
    }

    private void checkFavouriteMovies(Movies response, Context context) {
        Log.d(TAG, "checkFavouriteMovies: 1");
        mSetting = new Setting(context);
        int[] favourite_genres_nos = mSetting.getNotificationSavedGenres();
        if (favourite_genres_nos != null) {
            ArrayList<Integer> favourite_genres_ids = Common.getGenresCodesByNumbers(favourite_genres_nos);
            Log.d(TAG, "checkFavouriteMovies: favourite_genres_nos =" + Arrays.toString(favourite_genres_nos));
            Log.d(TAG, "checkFavouriteMovies: favourite_genres_ids =" + favourite_genres_ids);
            List<Result> movies_list = response.getMoviesList();
            if (!movies_list.isEmpty()) {
                Log.d(TAG, "checkFavouriteMovies: 1.5: movies_list = " + new Gson().toJson(movies_list));
                for (Result movie : movies_list) {
                    Log.d(TAG, "checkFavouriteMovies: 2");

                    List<Integer> genres = movie.getGenre_ids();

                    if (genres != null && !genres.isEmpty()) {

                        for (int x : genres) {
                            Log.d(TAG, "checkFavouriteMovies: 3 x = " + x + " MY GENRES = " + favourite_genres_ids);

                            if (favourite_genres_ids.contains(x)) {// keda el movie da favourite genre ll user.
                                Log.d(TAG, "checkFavouriteMovies: 4");

                                // if saved shared notified movies ids.
                                String movie_id = movie.getId();
                                ArrayList<Integer> notified_movies = (ArrayList<Integer>) mSetting.getNotifiedMoviesIds();
                                if (!notified_movies.contains(Integer.parseInt(movie_id)) &&
                                        movie.getOverview() != null &&
                                        movie.getPoster_path() != null &&
                                        movie.getTitle() != null) {
                                    Log.d(TAG, "checkFavouriteMovies: 5");
                                    // i will notify the user title  = "Movie title ", content Released today + genres + tab to see more details
                                    checkTrailer(movie_id, notified_movies, context);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "checkFavouriteMovies: 5.5 Movies list is empty !!!!");
            }
        } else {
            Log.d(TAG, "checkFavouriteMovies:6  NO Selected Genres !!!!!!!!");
            List<Result> movies_list = response.getMoviesList();
            if (!movies_list.isEmpty()) {
                Log.d(TAG, "checkFavouriteMovies: 7: movies_list = " + new Gson().toJson(movies_list));
                for (Result movie : movies_list) {
                    Log.d(TAG, "checkFavouriteMovies: 8");

                    List<Integer> genres = movie.getGenre_ids();

                    if (genres != null && !genres.isEmpty()) {

                        Log.d(TAG, "checkFavouriteMovies: 9");

                        // if saved shared notified movies ids.
                        String movie_id = movie.getId();
                        ArrayList<Integer> notified_movies = (ArrayList<Integer>) mSetting.getNotifiedMoviesIds();
                        if (!notified_movies.contains(Integer.parseInt(movie_id)) &&
                                movie.getOverview() != null &&
                                movie.getPoster_path() != null &&
                                movie.getTitle() != null) {
                            Log.d(TAG, "checkFavouriteMovies: 10");
                            // i will notify the user title  = "Movie title ", content Released today + genres + tab to see more details
                            checkTrailer(movie_id, notified_movies, context);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void checkTrailer(String movie_id, final ArrayList<Integer> notified_movies, final Context context) {
        Log.d(TAG, "checkTrailer: 1");
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call call = apiInterface.getMovieDetails(movie_id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: checkTrailer: 1");
                    extractData((DetailsResponse) response.body(), notified_movies, context);
                } else {
                    Log.d(TAG, "onResponse: checkTrailer: 2");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "onFailure: checkTrailer: 2");
            }
        });
    }

    private void extractData(DetailsResponse detailsRespons, ArrayList<Integer> notified_movies, Context context) {
        String trailer_key = extractTrailerKey(detailsRespons.getVideos());// extract official trailer -**************************************************-
        // yb2a e7na 3yzeen el trailer + movie name + overview
        String movie_title = detailsRespons.getTitle();
        String id = detailsRespons.getId();
        String movie_genres = extractGenres(detailsRespons.getGenre());// 2
        Log.d(TAG, "extractData: movie_genres = " + movie_genres);
        String overView = detailsRespons.getOverview();                                               // 8
        String poster = detailsRespons.getPoster_path();
        // will push notificatiion of the picture type.
        pushNotification(Integer.parseInt(id), context, notified_movies, poster, movie_title, movie_genres, overView, trailer_key, new Gson().toJson(detailsRespons));

    }

    private static String extractGenres(List<Genre> genre) {
        String g = "";
        StringBuilder genres = new StringBuilder();
        if (genre != null && !genre.isEmpty()) {
            for (int i = 0; i < genre.size(); i++) {
                if (genres.length() == 0) {
                    genres = new StringBuilder(genre.get(i).getName());
                } else if (genres.length() > 0) {
                    genres.append(" | ").append(genre.get(i).getName());
                }
            }
        }
        return genres.toString();
    }

    private static String extractTrailerKey(Videos videos) {
        String key;
        List<com.info.movies.models.movie_details.Result> video_results = videos.getResults();
        if (!video_results.isEmpty()) {
            for (int i = 0; i < video_results.size(); i++) {

                if (video_results.get(i).getType().toLowerCase().contains("trailer")) {
                    if (video_results.get(i).getSite().trim().toLowerCase().contains("youtube")) {
                        if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("official")) {
                            key = video_results.get(i).getKey();
                            return key;
                        } else if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("trailer")) {
                            key = video_results.get(i).getKey();
                            return key;
                        }
                    }
                }
            }
            for (int i = 0; i < video_results.size(); i++) {
                if (video_results.get(i).getType().toLowerCase().contains("teaser")) {
                    if (video_results.get(i).getSite().toLowerCase().trim().toLowerCase().contains("youtube")) {
                        if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("official")) {
                            key = video_results.get(i).getKey();
                            return key;
                        } else if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("teaser")) {
                            key = video_results.get(i).getKey();
                            return key;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static NotificationManager mNotificationManager;
    Setting mSetting;
    private final static int NORMAL = 0x00;
    private final static int BIG_TEXT_STYLE = 0x01;

    private final static int BIG_PICTURE_STYLE = 0x02;

    private void pushNotification(int movie_id, Context context, ArrayList<Integer> notified_movies, String poster, String movie_title, String movie_genres, String overView, String trailer_key, String movie_details) {
        Log.d(TAG, "pushNotification: 1");

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =  new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        if (trailer_key == null) {
            Log.d(TAG, "pushNotification: genres = movie_genres " + movie_genres);
            new CreateNotification(BIG_TEXT_STYLE, context, poster, movie_title, movie_genres, overView, null, movie_details, movie_id).execute();
        } else {
            new CreateNotification(BIG_PICTURE_STYLE, context, poster, movie_title, overView, movie_genres, trailer_key, movie_details, movie_id).execute();
        }
        notified_movies.add(movie_id);
        mSetting.addNotifiedMovieId(notified_movies);
    }

    public class CreateNotification extends AsyncTask<Void, Void, Void> {

        private final String movie_details;
        private final String trailer_key;
        private final String movie_overview;
        private final String movie_genres;
        private final String movie_title;
        private final String movie_poster;
        private final int movie_id;
        Context context;
        int style = NORMAL;

        public CreateNotification(int style, Context context, String poster, String movie_title, String overView, String movie_genres, String trailer_key, String movie_details, int movie_id) {
            this.context = context;
            this.style = style;
            this.movie_title = movie_title;
            this.movie_genres = movie_genres;
            this.movie_overview = overView;
            this.trailer_key = trailer_key;
            this.movie_details = movie_details;
            this.movie_poster = poster;
            this.movie_id = movie_id;

        }

        @Override
        protected Void doInBackground(Void... params) {
            Notification noti = new Notification();
            int noti_id = new Random().nextInt(10000);
            switch (style) {

                case BIG_PICTURE_STYLE:
                    try {
                        noti = setBigPictureStyleNotification(noti_id, context, movie_title, movie_genres, movie_details, trailer_key, movie_id, movie_poster);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case BIG_TEXT_STYLE:
                    noti = setBigTextStyleNotification(noti_id, context, movie_poster, movie_title, movie_overview, movie_genres, movie_details, movie_id, movie_poster);
                    break;

            }

            if (noti != null) {
                noti.defaults |= Notification.DEFAULT_LIGHTS;
                noti.defaults |= Notification.DEFAULT_VIBRATE;
                noti.defaults |= Notification.DEFAULT_SOUND;

                noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
//                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                mNotificationManager.notify(noti_id, noti);

            }

            return null;

        }
    }

    private Notification setBigPictureStyleNotification(int noti_id, Context context, String movie_title, String movie_genres, String movie_details, String trailer_key, int movie_id, String movie_poster) throws ExecutionException, InterruptedException {

        Log.d(TAG, "setBigPictureStyleNotification: ");

        Bitmap remote_picture = null;

        // Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(movie_title); // title when expanded
        notiStyle.setSummaryText(context.getString(R.string.big_pic_notification_summary)); // subtitle when expanded

        // Add the big picture to the style.

        try {
            remote_picture = GlideApp.with(context)
                    .asBitmap()
                    .load("https://i1.ytimg.com/vi/" + trailer_key + "/hqdefault.jpg")
                    .centerInside()
                    .submit().get();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        notiStyle.bigPicture(remote_picture);


        Intent detailsIntent = new Intent(context, MainActivity.class);   // CONTENT ACTION
        detailsIntent.putExtra("noti_id", noti_id); // d l data
        detailsIntent.putExtra("movie_title", movie_title); // d l data
        detailsIntent.putExtra("movie_id", movie_id); // d l data
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(detailsIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(new Random().nextInt(1000), PendingIntent.FLAG_UPDATE_CURRENT);// this is the target

        Intent add_to_watchlist_action_intent = new Intent(context, AddToWatchlistActionReceiver.class); // WATCHLIST ACTION
        add_to_watchlist_action_intent.putExtra("noti_id", noti_id);
        add_to_watchlist_action_intent.putExtra("movie_id", movie_id);
        add_to_watchlist_action_intent.putExtra("movie_poster", movie_poster);
        add_to_watchlist_action_intent.putExtra("movie_title", movie_title);
        add_to_watchlist_action_intent.putExtra("movie_genres", movie_genres);
        PendingIntent add_to_watchlist_pending_intent = PendingIntent.getBroadcast(context, new Random().nextInt(1000), add_to_watchlist_action_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder player_stack = TaskStackBuilder.create(context);
        player_stack.addParentStack(MainActivity.class);
        Intent main = new Intent(context, MainActivity.class);
        player_stack.addNextIntent(main);
        Intent play_tailer_intent = new Intent(context, PlayerActivity.class); // PLAY TRAILER ACTION
        play_tailer_intent.putExtra("trailer_key", trailer_key);
        play_tailer_intent.putExtra("noti_id", noti_id);
        player_stack.addNextIntent(play_tailer_intent);
        PendingIntent play_trailer_pending_intent = player_stack.getPendingIntent(new Random().nextInt(1000), PendingIntent.FLAG_UPDATE_CURRENT);
        int ic_add_watchlist;
        int ic_play;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ic_add_watchlist = R.drawable.ic_notification_add_watchlist_dark;
            ic_play = R.drawable.ic_notification_play_dark;
        } else {
            ic_add_watchlist = R.drawable.ic_notification_add_watchlist_light;
            ic_play = R.drawable.ic_notificarion_play_light;
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(ic_play, context.getString(R.string.notification_watch_trailer_action), play_trailer_pending_intent)
                .addAction(ic_add_watchlist, context.getString(R.string.notification_watchlist_action), add_to_watchlist_pending_intent)
                .setContentTitle(context.getString(R.string.notification_pig_pic_content_title)) // title when collapsed
                .setContentText(movie_genres) // subtitle when collapsed
                .setStyle(notiStyle)
//                .setChannelId(CHANNEL_ID)
                .build();
    }

    private Notification setBigTextStyleNotification(int noti_id, Context context, String movie_poster, String movie_title, String movie_genres, String movie_overview, String movie_details, int movie_id, String moviePoster) {
        Bitmap remote_picture = null;

        Log.d(TAG, "setBigTextStyleNotification: ");
        // Create the style object with BigTextStyle subclass.
        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();


        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL("https://image.tmdb.org/t/p/w185/" + movie_poster).getContent());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        notiStyle.setBigContentTitle(movie_title);
        notiStyle.setSummaryText(movie_genres);
        // Add the big text to the style.
        CharSequence bigText = movie_overview;
        notiStyle.bigText(bigText);

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("noti_id", noti_id);
        resultIntent.putExtra("movie_title", movie_title);
        resultIntent.putExtra("movie_id", movie_id);
        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(new Random().nextInt(1000), PendingIntent.FLAG_UPDATE_CURRENT);

        // add to watch list action.
        Intent add_to_watchlist_action_intent = new Intent(context, AddToWatchlistActionReceiver.class);
        add_to_watchlist_action_intent.putExtra("noti_id", noti_id);
        add_to_watchlist_action_intent.putExtra("movie_id", movie_id);
        add_to_watchlist_action_intent.putExtra("movie_poster", movie_poster);
        add_to_watchlist_action_intent.putExtra("movie_title", movie_title);
        add_to_watchlist_action_intent.putExtra("movie_genres", movie_genres);
        PendingIntent add_to_watchlist_pending_intent = PendingIntent.getBroadcast(context, new Random().nextInt(1000), add_to_watchlist_action_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int ic_add_watchlist;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ic_add_watchlist = R.drawable.ic_notification_add_watchlist_dark;
        } else {
            ic_add_watchlist = R.drawable.ic_notification_add_watchlist_light;
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(ic_add_watchlist, context.getResources().getString(R.string.notification_watchlist_action), add_to_watchlist_pending_intent)
                .setContentTitle(context.getString(R.string.notification_big_text_content_title))
                .setContentText(movie_overview)
                .setStyle(notiStyle)
//                .setChannelId(CHANNEL_ID)
                .build();
    }
}
