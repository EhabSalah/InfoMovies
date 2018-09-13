package com.info.movies.fargments.watchlist;

import android.util.Log;

import com.info.movies.db.DBHelper;
import com.info.movies.models.WatchlistItem;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 1/18/2018.
 */

class WatchlistPresenterIMP implements WatchlistPresenter ,WatchlistInteractor.OnResponseListener {
    WatchlistView mWatchlistView;
    private WatchlistInteractor mWatchlistInteractor;

    public WatchlistPresenterIMP(WatchlistView watchlistView) {
        this.mWatchlistView=watchlistView;
        mWatchlistInteractor = new WatchlistInteractorIMP();
    }

    @Override
    public void onCreate(DBHelper mDb) {
        if (mWatchlistView!=null) {
            Log.d("TAG_W", "onCreate: ");
            mWatchlistInteractor.checkWatchList(this,mDb);
        }
    }

    @Override
    public void onItemCLicked(String id, String title) {
        if (mWatchlistView!=null) {
            Log.d("TAG_W", "onItemCLicked: ");
            mWatchlistView.moveToMoviePage(id,title);
        }
    }

    /**************************************************************************************************/
    @Override
    public void onEmpty() {
        if (mWatchlistView!=null) {
            Log.d("TAG_W", "onEmpty: ");
            mWatchlistView.hideRecyclerview();
            mWatchlistView.showEmptyLayout();
            mWatchlistView.setToolbarTitle("");
        }
    }

    @Override
    public void onNotEmpty(ArrayList<WatchlistItem> w) {
        if (mWatchlistView!=null) {
            Log.d("TAG_W", "1");

            mWatchlistView.hideEmptyLayout();
            mWatchlistView.showRecyclerview();
            mWatchlistView.receiveWatchlist(w);
            if(w.size()>0){
                Log.d("TAG_W", "onNotEmpty: 2");
                mWatchlistView.setToolbarTitle(String.valueOf(w.size()));
            }
        }
    }
}
