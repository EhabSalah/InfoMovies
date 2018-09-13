package com.info.movies.fargments.watchlist;

import com.info.movies.models.WatchlistItem;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 1/18/2018.
 */

interface WatchlistView {

    void showRecyclerview();
    void hideRecyclerview();
    void showEmptyLayout();
    void hideEmptyLayout();


    void moveToMoviePage(String id, String title);

    void receiveWatchlist(ArrayList<WatchlistItem> w);

    void setToolbarTitle(String size);
}
