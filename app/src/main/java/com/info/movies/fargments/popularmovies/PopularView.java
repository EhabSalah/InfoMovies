package com.info.movies.fargments.popularmovies;

import com.info.movies.models.posters.PopularMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface PopularView {
    void showProgress();
    void hideProgress();
    void showRecyclerViewLayout();
    void hideRecyclerViewLayout();

    void showRecyclerView();

    void hideRecyclerView();




    void receivePopularMovies(ArrayList<PopularMovie> popularMovies, int page_counter, int total_pages);

    void showErrorMessage(String message);

    void showNoInternetLayout();
    void hideNoInternetLayout();
    void hideRefreshProgressAnimation();
    void showRefreshProgressAnimation();
   // void showNotice(boolean isConnected);

    void showNoInternetSnak();

    void onLoadMoreFail();

    void resetFlags();

    void hideNoResultsLayout();

    void showNoResultsLayout();

    void showSortProgress();

    void moveToMovieFragment(String movie_id, String title);
}
