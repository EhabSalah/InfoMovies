package com.info.movies.fargments.upcoming;

import android.os.Bundle;

/**
 * Created by EhabSalah on 11/12/2017.
 */

public interface UpcommingPresenter {
    void onCreateView(String min_date, String max_date, int required_page_no, int upcoming_genre);
    void onRefresh(String min_date, String max_date, int required_page_no, int upcoming_genre);
    void onLoadMore(String min_date, String max_date, int required_page_no, int upcoming_genre);

    void onDateSet(String min_date, String max_date, int required_page_no, int genre_code);

    void onInValidDateSelected();

    boolean noInternetLayoutVisibility();

    void onReCreateView(Bundle savedState, String currentDate, String maximumDate, int page_counter, int upcoming_genre);

    void onPause();

    void onItemClicked(String movie_id, String title);
}
