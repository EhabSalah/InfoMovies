package com.info.movies;

import android.content.Context;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface MainPresenter {

    void nowPlayingSelected();
    void popularSelected();
    void topRatedSelected();
    void upcomingSelected();
    void favouriteSelected();
    void settingSelected();
    void aboutSelected();


    void onRestoreInstanceState();

    void onDestroy();
    void onResume();

    void checkSessionId(Context context);
}
