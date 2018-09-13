package com.info.movies;

import android.content.Context;
import android.util.Log;

import com.info.movies.constants.Common;
import com.info.movies.constants.Setting;
import com.info.movies.models.sign_up.GuestSession;


/**
 * Created by EhabSalah on 10/31/2017.
 */

public class MainPresenterImp implements MainPresenter, MainInteractor.OnRequestSessionIdListener {
    private static final String TAG = MainPresenterImp.class.getSimpleName();
    private final MainActivity mMainActivity;
    private MainView mMainView;
    MainInteractor mInteractor;

    public MainPresenterImp(MainView mMainView) {
        this.mMainView = mMainView;
        this.mMainActivity = (MainActivity) mMainView;
        mInteractor = new MainInteractorImp();
    }

    //                                                                        MAIN ACTIVITY VIEW ***
    @Override
    public void nowPlayingSelected() {
        if (mMainView != null) {
            Log.d(TAG, "nowPlayingSelected: ");
            mMainView.showNowPlayingFragment();
        }
    }

    @Override
    public void popularSelected() {
        if (mMainView != null) {
            mMainView.showPopularFragment();
        }
    }

    @Override
    public void topRatedSelected() {
        if (mMainView != null) {
            mMainView.showTopRatedFragment();
        }
    }

    @Override
    public void upcomingSelected() {
        if (mMainView != null) {
            mMainView.showUpcomingFragment();
        }
    }

    @Override
    public void favouriteSelected() {
        if (mMainView != null) {
            mMainView.showFavouriteFragment();
        }
    }

    @Override
    public void settingSelected() {
        if (mMainView != null) {
            mMainView.showSettingFragment();
        }
    }

    @Override
    public void aboutSelected() {
        if (mMainView != null) {
            mMainView.showAboutFragment();
        }
    }

    @Override
    public void onRestoreInstanceState() {
        mMainView.closeDrawer();
        //mMainView.resetToolbar();
    }

    @Override
    public void onDestroy() {
        if (mMainView != null) {
            mInteractor.refreshInteractor();
            mMainView = null;
        }
    }

    @Override
    public void onResume() {
        if (mMainView != null) {
        }
    }

    @Override
    public void checkSessionId(Context context) {
        Log.d(TAG, "checkSessionId: 1");
        String guest_session_id = Setting.getSessionId(context);
        if (guest_session_id != null) {
            Log.d(TAG, "checkSessionId: 2");
            Common.GSID = guest_session_id;
        } else {
            Log.d(TAG, "checkSessionId: 3");
            mInteractor.refreshInteractor();
            mInteractor.getSessionId(this,context);
        }
    }

    @Override
    public void onSuccess(GuestSession body, Context context) {
        Log.d(TAG, "onSuccess: ");
            Setting.saveGSID(body, context);
    }
}
