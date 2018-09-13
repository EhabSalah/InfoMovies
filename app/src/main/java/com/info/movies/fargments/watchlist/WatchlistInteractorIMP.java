package com.info.movies.fargments.watchlist;

import android.util.Log;

import com.info.movies.db.DBHelper;
import com.info.movies.models.WatchlistItem;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 1/18/2018.
 */

class WatchlistInteractorIMP implements WatchlistInteractor {

    public WatchlistInteractorIMP() {

    }

    @Override
    public void checkWatchList(OnResponseListener responseListener, DBHelper mDb) {
      ArrayList<WatchlistItem> w = mDb.getWatchlist();
        Log.d("TAG_W", "checkWatchList: = "+ new Gson().toJson(w));
        if (w!=null&&!w.isEmpty()) {
            Log.d("TAG_W", "checkWatchList: 1");
            responseListener.onNotEmpty(w);
        }else
        {
            Log.d("TAG_W", "checkWatchList: 2");
            responseListener.onEmpty();
        }
        mDb.close();
    }
}
