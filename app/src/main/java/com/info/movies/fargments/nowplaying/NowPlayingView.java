package com.info.movies.fargments.nowplaying;

import com.info.movies.models.posters.MoviePosterAR;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface NowPlayingView {
    void showProgress();
    void hideProgress();
    void showNowPlayinLayout();
    void hideNowPlayingRecyclerViewLayout();
    void showRecyclerView();
    void hideRecyclerView();

    void recieveNowPlayingMovies(ArrayList<MoviePosterAR> nowPlayingMovies, int counter, int total_pages);

    void showErrorMessage(String message);


    void showNoInternetLayout();
    void hideNoInternetLayout();
    void hideRefreshProgressAnimation();
   // void showNotice(boolean isConnected);

    void showRefreshProgressAnimation();

    void showNoInternetSnak();


    void onLoadMoreFail();

    void resetFlags();

    void hideNoResultsLayout();

    void showNoResultsLayout();

    void showSortProgress();

    void moveToMovieFragment(String movie_id, String title);
    // void scrollToTop();
    /// HERE I STOPPED  3ayz a3raf ezzai h5ali yscroll l fo2 mn l activity
    // kamam 3yz a3ml el PULL to REFRESH +
    // LOAD MORE +
    // SAVE FRAGMENT STATE +  ... DONE
    // ONCLICK +
    // anafz el loop bta3 el server result f thread monfasl + ... DONE
    // handling retrofit connection errors ... DONE

}
