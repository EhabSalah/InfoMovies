package com.info.movies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.info.movies.models.WatchlistItem;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 1/17/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private static final String DB_NAME = "app_d";
    private static final int DB_VERSION = 4;

    private static final String CREATE_TABLE_WHATCHLIST_QUERY = "CREATE TABLE " + DatabaseCntract.WatchlistEntry.TABLE_NAME + "( " +
            DatabaseCntract.WatchlistEntry.COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseCntract.WatchlistEntry.COL_ID + " INTEGER UNIQUE NOT NULL," +
            DatabaseCntract.WatchlistEntry.COL_TITLE + " TEXT," +
            DatabaseCntract.WatchlistEntry.COL_POSTER_PATH + " TEXT," +
            DatabaseCntract.WatchlistEntry.COL_GENRES + " TEXT);";

    private static final String DROP_TABLE_WHATCHLIST_QUERY = "DROP TABLE IF EXISTS '" + DatabaseCntract.WatchlistEntry.TABLE_NAME + "';";

    private static final String DROP_TABLE_RATED_QUERY = "DROP TABLE IF EXISTS '" + DatabaseCntract.RatedEntry.TABLE_NAME + "';";
    private String CREATE_TABLE_RATED_QUERY = "CREATE TABLE " + DatabaseCntract.RatedEntry.TABLE_NAME + "( " +
            DatabaseCntract.RatedEntry.COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseCntract.RatedEntry.COL_ID + " INTEGER UNIQUE NOT NULL," +
            DatabaseCntract.RatedEntry.COL_RATE + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate DATABASE: 1");
        db.execSQL(CREATE_TABLE_WHATCHLIST_QUERY);
        Log.d(TAG, "onCreate: DATABASE: 2");
        db.execSQL(CREATE_TABLE_RATED_QUERY);
        Log.d(TAG, "onCreate: DATABASE: 3");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "onUpgrade: ");
        db.execSQL(DROP_TABLE_WHATCHLIST_QUERY);
        db.execSQL(DROP_TABLE_RATED_QUERY);
    }

    public boolean insertMovie(String id, String title, String poster_path, String genres, SQLiteDatabase db) // insert into the table
    {
        Log.d(TAG, "insertMovie: id = " + id);
        Log.d(TAG, "insertMovie: title = " + title);
        Log.d(TAG, "insertMovie: poster_path = " + poster_path);
        Log.d(TAG, "insertMovie: rateGenres = " + genres);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseCntract.WatchlistEntry.COL_ID, id);
        contentValues.put(DatabaseCntract.WatchlistEntry.COL_TITLE, title);
        contentValues.put(DatabaseCntract.WatchlistEntry.COL_POSTER_PATH, poster_path);
        contentValues.put(DatabaseCntract.WatchlistEntry.COL_GENRES, genres);

        long l = db.insert(DatabaseCntract.WatchlistEntry.TABLE_NAME, null, contentValues);
        if (l != -1) {
            Log.d(TAG, "One Row Inserted...");
            return true;
        } else {
            Log.d(TAG, "One Row Not Inserted...");
            return false;
        }
    }

    public boolean daleteFromWatchlist(SQLiteDatabase db, String id) {
        String whereClause = DatabaseCntract.WatchlistEntry.COL_ID + "=?";

        String[] whereArgs
                = new String[]{id};

        return db.delete(DatabaseCntract.WatchlistEntry.TABLE_NAME, whereClause, whereArgs) > 0;
    }

    public ArrayList<WatchlistItem> getWatchlist() {

        ArrayList<WatchlistItem> watchlistItems = new ArrayList<WatchlistItem>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseCntract.WatchlistEntry.TABLE_NAME + " ORDER BY " + DatabaseCntract.WatchlistEntry.COL_NO + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
//        db.delete(DatabaseCntract.WatchlistEntry.TABLE_NAME,null,null);
        try {
            Log.d(TAG, "getWatchlist: 1");
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                Log.d(TAG, "getWatchlist: 2");

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    Log.d(TAG, "getWatchlist: 3");

                    do {
                        Log.d(TAG, "getWatchlist: 4");

                        WatchlistItem item = new WatchlistItem();
                        //only one column
                        item.setId(cursor.getString(1));
                        item.setTitle(cursor.getString(2));
                        item.setPoster_path(cursor.getString(3));
                        item.setGenres(cursor.getString(4));

                        watchlistItems.add(item);
                    } while (cursor.moveToNext());
                }

            } finally {
                Log.d(TAG, "getWatchlist: 5");

                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }

        } finally {
            Log.d(TAG, "getWatchlist: 6");

            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return watchlistItems;
    }


    public long insertRatedMovie(SQLiteDatabase sqLiteDatabase, String id, float rateValue) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseCntract.RatedEntry.COL_ID, id);
        contentValues.put(DatabaseCntract.RatedEntry.COL_RATE, rateValue);
        return sqLiteDatabase.insert(DatabaseCntract.RatedEntry.TABLE_NAME, null, contentValues);
    }

    public String getRateForThisMovie(SQLiteDatabase sqLiteDatabase, String id) {
        String[] projections = {DatabaseCntract.RatedEntry.COL_RATE};


        Cursor c = sqLiteDatabase.query(DatabaseCntract.RatedEntry.TABLE_NAME, projections, DatabaseCntract.RatedEntry.COL_ID + "=?", new String[]{id}, null, null, null);
        if (c.moveToFirst()) {
            Log.d(TAG, "getRateForThisMovie: 1");
            String rate = c.getString(0);
            Log.d(TAG, "getRateForThisMovie: 2 rate = " + rate);
            c.close();
            return rate;
        } else {
            Log.d(TAG, "getRateForThisMovie: 3");
            c.close();
            return null;
        }
    }


    //Cursor findEntry = db.query("sku_table", columns, "owner=? and price=?", new String[] { owner, price }, null, null, null);
}

