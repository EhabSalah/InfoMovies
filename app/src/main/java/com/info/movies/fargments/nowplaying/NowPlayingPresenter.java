package com.info.movies.fargments.nowplaying;

import android.os.Bundle;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface NowPlayingPresenter {

    void onDestroyView();

    void onCreateView(int required_page_no, String month_before, String current_date, int genre_code);

    void onRefresh(int required_page_no, String month_before, String current_date, int genre_code);

    void onLoadMore(int required_page_no, String month_before, String current_date, int genre_code);

    void onDateSet(int page_counter, String current_date, String before_month_date, int genre_code);

    void onPause();

    void onRecreateView(Bundle savedState, int page_counter, String before_month_date, String current_date, int genre_code);

    void onItemClicked(String movie_id, String title);
}
