package com.info.movies.constants;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.info.movies.R;
import com.info.movies.db.DatabaseCntract;
import com.info.movies.db.DBHelper;
import com.info.movies.models.generslise_response.Genre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by EhabSalah on 10/11/2017.
 */

public class Common {
    private static final String APP_URL_ON_STORE = "https://play.google.com/store/apps/details?id=com.info.movies";
    private static final String APP_PACKAGE_NAME = "com.info.movies";
    public static String GSID;
    public static final String ADMOB_APP_ID = "ca-app-pub-8746198548920349~4104393637";
    /* private  Context context;
        private  Activity activity;*/
    public Common(/*Context context*/) {
        /*this.activity= (Activity) context;
        this.context= context;*/
    }

    public static void showSnackAlert(View view, String message) {

        final Snackbar snack = Snackbar.make(view, message, 4000);
        snack.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    public static ArrayList<Genre> basic_geners = new ArrayList<>(Arrays.asList(
            new Genre(28, "Action"),
            new Genre(12, "Adventure"),
            new Genre(16, "Animation"),
            new Genre(35, "Comedy"),
            new Genre(80, "Crime"),
            new Genre(99, "Documentary"),
            new Genre(18, "Drama"),
            new Genre(10751, "Family"),
            new Genre(14, "Fantasy"),
            new Genre(36, "History"),
            new Genre(27, "Horror"),
            new Genre(10402, "Music"),
            new Genre(9648, "Mystery"),
            new Genre(10749, "Romance"),
            new Genre(878, "Science Fiction"),
            new Genre(10770, "TV Movie"),
            new Genre(53, "Thriller"),
            new Genre(10752, "War"),
            new Genre(37, "Western")
    ));
    public static SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat mTopRatedDF = new SimpleDateFormat("MMM yyyy");
    public static SimpleDateFormat mUpcomingDF = new SimpleDateFormat("dd MMM yyyy");

    public static String getCurrentDateSF() {
        Date currentTime = Calendar.getInstance().getTime();
        return sDF.format(currentTime);
    }

    public static String getDateAfterWeekSF() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        Date d = c.getTime();
        return sDF.format(d);
    }

    public static String getTomorrowDateSF() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        Date d = c.getTime();
        return sDF.format(d);
    }

    public static String getDateBeforeMonthSF() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -50);
        Date d = c.getTime();
        return sDF.format(d);
    }

    public static void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d("TAG", " hideSoftKeyboard: ");
    }

    public static void showKeyboard(EditText e, Context context) {
        e.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String getGenreString(int genre_code) {
        for (int i = 0; i < basic_geners.size(); i++) {
            if (basic_geners.get(i).getId() == genre_code) {
                return basic_geners.get(i).getName();
            }
        }
        return null;
    }

    public static int getGenreCode(String selected_genre) {
        for (int i = 0; i < basic_geners.size(); i++) {
            if (basic_geners.get(i).getName().equals(selected_genre)) {
                return basic_geners.get(i).getId();
            }
        }
        return 0;
    }

    public static int getGenreCodeByNumber(int gener_no) {
        return basic_geners.get(gener_no).getId();
    }

    public static ArrayList<String> getGenresNamesList() {
        ArrayList<String> genre_names = new ArrayList<>();
        for (int i = 0; i < basic_geners.size(); i++) {
            genre_names.add(basic_geners.get(i).getName());
        }
        return genre_names;
    }

    private static char[] c = new char[]{'K', 'M', 'B', 'T'};

    public static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + ""
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));

    }

    public static boolean isMovieAddedToWatchlist(String id, Context context) throws SQLException {
        DBHelper watchlistDBHelper = new DBHelper(context); // db declaration
        SQLiteDatabase sqLiteDatabase = watchlistDBHelper.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + DatabaseCntract.WatchlistEntry.TABLE_NAME + " WHERE " + DatabaseCntract.WatchlistEntry.COL_ID + " = ?";
            c = sqLiteDatabase.rawQuery(query, new String[]{id});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
                watchlistDBHelper.close();
            }
        }
    }

    public static boolean isWatchlist(String id, Context context) throws SQLException {
        DBHelper DBHelper = new DBHelper(context); // db declaration
        SQLiteDatabase sqLiteDatabase = DBHelper.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + DatabaseCntract.WatchlistEntry.TABLE_NAME + " WHERE " + DatabaseCntract.WatchlistEntry.COL_ID + " = ?";
            c = sqLiteDatabase.rawQuery(query, new String[]{id});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
                DBHelper.close();
            }
        }
    }

    public static boolean isAppEnabled(String package_name, Activity activity) throws PackageManager.NameNotFoundException {
        ApplicationInfo ai =
                activity.getPackageManager().getApplicationInfo(package_name, 0);
        return ai.enabled;
    }

    public static ArrayList<Integer> getGenresCodesByNumbers(int[] favourite_genres_nos) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int no : favourite_genres_nos) {
            int code = Common.getGenreCodeByNumber(no);
            ids.add(code);
        }
        return ids;
    }


    public static String MovieRate(String id, Context context) {
        DBHelper mDBHelper = new DBHelper(context); // db declaration
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        String rate = mDBHelper.getRateForThisMovie(sqLiteDatabase,id);
        sqLiteDatabase.close();
        return rate;
    }

    public static void CopyText(TextView textView, Activity activity) {
        if (!TextUtils.isEmpty(textView.getText().toString())) {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(activity.getResources().getString(R.string.app_name), textView.getText().toString());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity, R.string.copy_sucess_message, Toast.LENGTH_SHORT).show();
        }
    }
    public static void setIconTextColorOfRecentAppState(Activity mActivity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_logo);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(mActivity.getResources().getString(R.string.app_name), bm, mActivity.getResources().getColor(R.color.colorAccent));
            mActivity.setTaskDescription(taskDesc);
        }
    }

    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, APP_URL_ON_STORE);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing "+context.getResources().getString(R.string.app_name)+ "App");
        context.startActivity(Intent.createChooser(shareIntent, "Share "+context.getResources().getString(R.string.app_name)+ "App"));
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE_NAME)));
        } catch (android.content.ActivityNotFoundException anfe) {
           context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PACKAGE_NAME)));
        }
    }
}