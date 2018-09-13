package com.info.movies.fargments.toprated;

import android.os.Bundle;
import android.util.Log;

import com.info.movies.MainActivity;

import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

import static com.info.movies.constants.Common.getGenreCode;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class TopRatedPresenterImp implements TopRatedPresenter, TopRatedInteractor.OnMoviesResponseListener/*, TopRatedInteractor.OnCheckInternetExistance*/ {
    TopRatedView mTopRatedView;


    TopRatedInteractor mTopRatedInteractor;

    public TopRatedPresenterImp(TopRatedView TopRatedView) {
        this.mTopRatedView = TopRatedView;
        this.mTopRatedInteractor = new TopRatedInteractorImp();
    }


    @Override
    public void onCreateView(int required_page_no, String year, int genre) {
        Log.d("TAG", "onCreateView: 1 ");

        if (mTopRatedView != null) {
            Log.d("TAG", "onCreateView: 1 ");
            if (((TopRatedFragment) mTopRatedView).v != null) {

                mTopRatedView.hideRecyclerViewLayout();
                mTopRatedView.hideRecyclerView();
                mTopRatedView.hideNoInternetLayout();
                mTopRatedView.hideSpinners();
                mTopRatedView.hideRefreshProgress();
                mTopRatedView.hideNoResultsLayout();
                mTopRatedView.showProgress();
                mTopRatedInteractor.refreshInteractor();
                mTopRatedInteractor.fetchTopRatedMovies(this, required_page_no,year,genre);
            }
        }
    }

    @Override
    public void onRefresh(int required_page_no, String year, int genre) {
        Log.d("TAG", "onRefresh: presenter: 1");
        if (mTopRatedView != null) {
            Log.d("TAG", "onRefresh: presenter: 2");
            if (((TopRatedFragment) mTopRatedView).v != null) {
                mTopRatedInteractor.refreshInteractor();
                mTopRatedInteractor.fetchTopRatedMovies(this, required_page_no, year, genre);
            }
        }
    }

    @Override
    public void onLoadMore(int required_page_no, String year, int genre) {
        Log.d("TAG", "onLoadMore: 1");
        if (mTopRatedView != null) {
            Log.d("TAG", "onLoadMore: 2");
            if (((TopRatedFragment) mTopRatedView).v != null) {
                Log.d("TAG", "onLoadMore: 3");
                mTopRatedInteractor.newCall();
                mTopRatedInteractor.fetchTopRatedMovies(this, required_page_no, year, genre);
            }
        }
    }

    @Override
    public void onSort(int page, String year, int genre) {
        Log.d("TAG", "onSort: 1");
        if (mTopRatedView != null) {
            Log.d("TAG", "onSort: 2");
            if (((TopRatedFragment) mTopRatedView).v != null) {
                Log.d("TAG", "onSort: 3");
                mTopRatedView.hideNoResultsLayout();
                mTopRatedView.showSortProgress();
                mTopRatedInteractor.refreshInteractor();
                mTopRatedInteractor.fetchTopRatedMovies(this,page,year,genre);
            }
        }
    }

    @Override
    public void onItemClicked(String movie_id, String title) {
        Log.d("TAG", "onRecommendedMovieClicked: 1");
        if (mTopRatedView != null) {
            Log.d("TAG", "onRecommendedMovieClicked: 2");
            if (((TopRatedFragment) mTopRatedView).v != null) {
                Log.d("TAG", "onRecommendedMovieClicked: 3");
                mTopRatedView.moveToMovieFragment(movie_id,title);
            }
        }
    }

    @Override
    public void onRecreateView(Bundle savedState, int page_counter, String year, int genre) {
        Log.d("TAG", "onRecreateView: 1");
        if (mTopRatedView != null) {
            Log.d("TAG", "onRecreateView: 2");
            if (((TopRatedFragment) mTopRatedView).v != null) {
                Log.d("TAG", "onRecreateView: 3");
                int layout = 0;
            if (savedState != null)   // data saved
            {

                    Log.d("TAG", "onRecreateView: 4");

                    Bundle b = savedState;
                    ArrayList<TopRatedMovie> saved_list = b.getParcelableArrayList(TopRatedFragment.SAVED_MOVIES_ARRAY_TOPRATED);
                    int saved_total_pages = b.getInt(TopRatedFragment.SAVED_TOTAL_PAGES_TOPRATED);
                    int saved_page_counter = b.getInt(TopRatedFragment.SAVED_MOVIES_PAGE_NO_TOPRATED);
                    String saved_genre = b.getString(TopRatedFragment.SAVED_CURRENT_GENRE);
                    String saved_year = b.getString(TopRatedFragment.SAVED_CURRENT_YEAR);
                    TopRatedInteractorImp.movies_filter = b.getIntegerArrayList(TopRatedInteractorImp.SAVED_MOVIES_FILTER);
                    layout = b.getInt(TopRatedFragment.SAVED_NO_LAYOUT);

                if (saved_list != null && saved_list.size() != 0) { // data exist
                               Log.d("TAG", "onRecreateView: 5");

                        Log.d("TAG", "onReCreateView: TOPRATED : list size = " + saved_list.size());
                        Log.d("TAG", "onReCreateView: TOPRATED : page no" + saved_page_counter);
                        Log.d("TAG", "onReCreateView: TOPRATED : total pages" + saved_total_pages);
                        Log.d("TAG", "onReCreateView: TOPRATED : TopRatedInteractorImp.movies_filter size = " + TopRatedInteractorImp.movies_filter);

                        mTopRatedInteractor.newCall();
                        mTopRatedView.receiveTopRatedMovies(saved_list, saved_page_counter, saved_total_pages, saved_year, saved_genre);
                        mTopRatedView.hideProgress();
                        mTopRatedView.showRecyclerViewLayout();
                        mTopRatedView.showRecyclerView();
                        mTopRatedView.showSpinners();
                        mTopRatedView.hideRefreshProgress();
                        TopRatedFragment.no_visible_layout = 0;

                    }
                else // no data exist
                {
                    if (layout == 3) // no results
                    {
                        Log.d("TAG", "onReCreateView: 9");
                        mTopRatedView.hideProgress();
                        mTopRatedView.showNoResultsLayout();
                        mTopRatedView.showSpinners();
                        TopRatedFragment.no_visible_layout = 3;
                    }
                    else
                    {
                        Log.d("TAG", "onRecreateView: 6");

                        // go make a new request. using parameters
                        Log.d("TAG", "onReCreateView: saved list is empty");
                        mTopRatedView.hideRecyclerView();
                        mTopRatedView.hideRecyclerViewLayout();
                        mTopRatedView.hideSpinners();
                        mTopRatedView.showProgress();
                        mTopRatedInteractor.newCall();
                        mTopRatedInteractor.fetchTopRatedMovies(this, page_counter, year, genre);
                    }
                }
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (mTopRatedView!=null) {
            Log.d("TAG", "onDestroyView: 1 ");
            mTopRatedInteractor.newCall();
            mTopRatedView.hideProgress();
            mTopRatedView.hideRefreshProgress();
        }
    }


    @Override
    public void onFailed (String message){
        Log.d("TAG", "onFailed:  1");

        if (mTopRatedView != null) {
            Log.d("TAG", "onFailed:  2");

            if (((TopRatedFragment) mTopRatedView).v != null) {
                Log.d("TAG", "onFailed:  3");

                if (((TopRatedFragment) mTopRatedView).isThereDataAvailable()) { // keda n show snackbar, el 7ala gomovies htkoon fl load more
                    Log.d("TAG", "onFailed:  4");
                    mTopRatedView.hideProgress();
                    mTopRatedView.hideRefreshProgress();
                    mTopRatedView.onLoadMoreFail();
                    mTopRatedView.showNoInternetSnak();
                    TopRatedFragment.no_visible_layout = 0;
                } else  // keda n hide all w  n show el no internet layout.
                {// if it is not visible
                    Log.d("TAG", "onFailed: 5");
                    mTopRatedView.hideProgress();
                    mTopRatedView.hideRecyclerViewLayout();
                    mTopRatedView.hideRefreshProgress();
                    mTopRatedView.hideRecyclerView();
                    mTopRatedView.hideSpinners();
                    mTopRatedView.showNoInternetLayout(); // show Connection error layout
                    TopRatedFragment.no_visible_layout = 2;
                    if (message != null) {
                        Log.d("TAG", "onFailed: 6");
                        mTopRatedView.showErrorMessage(message);
                    }
                }
                mTopRatedView.resetFlags();
            }
        }
    }

    @Override
    public void onSuccess(ArrayList<TopRatedMovie> topRatedMovies, int totalPages, String year, String genre){
                Log.d("TAG", "onSuccess:  1");
                if (mTopRatedView != null) {
                    Log.d("TAG", "onSuccess: topRated 2");
                    if (((TopRatedFragment) mTopRatedView).v != null)
                    {
                        Log.d("TAG", "onSuccess: topRated 3");

                        if(!topRatedMovies.isEmpty()||topRatedMovies.size()!=0)
                        {

                            Log.d("TAG", "onSuccess: topRated 4");
                            mTopRatedView.receiveTopRatedMovies(topRatedMovies, 0, totalPages, year, genre);
                            mTopRatedView.hideNoInternetLayout();
                            mTopRatedView.hideProgress();
                            mTopRatedView.showRecyclerViewLayout();
                            mTopRatedView.showRecyclerView();
                            mTopRatedView.showSpinners();
                            mTopRatedView.hideRefreshProgress();
                            TopRatedFragment.no_visible_layout = 0;
                        }
                        else
                        {
                            Log.d("TAG", "onSuccess: topRated 5");
                            if(TopRatedFragment.page_counter < totalPages)
                            {
                                Log.d("TAG", "onSuccess: topRated 6");
                                TopRatedFragment.total_pages = totalPages;
                                TopRatedFragment.page_counter++;
                                mTopRatedView.resetFlags();
                                TopRatedFragment.isOnLoadMore =true;
                                onLoadMore(TopRatedFragment.page_counter,TopRatedFragment.selected_year.equals(TopRatedFragment.setting.spinners_default_word) ? null : TopRatedFragment.selected_year, TopRatedFragment.selected_genre.equals(TopRatedFragment.setting.spinners_default_word) ? 0 : getGenreCode(TopRatedFragment.selected_genre));
                            }
                            else
                            {
                                Log.d("TAG", "onSuccess: topRated 7");
                                mTopRatedView.hideNoInternetLayout();
                                mTopRatedView.hideProgress();
                                mTopRatedView.hideRefreshProgress();
                                mTopRatedView.hideRecyclerView();
                                mTopRatedView.hideRecyclerViewLayout();
                                mTopRatedView.showNoResultsLayout();
                                mTopRatedView.showSpinners();
                                ((TopRatedFragment)mTopRatedView).newMovies.clear();
                                ((MainActivity)((TopRatedFragment) mTopRatedView).activity).showAppBarIfCollapsed();
                                TopRatedFragment.no_visible_layout = 3;
                                mTopRatedView.resetFlags();
                            }
                        }

                    }
                    else
                    {
                        Log.d("TAG", "onSuccess: topRated 4");
                    }

                }
            }
}


