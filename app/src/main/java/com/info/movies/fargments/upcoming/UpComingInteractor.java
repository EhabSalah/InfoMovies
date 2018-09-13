package com.info.movies.fargments.upcoming;

import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/12/2017.
 */

interface   UpComingInteractor {

    void refreshInteractor();

    void newCall();

    interface OnMoviesResponseListener {
        void onFailed(String message);
        void onSuccess(ArrayList<TopRatedMovie> topRatedMovies, int total_pages);
    }
    interface OnCheckInternetExistance {
        void onExistInternet(boolean isConnected);
    }

    void fetchUpcomingMovies(OnMoviesResponseListener ResponseListener, String min_date, String max_date, int required_page_no, int genre_code);
   // void checkForInternet(OnCheckInternetExistance onCheckInternetExistance);

}
