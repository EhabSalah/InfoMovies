package com.info.movies.fargments.toprated;

import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface TopRatedInteractor {
    void newCall();

    void refreshInteractor();

    interface OnMoviesResponseListener {
        void onFailed(String message);
        void onSuccess(ArrayList<TopRatedMovie> topRatedMovies, int totalPages, String year, String genre);
    }
  /*  interface OnCheckInternetExistance {
        void onExistInternet (boolean isConnected);
    }*/

    void fetchTopRatedMovies(OnMoviesResponseListener ResponseListener, int required_page_no, String year, int genre);
    //void checkForInternet(TopRatedInteractor.OnCheckInternetExistance onCheckInternetExistance);

}
