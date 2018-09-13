package com.info.movies.fargments.popularmovies;

import com.info.movies.models.posters.PopularMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface PopularInteractor {
    void newCall();

    void refreshInteractor();

    interface OnMoviesResponseListener {
        void onFailed(String message);
        void onSuccess(ArrayList<PopularMovie> popularMovies, int total_pages);
    }
//    interface OnCheckInternetExistance {
//        void onExistInternet (boolean isConnected);
//    }
    //void checkForInternet(PopularInteractor.OnCheckInternetExistance onCheckInternetExistance);

    void fetchPopularMovies(OnMoviesResponseListener ResponseListener, int required_page_no, int popular_genre);

}
