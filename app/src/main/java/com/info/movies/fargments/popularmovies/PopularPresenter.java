package com.info.movies.fargments.popularmovies;

import android.os.Bundle;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface PopularPresenter {

    void onCreateView(int required_page_no, int popular_genre);

    void onRefresh(int required_page_no, int popular_genre);

    void onLoadMore(int required_page_no, int popular_genre);

    void onDataSet(int page_counter, int genre_code);

    void onReCreateView(Bundle savedState, int page_counter, int popular_genre);

    void onPause();

    void onItemClicked(String movie_id, String title);
}
