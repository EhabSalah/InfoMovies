package com.info.movies.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;


import com.info.movies.R;
import com.info.movies.models.sign_up.GuestSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by EhabSalah on 8/23/2017.
 */

public class Setting {
    private static final String TAG = String.class.getSimpleName();

    private final String NOWPLAYING_GENRE_KEY;
    private final String POPULAR_GENRE_KEY;
    private final String UPCOMING_GENRE_KEY;
    private final String TOP_RATED_GENRE_KEY;
    private final String TOP_RATED_YEAR_KEY;
    private final String UPCOMING_MAXIMUM_DATE_KEY;
    private final String DAILY_NOTIFICATION;
    private Context ctx;
    private static String SHARED_DATA_FILE;
    private final String SHARED_NOTIFOCATIONS_DATA_FILE;
    private final String POSTER_INFO_VIEW_KEY;
    private final String TOOLBAR_NAVIGATION_STATE_KEY;
    private ArrayList<String> years;
    private ArrayList<String> genres;
    public String spinners_default_word;


    public Setting(Context ctx) {
        this.ctx = ctx;
        this.SHARED_DATA_FILE = ctx.getResources().getString(R.string.user_info_file_key);
        SHARED_NOTIFOCATIONS_DATA_FILE = ctx.getString(R.string.notification_data_key);
        this.POSTER_INFO_VIEW_KEY = ctx.getResources().getString(R.string.poster_info_display_key);
        this.UPCOMING_MAXIMUM_DATE_KEY = ctx.getString(R.string.upcoming_maximum_date_key);
        TOOLBAR_NAVIGATION_STATE_KEY = ctx.getString(R.string.key_user_shared_navigation_state);
        TOP_RATED_GENRE_KEY = ctx.getString(R.string.key_top_rated_genre_key);
        TOP_RATED_YEAR_KEY = ctx.getString(R.string.key_top_rated_year_key);
        spinners_default_word = ctx.getString(R.string.spinners_default_word_top_rated);
        UPCOMING_GENRE_KEY = ctx.getString(R.string.upcoming_genre_key);
        POPULAR_GENRE_KEY = ctx.getString(R.string.popular_genre_key);
        NOWPLAYING_GENRE_KEY = ctx.getString(R.string.nowplaying_genre_key);
        DAILY_NOTIFICATION = ctx.getString(R.string.notification_switch_key);
        years = new ArrayList<>();
        genres = new ArrayList<>();
    }

    public ArrayList<String> getGenres() {
        if (genres != null && !genres.isEmpty() && genres.get(0).equals(spinners_default_word) && genres.size() == Common.basic_geners.size()) {
            Log.d(TAG, "getGenres: 1 ");
            return genres;
        } else {
            Log.d(TAG, "getGenres: 2 ");
            return genresList();

        }
    }

    private ArrayList<String> genresList() {
        genres = new ArrayList<>();
        genres.add(spinners_default_word);
        for (int i = 0; i < Common.basic_geners.size(); i++) {
            String genre = Common.basic_geners.get(i).getName();
            genres.add(genre);
        }
        return genres;
    }

