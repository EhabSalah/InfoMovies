package com.info.movies.fargments.watchlist;

import com.info.movies.db.DBHelper;
import com.info.movies.models.WatchlistItem;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 1/18/2018.
 */

interface WatchlistInteractor {
    void checkWatchList(OnResponseListener watchlistPresenterIMP, DBHelper mDb);

    public interface OnResponseListener {
        void onEmpty();
        void onNotEmpty(ArrayList<WatchlistItem> w);
    }
}
