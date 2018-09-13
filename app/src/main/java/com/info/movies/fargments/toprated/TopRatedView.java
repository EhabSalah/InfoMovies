package com.info.movies.fargments.toprated;

import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface TopRatedView {
        void showProgress();
        void hideProgress();
        void showRecyclerViewLayout();
        void hideRecyclerViewLayout();

        void showRecyclerView();

        void hideRecyclerView();

        void hideSpinners();
        void showSpinners();
        void receiveTopRatedMovies(ArrayList<TopRatedMovie> topRatedMovies, int counter, int total_pages, String year, String genre);


        void showErrorMessage(String message);

        //void loadSavedMovies();
       // boolean stateOfSavedState();
        void showNoInternetLayout();
        void hideNoInternetLayout();
       // void showNotice(boolean isConnected);
       void showNoInternetSnak();

    void showSortProgress();

    void onLoadMoreFail();

    void resetFlags();

    void showRefreshProgress();
    void hideRefreshProgress();

    void hideNoResultsLayout();

    void showNoResultsLayout();

    void moveToMovieFragment(String movie_id, String title);
}
