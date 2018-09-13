package com.info.movies.fargments.movie;

import android.app.Activity;
import android.content.Context;

import com.info.movies.models.movie_page.MoviePage;

/**
 * Created by EhabSalah on 1/12/2018.
 */

public interface MoviesInteractor {

    void fetchDetails(OnDetailsResponseListener onDetailsResponseListener, String id);
    void refreshInteractor();

    void checkToPlayVideo(OnPlayTrailerListener onPlayTrailerListener, Activity activity, String key);

    void rateMovie(OnMovieRateListener listener, Context context, String id, float rateValue);

    interface OnMovieRateListener {
        void onRateFailed(String message);
        void onRateSuccess();
    }

    interface OnDetailsResponseListener {
        void onFailed(String message);
        void onSuccess(MoviePage moviePage);
    }
    interface OnPlayTrailerListener{
        void onDisabled();

        void onNoInternet();
    }
}
