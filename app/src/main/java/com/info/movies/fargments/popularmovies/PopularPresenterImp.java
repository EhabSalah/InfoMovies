package com.info.movies.fargments.popularmovies;

import android.os.Bundle;
import android.util.Log;

import com.info.movies.MainActivity;
import com.info.movies.models.posters.PopularMovie;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class PopularPresenterImp implements PopularPresenter, PopularInteractor.OnMoviesResponseListener/*, PopularInteractor.OnCheckInternetExistance*/ {
    PopularView mPopularView;
    PopularInteractor mPopularInteractor;

    public PopularPresenterImp(PopularView popularView) {
        this.mPopularView = popularView;
        this.mPopularInteractor = new PopularInteractorImp();
    }


    @Override
    public void onCreateView(int required_page_no, int genre_code) {
        Log.d("TAG", "onCreateView: 1 ");

        if (mPopularView != null) {
            Log.d("TAG", "onCreateView: 2");
            if (((PopularFragment) mPopularView).v != null) {
                Log.d("TAG", "onCreateView: 3 ");
                mPopularView.hideRecyclerViewLayout();
                mPopularView.hideRecyclerView();
                mPopularView.hideNoInternetLayout();
                mPopularView.showProgress();
                mPopularInteractor.newCall();
                mPopularInteractor.fetchPopularMovies(this, required_page_no, genre_code);
            }
        }
    }

    @Override
    public void onRefresh(int required_page_no, int genre_code) {
        Log.d("TAG", "onRefresh: 1 ");

        if (mPopularView != null) {
            Log.d("TAG", "onRefresh: 2");

            if (((PopularFragment) mPopularView).v != null) {

                Log.d("TAG", "onRefresh: 3 ");
                mPopularInteractor.refreshInteractor();
                mPopularInteractor.fetchPopularMovies(this, required_page_no, genre_code);
            }
        }
    }

    @Override
    public void onLoadMore(int required_page_no, int genre_code) {
        Log.d("TAG", "onLoadMore: 1 ");

        if (mPopularView != null) {
            Log.d("TAG", "onLoadMore: 2");

            if (((PopularFragment) mPopularView).v != null) {

                Log.d("TAG", "onLoadMore: 3 ");
                mPopularInteractor.newCall();
                mPopularInteractor.fetchPopularMovies(this, required_page_no, genre_code);
            }
        }
    }


    @Override
    public void onDataSet(int page_counter, int genre_code) {
        Log.d("TAG", "onDataSet: 1");

        if (mPopularView != null) {
            Log.d("TAG", "onDataSet: 2");

            if (((PopularFragment) mPopularView).v != null) {

                Log.d("TAG", "onDataSet: 3 ");
                mPopularView.hideNoResultsLayout();
                mPopularView.showSortProgress();
                mPopularInteractor.refreshInteractor();
                mPopularInteractor.fetchPopularMovies(this, page_counter, genre_code);
            }
        }
    }

    @Override
    public void onReCreateView(Bundle savedState, int page_counter, int genre_code) {
        Log.d("TAG", "onReCreateView: 1 ");

        if (mPopularView!=null) {
            Log.d("TAG", "onReCreateView: 2 ");

            if (((PopularFragment) mPopularView).v != null) {
                int layout = 0;
                Log.d("TAG", "onReCreateView: 3 ");
                if (savedState != null)   // data saved
                {
                    Log.d("TAG", "onReCreateView: 4 ");

                    Bundle b = savedState;
                    ArrayList<PopularMovie> saved_list = b.getParcelableArrayList(PopularFragment.SAVED_MOVIES_ARRAY_POPULAR);
                    int saved_total_pages = b.getInt(PopularFragment.SAVED_TOTAL_PAGES_POPULAR);
                    int saved_page_counter = b.getInt(PopularFragment.SAVED_MOVIES_PAGE_NO_POPULAR);
                    PopularFragment.movie_rank = b.getInt(PopularFragment.SAVED_RANK_NO_POPULAR);
                    PopularInteractorImp.movies_filter = b.getIntegerArrayList(PopularInteractorImp.SAVED_MOVIES_FILTER);
                    layout = b.getInt(PopularFragment.SAVED_NO_LAYOUT);

                    if (saved_list != null && saved_list.size() != 0) { // data exist
                        Log.d("TAG", "onReCreateView: 5 ");

                        Log.d("TAG", "onReCreateView : list size = " + saved_list.size());
                        Log.d("TAG", "onReCreateView : page no = " + saved_page_counter);
                        Log.d("TAG", "onReCreateView : total pages = " + saved_total_pages);
                        Log.d("TAG", "onReCreateView : popularFragment.movie_rank = " + PopularFragment.movie_rank);
                        Log.d("TAG", "onReCreateView : popularInteractorImp.movies_filter = " + PopularInteractorImp.movies_filter.size());

                        mPopularView.hideProgress();
                        mPopularView.showRecyclerViewLayout();
                        mPopularView.showRecyclerView();
                        mPopularView.hideRefreshProgressAnimation();
                        mPopularInteractor.newCall();

                        mPopularView.receivePopularMovies(saved_list, saved_page_counter, saved_total_pages);
                        //mUpComingView.hideNoInternetLayout();

                    }
                    else // no data exist
                    {
                        Log.d("TAG", "onReCreateView: 6 ");
                        if (layout == 3) // no results
                        {
                            Log.d("TAG", "onReCreateView: 9");
                            mPopularView.hideProgress();
                            mPopularView.showNoResultsLayout();
                            PopularFragment.no_visible_layout = 3;
                        } else
                        {
                            Log.d("TAG", "onRecreateView: 6");

                            // go make a new request. using parameters
                            Log.d("TAG", "onReCreateView: saved list is empty");
                            mPopularView.hideRecyclerView();
                            mPopularView.hideRecyclerViewLayout();
                            mPopularView.showProgress();
                            mPopularInteractor.newCall();
                            mPopularInteractor.fetchPopularMovies(this, page_counter,genre_code);
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (mPopularView != null) {

            mPopularInteractor.newCall();
            mPopularView.hideRefreshProgressAnimation();
            mPopularView.hideProgress();
        }
    }

    @Override
    public void onItemClicked(String movie_id, String title) {
        Log.d("TAG", "onRecommendedMovieClicked: 1");
        if (mPopularView != null) {
            Log.d("TAG", "onRecommendedMovieClicked: 1");

            if (((PopularFragment) mPopularView).v != null) {
                Log.d("TAG", "onRecommendedMovieClicked: 1");
                mPopularView.moveToMovieFragment(movie_id,title);
            }
        }

    }


    @Override
    public void onFailed(String message) {
        Log.d("TAG", "onFailed: 1");

        if (mPopularView != null) {
            Log.d("TAG", "onFailed: 2");

            if (((PopularFragment) mPopularView).v != null) {
                Log.d("TAG", "onFailed: 3");

                if (((PopularFragment) mPopularView).isThereDataAvailable()) { // keda n show snackbar, el 7ala gomovies htkoon fl load more
                    Log.d("TAG", "onFailed: 4");
                    mPopularView.hideProgress();
                    mPopularView.hideRefreshProgressAnimation();
                    mPopularView.onLoadMoreFail();
                    mPopularView.showNoInternetSnak();
                    PopularFragment.no_visible_layout = 0;
                }
                else  // keda n hide all w  n show el no internet layout.
                {// if it is not visible
                    Log.d("TAG", "onFailed: 5");
                    mPopularView.hideProgress();
                    mPopularView.hideRecyclerViewLayout();
                    mPopularView.hideRefreshProgressAnimation();
                    mPopularView.hideRecyclerView();
                    mPopularView.showNoInternetLayout(); // show Connection error layout
                    PopularFragment.no_visible_layout = 0;
                    if (message != null) {
                        Log.d("TAG", "onFailed: 6");
                        mPopularView.showErrorMessage(message);
                    }
                }
                mPopularView.resetFlags();
            }
        }
    }

    @Override
    public void onSuccess(ArrayList<PopularMovie> popularMovies, int total_pages) {
        Log.d("TAG", "onSuccess:  1");

        if (mPopularView != null) {
            Log.d("TAG", "onSuccess:  2");

            if (((PopularFragment) mPopularView).v != null) {
                Log.d("TAG", "onSuccess:  3");

                if(!popularMovies.isEmpty()||popularMovies.size()!=0)
                {
                    Log.d("TAG", "onSuccess:  4");
                    mPopularView.receivePopularMovies(popularMovies, 0, total_pages);
                    mPopularView.hideNoInternetLayout();
                    mPopularView.hideProgress();
                    mPopularView.showRecyclerViewLayout();
                    mPopularView.showRecyclerView();
                    mPopularView.hideRefreshProgressAnimation();
                    PopularFragment.no_visible_layout = 0;

                }
                else
                {
                    Log.d("TAG", "onSuccess:  5");

                    if(PopularFragment.page_counter < total_pages)
                    {
                        Log.d("TAG", "onSuccess:  6");
                        PopularFragment.total_pages = total_pages;
                        PopularFragment.page_counter++;
                        mPopularView.resetFlags();
                        PopularFragment.isOnLoadMore =true;

                        onLoadMore(PopularFragment.page_counter,PopularFragment.popular_genre);
                    }
                    else {
                        if (PopularFragment.newMovies.isEmpty()) {
                            Log.d("TAG", "onSuccess:  7");
                            mPopularView.hideNoInternetLayout();
                            mPopularView.hideProgress();
                            mPopularView.hideRefreshProgressAnimation();
                            mPopularView.hideRecyclerView();
                            mPopularView.hideRecyclerViewLayout();
                            mPopularView.showNoResultsLayout();
                            PopularFragment.newMovies.clear();
                            ((PopularFragment)mPopularView).mAdapter.notifyDataSetChanged();
                            PopularFragment.no_visible_layout = 3;
                            mPopularView.resetFlags();
                            ((MainActivity) ((PopularFragment) mPopularView).activity).showAppBarIfCollapsed();
                        }
                        else {
                            mPopularView.hideNoInternetLayout();
                            mPopularView.hideProgress();
                            mPopularView.hideRefreshProgressAnimation();
                            ((PopularFragment)mPopularView).mAdapter.hideFooter();
                            ((PopularFragment)mPopularView).mAdapter.notifyDataSetChanged();
                            PopularFragment.no_visible_layout = 0;
                            mPopularView.resetFlags();
                        }

                    }
                }
                } else {
                    Log.d("TAG", "onSuccess: popular 4");
                }
            }
        }


}


/*    @Override
    public void onExistInternet(boolean isConnected) {
        mPopularView.showNotice(isConnected);
    }*/
