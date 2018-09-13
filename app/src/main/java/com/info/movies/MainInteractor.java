package com.info.movies;

import android.content.Context;

import com.info.movies.models.sign_up.GuestSession;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public interface MainInteractor {

    void getSessionId(OnRequestSessionIdListener mainPresenterImp, Context context);

    void refreshInteractor();

    interface OnRequestSessionIdListener{
        void onSuccess(GuestSession body, Context context);
    }
}