    public ArrayList<String> getYears() {
        if (years != null && !years.isEmpty() && years.contains(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))) && years.get(0).equals(spinners_default_word)) {
            return years;
        } else {
            return yearsList();
        }
    }

    public ArrayList<String> yearsList() {

        years = new ArrayList<>();
        years.add(spinners_default_word);
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= ctx.getResources().getInteger(R.integer.minimumYear); i--) {
            years.add(String.valueOf(i));
        }
        return years;
    }

    public void saveToolbarNavigationState(boolean isNormal) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TOOLBAR_NAVIGATION_STATE_KEY, isNormal);
        editor.apply();
    }

    public boolean getNavigationState() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getBoolean(TOOLBAR_NAVIGATION_STATE_KEY, false);
    }

    public void savePosterInfoView(boolean show) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(POSTER_INFO_VIEW_KEY, show);
        editor.apply();
        Log.d(TAG, "savePosterInfoView: " + show);
    }

    public boolean getPosterInfoView() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        Log.d(TAG, "getPosterInfoView: " + sharedPreferences.getBoolean(POSTER_INFO_VIEW_KEY, false));
        return sharedPreferences.getBoolean(POSTER_INFO_VIEW_KEY, false);
    }

    public void saveMaximumDateSF(String d) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UPCOMING_MAXIMUM_DATE_KEY, d);
        editor.apply();
    }

    public String getMaximumDateSF() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(UPCOMING_MAXIMUM_DATE_KEY, Common.getDateAfterWeekSF());
    }

    public String getTopRatedGenre() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(TOP_RATED_GENRE_KEY, spinners_default_word);
    }

    public void saveTopRatedGenre(String i) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOP_RATED_GENRE_KEY, i);
        editor.apply();
    }

    public String getTopRatedYear() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(TOP_RATED_YEAR_KEY, spinners_default_word);
    }

    public void saveTopRatedYear(String s) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOP_RATED_YEAR_KEY, s);
        editor.apply();
    }

    public void saveUpComingGnre(int genre) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UPCOMING_GENRE_KEY, genre);
        editor.apply();
    }

    public int getUpComingGnre() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getInt(UPCOMING_GENRE_KEY, 0);
    }

    public void savePopularGnre(int genre) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(POPULAR_GENRE_KEY, genre);
        editor.apply();
    }

    public int getPopularGnre() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getInt(POPULAR_GENRE_KEY, 0);
    }

    public void saveNowPlayingGenre(int genre) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NOWPLAYING_GENRE_KEY, genre);
        editor.apply();
    }

    public int getNowPlayingGenre() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_DATA_FILE, MODE_PRIVATE);
        return sharedPreferences.getInt(NOWPLAYING_GENRE_KEY, 0);
    }


    public void saveDailyNotificationState(boolean b) {
        Log.d(TAG, "saveDailyNotificationState: ");
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE).edit();
        editor.putBoolean(DAILY_NOTIFICATION, b);
        editor.apply();
    }

    public boolean getDailyNotificationState() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean(DAILY_NOTIFICATION, false);
        Log.d(TAG, "getDailyNotificationState: b = "+b);
        return b;
    }

    public void saveNotificationGenres(ArrayList<Integer> list) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE).edit();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            str.append(list.get(i)).append(",");
        }
        editor.putString("daily_notification_selected_genres", str.toString());
        Log.d(TAG, "saveNotificationGenres: list = " + list + "  string list = " + str.toString());
        editor.apply();
    }

    public int[] getNotificationSavedGenres() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE);
        String savedString = sharedPreferences.getString("daily_notification_selected_genres", null);

        if (savedString != null && !TextUtils.isEmpty(savedString)) {
            Log.d(TAG, "getNotificationSavedGenres: savedString " + savedString);
            String[] tokens = savedString.split(",");
            int[] numbers = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                numbers[i] = Integer.parseInt(tokens[i]);
            }

            return numbers;
        }
        return null;
    }

    public void addNotifiedMovieId(ArrayList<Integer> list) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE).edit();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            str.append(list.get(i)).append(",");
        }
        editor.putString("daily_notification_notified_movies", str.toString());
//        editor.putString("daily_notification_notified_movies", "");
        Log.d(TAG, "saveNotifiedMovieId: list = " + list + "  string list = " + str.toString());
        editor.apply();
    }

    public List<Integer> getNotifiedMoviesIds() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_NOTIFOCATIONS_DATA_FILE, MODE_PRIVATE);
        String savedString = sharedPreferences.getString("daily_notification_notified_movies", null);

        if (savedString != null && !TextUtils.isEmpty(savedString)) {
            Log.d(TAG, "getNotifiedMoviesIds: savedString " + savedString);
            String[] tokens = savedString.split(",");
            ArrayList<Integer> arr = new ArrayList<>();
            for (int i = 0; i < tokens.length; i++) {
                arr.add(Integer.parseInt(tokens[i]));
            }
            return arr;
        }
        return new ArrayList<>();
    }

    public static String getSessionId(Context context) {
        Log.d(TAG, "getSessionId: 1");
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info_file_key), MODE_PRIVATE);
        String guest_session_id = sharedPreferences.getString("guest_session_id", null);
//        String guest_session_id = "a4de63ed455573b6bb2fb79925e17229";
        String expires_at = sharedPreferences.getString("expires_at", null);
//        String expires_at = "2018-04-13 12:56:55 UTC";
        Date current = null;
        Date d = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss z");
        if(guest_session_id!=null&&expires_at!=null){
            try {
                Log.d(TAG, "getSessionId: 2");
                d = simpleDateFormat.parse(expires_at);
                Calendar cal = Calendar.getInstance();
                current = cal.getTime();
                Log.d(TAG, "getSessionId: saved time = "+d);
                Log.d(TAG, "getSessionId: current time = "+current);
            } catch (ParseException e) {
                Log.d(TAG, "getSessionId: 3");
                e.printStackTrace();
            }
        }

        if (d != null && current != null) {
            Log.d(TAG, "getSessionId: 4");
            if (current.before(d)) {
                Log.d(TAG, "getSessionId: 5");
                return guest_session_id;
            } else return null;
        } else return null;
    }

    public static void saveGSID(GuestSession body, Context context) {
        Log.d(TAG, "saveGSID: 1");
        if (body.getSuccess()) {
            Log.d(TAG, "saveGSID: 2");
            String id = body.getGuestSessionId();
            String expire_date = body.getExpiresAt();
            Common.GSID = id;
            SharedPreferences.Editor editor = context.getSharedPreferences(context.getResources().getString(R.string.user_info_file_key), MODE_PRIVATE).edit();
            editor.putString("guest_session_id", id);
            editor.putString("expires_at", expire_date);
            editor.apply();
        }
    }
}
