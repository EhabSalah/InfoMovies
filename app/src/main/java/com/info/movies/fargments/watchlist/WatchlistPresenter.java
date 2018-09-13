package com.info.movies.fargments.watchlist;

import com.info.movies.db.DBHelper;

/**
 * Created by EhabSalah on 1/18/2018.
 */

public interface WatchlistPresenter {
    void onCreate(DBHelper mDb);

    void onItemCLicked(String id, String title);
}
