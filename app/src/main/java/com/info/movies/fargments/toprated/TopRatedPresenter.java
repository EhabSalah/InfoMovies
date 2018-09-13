package com.info.movies.fargments.toprated;

import android.os.Bundle;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public interface TopRatedPresenter {


    void onPause();

    void onCreateView(int page_counter, String year, int genre);

    void onRecreateView(Bundle savedState, int page_counter, String year, int genre);

    void onRefresh(int page, String year, int genre);

    void onLoadMore(int page_counter, String year, int genre);

    void onSort(int page, String year, int genre);

    void onItemClicked(String movie_id, String title);
}
