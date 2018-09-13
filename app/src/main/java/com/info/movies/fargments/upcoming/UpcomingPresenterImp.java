package com.info.movies.fargments.upcoming;

import android.os.Bundle;
import android.util.Log;

import com.info.movies.MainActivity;
import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/12/2017.
 */

public class UpcomingPresenterImp implements UpcommingPresenter, UpComingInteractor.OnMoviesResponseListener/*, UpComingInteractor.OnCheckInternetExistance*/ {
    private static final String TAG = UpcomingPresenterImp.class.getSimpleName();
    UpComingView mUpComingView;
    UpComingInteractor mUpcomingInteractor;

    UpcomingPresenterImp(UpComingView mUpComingView) {
        this.mUpComingView = mUpComingView;
        this.mUpcomingInteractor = new UpComingInteractorImp();
    }


    @Override
    public void onCreateView(String min_date, String max_date, int required_page_no, int upcoming_genre) {
        Log.d(TAG, "onCreateView: 1 ");
        if (mUpComingView != null) {
            Log.d(TAG, "onCreateView: 2 ");
            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onCreateView: 3 ");
                mUpComingView.hideRecyclerViewLayout();
                mUpComingView.hideRecyclerView();
                mUpComingView.hideNoInternetLayout();
                mUpComingView.hideNoResultsLayout();
                mUpComingView.hideRefreshProgressAnimation();
                mUpComingView.showProgress();
                mUpcomingInteractor.refreshInteractor();
                mUpcomingInteractor.fetchUpcomingMovies(this, min_date, max_date, required_page_no, upcoming_genre);
            }
        }
    }

    @Override
    public void onRefresh(String min_date, String max_date, int required_page_no, int upcoming_genre) {
        Log.d(TAG, "onRefresh: 1 ");

        if (mUpComingView != null) {
            Log.d(TAG, "onRefresh: 2 ");

            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onRefresh: 3 ");
                mUpcomingInteractor.refreshInteractor();
                mUpcomingInteractor.fetchUpcomingMovies(this, min_date, max_date, required_page_no, upcoming_genre);
            }
        }
    }

    @Override
    public void onLoadMore(String min_date, String max_date, int required_page_no, int upcoming_genre) {
        Log.d(TAG, "onLoadMore: 1 ");

        if (mUpComingView != null) {
            Log.d(TAG, "onLoadMore: 2 ");

            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onLoadMore: 3 ");
                mUpcomingInteractor.newCall();
                mUpcomingInteractor.fetchUpcomingMovies(this, min_date, max_date, required_page_no, upcoming_genre);
            }
        }
    }


    @Override
    public void onDateSet(String min_date, String max_date, int required_page_no, int upcoming_genre) {
        if (mUpComingView != null) {
            Log.d(TAG, "onDateSet: 2 ");

            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onDateSet:3 ");

                mUpComingView.hideInValidDateSelectedLayout();
                mUpComingView.hideNoResultsLayout();
                mUpComingView.showSortProgress();
                mUpcomingInteractor.refreshInteractor();
                mUpcomingInteractor.fetchUpcomingMovies(this, min_date, max_date, required_page_no, upcoming_genre);
            }
        }
    }


    @Override
    public void onInValidDateSelected() {
        Log.d(TAG, "onInValidDateSelected: 1");
        if (mUpComingView != null) {
            Log.d(TAG, "onInValidDateSelected: 2 ");

            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onInValidDateSelected: 3");

                mUpComingView.hideProgress();
                mUpComingView.hideRefreshProgressAnimation();
                mUpComingView.hideRecyclerViewLayout();
                mUpComingView.hideRecyclerView();
                mUpComingView.hideNoResultsLayout();
                UpcomingFragment.newMovies.clear();
                // then show layout
                UpcomingFragment.no_visible_layout = 1;
                mUpComingView.showInValidDateSelectedLayout();
                mUpComingView.resetFlags();
            }
        }
    }

    @Override
    public boolean noInternetLayoutVisibility() {
        return mUpComingView.noInternetLayoutVisibile();
    }

    @Override
    public void onReCreateView(Bundle savedState, String currentDate, String maximumDate, int page_counter, int upcoming_genre) {
        Log.d(TAG, "onReCreateView: 1");

        if (mUpComingView != null) {
            Log.d(TAG, "onReCreateView: 2");

            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onReCreateView: 3");
                int layout = 0;
                if (savedState != null)   // data saved
                {
                    Log.d(TAG, "onReCreateView: 4");

                    Bundle b = savedState;
                    ArrayList<TopRatedMovie> saved_list = b.getParcelableArrayList(UpcomingFragment.SAVED_MOVIES_ARRAY_UPCOMING);
                    int saved_total_pages = b.getInt(UpcomingFragment.SAVED_TOTAL_PAGES_UPCOMING);
                    int saved_page_counter = b.getInt(UpcomingFragment.SAVED_MOVIES_PAGE_NO_UPCOMING);
                    UpComingInteractorImp.movies_filter = b.getIntegerArrayList(UpComingInteractorImp.SAVED_MOVIES_FILTER);
                    layout = b.getInt(UpcomingFragment.SAVED_NO_LAYOUT);
                    if (saved_list != null && saved_list.size() != 0) { // data exist
                        Log.d(TAG, "onReCreateView: 5");

                        Log.d(TAG, "onReCreateView: UPCOMING : list size = " + saved_list.size());
                        Log.d(TAG, "onReCreateView: UPCOMING : page no" + saved_page_counter);
                        Log.d(TAG, "onReCreateView: UPCOMING : total pages" + saved_total_pages);
                        Log.d(TAG, "onReCreateView: UPCOMING : UpComingInteractorImp.movies_filter" + UpComingInteractorImp.movies_filter);

                        mUpcomingInteractor.newCall();
                        mUpComingView.receiveUpcomingMovies(saved_list, saved_page_counter, saved_total_pages);
                        //mUpComingView.hideNoInternetLayout();
                        mUpComingView.hideProgress();
                        mUpComingView.hideNoResultsLayout();

                        mUpComingView.hideRefreshProgressAnimation();
                        mUpComingView.showRecyclerViewLayout();
                        mUpComingView.showRecyclerView();
                        UpcomingFragment.no_visible_layout = 0;
                    } else // no data exist
                    {
                        Log.d(TAG, "onReCreateView: 6");
                        if (layout != 0 && layout == 1) // invalid date
                        {
                            Log.d(TAG, "onReCreateView: 7");
                            mUpComingView.hideProgress();
                            mUpComingView.showInValidDateSelectedLayout();
                        } else if (layout != 0 && layout == 3) // no results
                        {
                            Log.d(TAG, "onReCreateView: 9");
                            mUpComingView.hideProgress();
                            mUpComingView.showNoResultsLayout();
                            UpcomingFragment.no_visible_layout = 3;

                        } else {
                            // go make a new request. using parameters
                            Log.d(TAG, "onReCreateView: 10");
                            mUpComingView.showProgress();
                            mUpComingView.hideNoResultsLayout();
                            mUpComingView.hideRecyclerView();
                            mUpComingView.hideRecyclerViewLayout();
                            mUpcomingInteractor.newCall();
                            mUpcomingInteractor.fetchUpcomingMovies(this, currentDate, maximumDate, page_counter, upcoming_genre);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onDestroyView: 1 ");
        if (mUpComingView != null) {
            Log.d(TAG, "onDestroyView: 2 ");
            mUpcomingInteractor.newCall();
            mUpComingView.hideRefreshProgressAnimation();
            mUpComingView.hideProgress();
        }
    }

    @Override
    public void onItemClicked(String movie_id, String title) {
        Log.d(TAG, "onRecommendedMovieClicked: 1");
        if (mUpComingView != null) {
            Log.d(TAG, "onRecommendedMovieClicked: 2");
            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onRecommendedMovieClicked: 3");
                mUpComingView.moveToMovieFragment(movie_id, title);
            }
        }
    }

    @Override
    public void onFailed(String message) { // handle cases of no internet connection
        Log.d(TAG, "onFailed: 1");
        if (mUpComingView != null) {
            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onFailed: 1.5");

                if (((UpcomingFragment) mUpComingView).isThereDataAvailable()) {
                    Log.d(TAG, "onFailed: 2");
                    mUpComingView.hideProgress();
                    mUpComingView.hideNoResultsLayout();
                    mUpComingView.hideRefreshProgressAnimation();
                    mUpComingView.onLoadMoreFail();
                    mUpComingView.showNoInternetSnak();
                    UpcomingFragment.no_visible_layout = 0;

                } else  // keda n hide all w  n show el no internet layout.
                {// if it is not visible
                    Log.d(TAG, "onFailed: 3");
                    mUpComingView.hideProgress();
                    mUpComingView.hideRecyclerViewLayout();
                    mUpComingView.hideRefreshProgressAnimation();
                    mUpComingView.hideRecyclerView();
                    mUpComingView.hideInValidDateSelectedLayout();
                    mUpComingView.hideNoResultsLayout();
                    UpcomingFragment.newMovies.clear();
                    UpcomingFragment.no_visible_layout = 2;

                    mUpComingView.showNoInternetLayout(); // show Connection error layout
                    if (message != null) {
                        Log.d(TAG, "onFailed: 4");
                        mUpComingView.showErrorMessage(message);
                    }
                }
                mUpComingView.resetFlags();
            } else {
                Log.d(TAG, "onFailed: 5");
                Log.d(TAG, "onFailed: view is not ready 5");
            }
        }
    }

    @Override
    public void onSuccess(ArrayList<TopRatedMovie> topRatedMovies, int total_pages) {

        if (mUpComingView != null) {
            Log.d(TAG, "onSuccess:  2");
            if (((UpcomingFragment) mUpComingView).v != null) {
                Log.d(TAG, "onSuccess:  3");
                if (!topRatedMovies.isEmpty() || topRatedMovies.size() != 0) {
                    Log.d(TAG, "onSuccess:  4");
                    mUpComingView.receiveUpcomingMovies(topRatedMovies, 0, total_pages);
                    mUpComingView.hideNoInternetLayout();
                    mUpComingView.hideProgress();
                    mUpComingView.hideRefreshProgressAnimation();
                    mUpComingView.hideNoResultsLayout();
                    mUpComingView.showRecyclerViewLayout();
                    mUpComingView.showRecyclerView();
                    UpcomingFragment.no_visible_layout = 0;
                } else {
                    Log.d(TAG, "onSuccess:  5");

                    if (UpcomingFragment.page_counter < total_pages) {
                        Log.d(TAG, "onSuccess:  6");
                        UpcomingFragment.total_pages = total_pages;
                        UpcomingFragment.page_counter++;
                        mUpComingView.resetFlags();
                        UpcomingFragment.isOnLoadMore = true;
                        onLoadMore(UpcomingFragment.tomorrowDate, UpcomingFragment.maximumDate, UpcomingFragment.page_counter, UpcomingFragment.upcoming_genre);
                    } else {
                        Log.d(TAG, "onSuccess:  7");
                        mUpComingView.hideNoInternetLayout();
                        mUpComingView.hideProgress();
                        mUpComingView.hideRefreshProgressAnimation();
                        mUpComingView.hideInValidDateSelectedLayout();

                        if (UpcomingFragment.isOnLoadMore) // no data on load more
                        {
                            Log.d(TAG, "onSuccess: 8");
                            mUpComingView.receiveUpcomingMovies(topRatedMovies, 0, total_pages);
                        }
                        else  // no data
                        {
                            Log.d(TAG, "onSuccess: 9");
                            ((MainActivity) ((UpcomingFragment) mUpComingView).getActivity()).showAppBarIfCollapsed();
                            mUpComingView.hideRecyclerView();
                            mUpComingView.hideRecyclerViewLayout();
                            mUpComingView.showNoResultsLayout();
                            UpcomingFragment.no_visible_layout = 3;
                            mUpComingView.receiveUpcomingMovies(topRatedMovies, 0, total_pages);

                            mUpComingView.resetFlags();
                        }

                    }
                }
            } else {
                Log.d(TAG, "onSuccess:  10");
            }

        }
    }



      /*  @Override
        public void onExistInternet(boolean isConnected) {
            mUpComingView.showNotice(isConnected);
        }*/
}