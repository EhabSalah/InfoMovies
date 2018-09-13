package com.info.movies.fargments.nowplaying;

import com.info.movies.models.posters.MoviePosterAR;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface NowPlayingInteractor {

    void newCall();

    void refreshInteractor();

    interface OnMoviesResponseListener {
        void onFailed(String message);
        void onSuccess(ArrayList<MoviePosterAR> moviePosterAR, int total_pages);
    }
  /*  interface OnCheckInternetExistance {
        void onExistInternet (boolean isConnected);
    }*/

    void fetchNowPlayingMovies(OnMoviesResponseListener ResponseListener, int required_page_no, String month_before, String current_date, int genre_code);

   // void checkForInternet(OnCheckInternetExistance onCheckInternetExistance);
}
