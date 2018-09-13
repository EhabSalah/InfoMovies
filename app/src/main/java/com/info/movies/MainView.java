package com.info.movies;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface MainView {

    void showNowPlayingFragment();
    void showPopularFragment();
    void showFavouriteFragment();
    void showTopRatedFragment();
    void showUpcomingFragment();
    void showSettingFragment();
    void showAboutFragment();

    void showToolbar();
    void hideToolbar();
    void resetToolbar();

    void changeToolbarTitle(int string_title_id);
    void closeDrawer();

    void showAppBarIfCollapsed();
}
