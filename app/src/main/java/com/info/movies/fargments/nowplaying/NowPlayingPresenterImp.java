package com.info.movies.fargments.nowplaying;

import android.os.Bundle;
import android.util.Log;

import com.info.movies.MainActivity;
import com.info.movies.models.posters.MoviePosterAR;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public class NowPlayingPresenterImp implements NowPlayingPresenter,
        NowPlayingInteractor.OnMoviesResponseListener/*,
                                                NowPlayingInteractor.OnCheckInternetExistance*/ {
    private static final String TAG = NowPlayingPresenterImp.class.getSimpleName();
    //    private final Context context;
    NowPlayingView mNowPlayingView;
    NowPlayingInteractor mNowPlayingInteractor;

    public NowPlayingPresenterImp(NowPlayingView nowPlayingView/*,Context context*/) {
        this.mNowPlayingView = nowPlayingView;
        this.mNowPlayingInteractor = new NowPlayingInteractorImp();
//        this.context=context;
    }


    @Override
    public void onDestroyView() {
        if (mNowPlayingView != null) {
            mNowPlayingInteractor.refreshInteractor();
        }
    }

    @Override
    public void onCreateView(int required_page_no, String month_before, String current_date, int genre_code) {
        Log.d(TAG, "onCreateView:  1 ");
        if (mNowPlayingView != null) {
            Log.d(TAG, "onCreateView:  2");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onCreateView:  3");
                mNowPlayingView.hideNowPlayingRecyclerViewLayout();
                mNowPlayingView.hideRecyclerView();
                mNowPlayingView.hideNoInternetLayout();
                mNowPlayingView.showProgress();
                mNowPlayingInteractor.newCall();
                mNowPlayingInteractor.fetchNowPlayingMovies(this, required_page_no, month_before, current_date, genre_code);

            }
        }
    }

    @Override
    public void onRefresh(int required_page_no, String month_before, String current_date, int genre_code) {
        Log.d(TAG, "onRefresh: 1");
        if (mNowPlayingView != null) {
            Log.d(TAG, "onRefresh: 2");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onRefresh: 3");
                mNowPlayingInteractor.refreshInteractor();
                mNowPlayingInteractor.fetchNowPlayingMovies(this, required_page_no, month_before, current_date, genre_code);
            }
        }
    }

    @Override
    public void onLoadMore(int required_page_no, String month_before, String current_date, int genre_code) {
        Log.d(TAG, "onLoadMore: presenter 1");
        if (mNowPlayingView != null) {
            Log.d(TAG, "onLoadMore:presenter 2");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onLoadMore: presenter 3");
                mNowPlayingInteractor.newCall();
                mNowPlayingInteractor.fetchNowPlayingMovies(this, required_page_no, month_before, current_date, genre_code);
            }
        }
    }

    @Override
    public void onDateSet(int page_counter, String current_date, String before_month_date, int genre_code) {
        Log.d(TAG, "onDateSet: 1 ");

        if (mNowPlayingView != null) {
            Log.d(TAG, "onDateSet: 2 ");

            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onDateSet: 3 ");
                mNowPlayingView.hideNoResultsLayout();
                mNowPlayingView.showSortProgress();
                mNowPlayingInteractor.refreshInteractor();
                mNowPlayingInteractor.fetchNowPlayingMovies(this, page_counter, before_month_date, current_date, genre_code);
            }
        }
    }

    @Override
    public void onPause() {
        if (mNowPlayingView != null) {
            Log.d(TAG, "onDestroyView: 1");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onDestroyView: 2");

                mNowPlayingInteractor.newCall();
                mNowPlayingView.hideProgress();
                mNowPlayingView.hideRefreshProgressAnimation();
            }
        }
    }

    @Override
    public void onRecreateView(Bundle savedState, int page_counter, String before_month_date, String current_date, int genre_code) {
        if (mNowPlayingView != null) {
            Log.d(TAG, "onReCreateView: 1");

            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onReCreateView: 2");
                int layout = 0;
                if (savedState != null)   // data saved
                {
                    Log.d(TAG, "onReCreateView: 3");

                    Bundle b = savedState;
                    ArrayList<MoviePosterAR> saved_list = b.getParcelableArrayList(NowPlayingFragment.SAVED_MOVIES_ARRAY_NOWPLAYING);
                    int saved_total_pages = b.getInt(NowPlayingFragment.SAVED_TOTAL_PAGES_NOWPLAYING);
                    int saved_page_counter = b.getInt(NowPlayingFragment.SAVED_MOVIES_PAGE_NO_NOWPLAYING);
                    NowPlayingInteractorImp.movies_filter = b.getIntegerArrayList(NowPlayingInteractorImp.SAVED_MOVIES_FILTER);
                    layout = b.getInt(NowPlayingFragment.SAVED_NO_LAYOUT);

                    if (saved_list != null && saved_list.size() != 0) { // data exist
                        Log.d(TAG, "onReCreateView: 4");

                        Log.d(TAG, "onReCreateView: NowPlaying : list size = " + saved_list.size());
                        Log.d(TAG, "onReCreateView: NowPlaying : page no" + saved_page_counter);
                        Log.d(TAG, "onReCreateView: NowPlaying : total pages" + saved_total_pages);
                        Log.d(TAG, "onReCreateView: NowPlaying : NowPlayingInteractorImp.movies_filter size =" + NowPlayingInteractorImp.movies_filter.size());

                        mNowPlayingInteractor.newCall();
                        mNowPlayingView.recieveNowPlayingMovies(saved_list, saved_page_counter, saved_total_pages);
                        //mUpComingView.hideNoInternetLayout();
                        mNowPlayingView.hideProgress();
                        mNowPlayingView.showNowPlayinLayout();
                        mNowPlayingView.showRecyclerView();
                        mNowPlayingView.hideRefreshProgressAnimation();
                    } else // no data exist
                    {
                        Log.d(TAG, "onReCreateView: 5");
                        if (layout != 0 && layout == 3) // no results
                        {
                            Log.d(TAG, "onReCreateView: 9");
                            mNowPlayingView.hideProgress();
                            mNowPlayingView.showNoResultsLayout();
                            NowPlayingFragment.no_visible_layout = 3;
                        } else {
                            // go make a new request. using parameters
                            Log.d(TAG, "onReCreateView: NowPlaying : saved list is empty");
                            mNowPlayingView.hideRecyclerView();
                            mNowPlayingView.hideNowPlayingRecyclerViewLayout();
                            mNowPlayingView.showProgress();

                            mNowPlayingInteractor.newCall();
                            mNowPlayingInteractor.fetchNowPlayingMovies(this, page_counter, before_month_date, current_date, genre_code);
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onItemClicked(String movie_id, String title) {
        if (mNowPlayingView != null) {
            Log.d(TAG, "onRecommendedMovieClicked: 1");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onRecommendedMovieClicked: 1");
                mNowPlayingView.moveToMovieFragment(movie_id, title);
            }
        }
    }

    @Override
    public void onFailed(String message) {
        if (mNowPlayingView != null) {
            Log.d(TAG, "onFailed: 1");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {

                Log.d(TAG, "onFailed: : 2");
                if (((NowPlayingFragment) mNowPlayingView).isThereDataAvailable()) // hna lw akiid gomovies tt invoked fl on load more failed, momkn . cheel l case gomovies w n7ot inOnloadmore bs.
                { // keda n show snackbar, el 7ala gomovies htkoon fl load more
                    Log.d(TAG, "onFailed: 3");
                    mNowPlayingView.hideProgress();
                    mNowPlayingView.hideRefreshProgressAnimation();
                    mNowPlayingView.onLoadMoreFail();
                    mNowPlayingView.showNoInternetSnak();
                } else  // keda n hide all w  n show el no internet layout.
                {// if it is not visible
                    Log.d(TAG, "onFailed: 4");
                    mNowPlayingView.hideProgress();
                    mNowPlayingView.hideNowPlayingRecyclerViewLayout();
                    mNowPlayingView.hideRefreshProgressAnimation();
                    mNowPlayingView.hideRecyclerView();
                    mNowPlayingView.showNoInternetLayout(); // show Connection error layout
                    if (message != null) {
                        Log.d(TAG, "onFailed: 5");
                        mNowPlayingView.showErrorMessage(message);
                    }
                }
                mNowPlayingView.resetFlags();
                NowPlayingFragment.no_visible_layout = 0;
            }
        }
    }

    @Override
    public void onSuccess(ArrayList<MoviePosterAR> moviePosterAR, int total_pages) {
        Log.d(TAG, "onSuccess 1");
        if (mNowPlayingView != null) {
            Log.d(TAG, "onSuccess 2");
            if (((NowPlayingFragment) mNowPlayingView).v != null) {
                Log.d(TAG, "onSuccess 3");
                if (!moviePosterAR.isEmpty() && moviePosterAR.size() != 0) {
                    Log.d(TAG, "onSuccess 4");
                    mNowPlayingView.recieveNowPlayingMovies(moviePosterAR, 0, total_pages);
                    mNowPlayingView.hideNoInternetLayout();
                    mNowPlayingView.hideProgress();
                    mNowPlayingView.showNowPlayinLayout();
                    mNowPlayingView.showRecyclerView();
                    mNowPlayingView.hideRefreshProgressAnimation();
                    NowPlayingFragment.no_visible_layout = 0;
                } else {
                    Log.d(TAG, "onSuccess 5");
                    if (NowPlayingFragment.page_counter < total_pages) {
                        Log.d(TAG, "onSuccess 6 ");
                        NowPlayingFragment.page_counter++;
                        mNowPlayingView.resetFlags();
                        NowPlayingFragment.isOnLoadMore = true;
                        onLoadMore(NowPlayingFragment.page_counter, NowPlayingFragment.before_month_date, NowPlayingFragment.current_date, NowPlayingFragment.nowplaying_genre);
                    } else {
                        if (((NowPlayingFragment) mNowPlayingView).newMovies.isEmpty()) { // no data in recyclerview
                            Log.d(TAG, "onSuccess:  7");
                            mNowPlayingView.hideNoInternetLayout();
                            mNowPlayingView.hideProgress();
                            mNowPlayingView.hideRefreshProgressAnimation();
                            mNowPlayingView.hideRecyclerView();
                            mNowPlayingView.hideNowPlayingRecyclerViewLayout();
                            mNowPlayingView.showNoResultsLayout();
                            ((NowPlayingFragment) mNowPlayingView).newMovies.clear();
                            ((NowPlayingFragment) mNowPlayingView).mAdapter.notifyDataSetChanged();
                            ((MainActivity) ((NowPlayingFragment) mNowPlayingView).activity).showAppBarIfCollapsed();
                            NowPlayingFragment.no_visible_layout = 3;
                        } else //  there is data in recyclerview
                        {
                            Log.d(TAG, "onSuccess: 9");
                            mNowPlayingView.hideNoInternetLayout();
                            mNowPlayingView.hideProgress();
                            mNowPlayingView.hideRefreshProgressAnimation();
                            mNowPlayingView.resetFlags();
                            ((NowPlayingFragment) mNowPlayingView).mAdapter.hideFooter();
                            ((NowPlayingFragment) mNowPlayingView).mAdapter.notifyDataSetChanged();
                            NowPlayingFragment.no_visible_layout = 0;
                        }
                    }
                }
            } else {
                Log.d(TAG, "onSuccess: 9");
            }
        }
    }


   /* @Override
    public void onExistInternet(boolean isConnected) {
        mNowPlayingView.showNotice(isConnected);
    }*/
}
