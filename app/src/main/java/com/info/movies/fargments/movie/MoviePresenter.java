package com.info.movies.fargments.movie;

import android.app.Activity;

import com.info.movies.models.movie_page.ListItemCastCrew;
import com.info.movies.models.movie_page.MoviePage;

import java.util.List;

/**
 * Created by EhabSalah on 1/12/2018.
 */

public interface MoviePresenter {

    void onCreateView(String id);

    void onDestroyView();

    void onSaveInstanceState();

    void onRecreateView(MoviePage moviePage);

    void onPlayTrailerClick( Activity activity, String key);

    void sendRate(String ID, float rateValue);

    void onRateLayoutClicked();

    void onRecommendedMovieClicked(String movie_id, String title);

    void onImageSelected(int position, List<String> images);

    void onCastClick(List<ListItemCastCrew> cast, int position);
}
