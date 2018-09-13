package com.info.movies.fargments.upcoming;

import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/12/2017.
 */

public interface UpComingView {
    void showProgress();
    void hideProgress();
    void showRecyclerViewLayout();
    void hideRecyclerViewLayout();

    void showRecyclerView();

    void hideRecyclerView();
    void showNoResultsLayout();
    void hideNoResultsLayout();

    void receiveUpcomingMovies(ArrayList<TopRatedMovie> upcomingMovies, int page_counter, int total_pages);

    void showErrorMessage(String message);

    void showNoInternetLayout();
    void hideNoInternetLayout();
    void hideRefreshProgressAnimation();
    void showRefreshProgressAnimation();
   // void showNotice(boolean isConnected);

    void showInValidDateSelectedLayout();

    void hideInValidDateSelectedLayout();

    boolean noInternetLayoutVisibile();

    void showNoInternetSnak();

    void showSortProgress();

    void onLoadMoreFail();

    void resetFlags();

    void moveToMovieFragment(String movie_id, String title);
}
