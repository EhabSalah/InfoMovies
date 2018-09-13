package com.info.movies.fargments.search;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.info.movies.models.SearchResultsModel;
import com.info.movies.models.SearchSuggestionsModel;

import java.util.ArrayList;

import static com.info.movies.fargments.search.SearchFragment.TAG_LAST_RESULT_QUERY_TEXT;
import static com.info.movies.fargments.search.SearchFragment.TAG_LAST_SEARCH_FIELD_TEXT;
import static com.info.movies.fargments.search.SearchFragment.TAG_LAST_SUGGESTION_QUERY_TEXT;
import static com.info.movies.fargments.search.SearchFragment.TAG_RESULTS_ARRAY;
import static com.info.movies.fargments.search.SearchFragment.TAG_RESULTS_PAGE_COUNTER;
import static com.info.movies.fargments.search.SearchFragment.TAG_RESULTS_TOTAL_PAGES;
import static com.info.movies.fargments.search.SearchFragment.TAG_SUGGESTIONS_ARRAY;
import static com.info.movies.fargments.search.SearchFragment.TAG_VISIBILITY_RESULTS_RV;
import static com.info.movies.fargments.search.SearchFragment.TAG_VISIBILITY_SUGGESTIONS_RV;


/**
 * Created by EhabSalah on 11/24/2017.
 */

public class SearchPresenterIMP implements SearchPresenter , SearchInteractor.OnMoviesResponseListener {

    private MySearchView mySearchView ;
    private SearchInteractor mSearchInteractor;
    private Fragment mSearchFragment;

    public SearchPresenterIMP(MySearchView mySearchView) {

       this. mySearchView = mySearchView;
        mSearchInteractor = new SearchInteractorIMP();
        this.mSearchFragment = (Fragment) mySearchView;
    }

    @Override
    public void onSubmit(final String query_text, final int page) { // show progress && make call
        Log.d("TAG", "onSubmit: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onSubmit: presenter 2");

            if (((SearchFragment)mySearchView).v!=null) {
                Log.d("TAG", "onSubmit: presenter 3");

                mySearchView.hideSuggestionList();
                mySearchView.hideNoResultFoundText();
                mySearchView.hideResultList();
                mSearchInteractor.newCall();
                mySearchView.showProgress();  // - 1
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "run: ");
                        mSearchInteractor.fetchQueryResults(SearchPresenterIMP.this,query_text,page); // - 2
                    }
                },500);
            }
        }

    }

    @Override
    public void onTexting(String query_text, int page) { // make call
        Log.d("TAG", "onTexting: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onTexting: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onTexting: presenter 3");
                mySearchView.hideProgress();
                mySearchView.hideNoResultFoundText();
                mySearchView.hideResultList();
                mySearchView.hideSuggestionList();
                mSearchInteractor.newCall();
                mSearchInteractor.fetchQuerySuggestions(this, query_text, page); // - 1
            }
        }
    }

    @Override
    public void onLoadMore(String query_text, int page) {
        Log.d("TAG", "onLoadMore: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onLoadMore: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onLoadMore: presenter 3");
                mSearchInteractor.newCall(); // if there is a call not finished.
                mSearchInteractor.fetchQueryResults(this, query_text, page);
            }
        }
    }

    @Override
    public void onCloseButtonClick() {
        Log.d("TAG", "onCloseButtonClick: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onCloseButtonClick: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onCloseButtonClick: presenter 3");
                mSearchInteractor.newCall();
                mySearchView.hideResultList();
                mySearchView.hideProgress();
                mySearchView.hideSuggestionList();
                mySearchView.hideNoResultFoundText();
                // mySearchView.showCursor();
            }
        }
    }

    @Override
    public void onSuggestionClick(String suggested_title) {
        Log.d("TAG", "onSuggestionClick: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onSuggestionClick: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onSuggestionClick: presenter 3");
                mySearchView.searchClickedSuggestion(suggested_title);
            }
        }
    }

    @Override
    public void onSuggestionLeftUpArrowClick(String suggested_title) {
        Log.d("TAG", "onSuggestionLeftUpArrowClick: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onSuggestionLeftUpArrowClick: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onSuggestionLeftUpArrowClick: presenter 3");
                mySearchView.changeSearchFieldText(suggested_title);
            }
        }
    }

    @Override
    public void onEmptiedSearchView() {
        Log.d("TAG", "onEmptiedSearchView: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onEmptiedSearchView: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onEmptiedSearchView: presenter 3");
                mSearchInteractor.newCall();
                mySearchView.hideResultList();
                mySearchView.hideProgress();
                mySearchView.hideSuggestionList();
                // mySearchView.showNo("No results found");
            }
        }
    }

    @Override
    public void onReCreateView(Bundle savedData) { // state: search field cursor, results vis, sugg vis, no rf vis | saved results and suggestions
        // check saved visibility of views
        // if suggestion rescycler view saved visibility is visible & RRV & NO RF are invisible, if yes check for saved suggestions and pass them again to recieve method
        // else if saved visibility of results resyclerview is visible and other are not ,so check for saved results ann re pass it.
        // else if any error gone and could not find saved objects even for suggestions or results then hide both layouts and show NO RF and searchfiend cursor true, 'll try to prevent this case :)
        // else if visibility of suggestions and  results are invisible then show NO RF layout
        Log.d("TAG", "onReCreateView: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onReCreateView: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onReCreateView: presenter 3");
                if (savedData != null) {
                    Log.d("TAG", "onReCreateView: presenter 4 ");

                    ArrayList<SearchSuggestionsModel> suggestions = savedData.getParcelableArrayList(TAG_SUGGESTIONS_ARRAY);
                    ArrayList<SearchResultsModel> results = savedData.getParcelableArrayList(TAG_RESULTS_ARRAY);

                    int results_total_pages = savedData.getInt(TAG_RESULTS_TOTAL_PAGES);
                    int results_page_counter = savedData.getInt(TAG_RESULTS_PAGE_COUNTER);

                    int results_visibility = savedData.getInt(TAG_VISIBILITY_RESULTS_RV);
                    int suggestion_visibility = savedData.getInt(TAG_VISIBILITY_SUGGESTIONS_RV);

                    String search_field_text = savedData.getString(TAG_LAST_SEARCH_FIELD_TEXT);
                    String result_keyword = savedData.getString(TAG_LAST_RESULT_QUERY_TEXT);
                    String suggestion_keyword = savedData.getString(TAG_LAST_SUGGESTION_QUERY_TEXT);

                    Log.d("TAG", "onReCreateView: Bundle =  " + savedData.toString());
                    Log.d("TAG", "onReCreateView: Suggestions =  " + suggestions.size());
                    Log.d("TAG", "onReCreateView: Results =  " + results.size());
                    Log.d("TAG", "onReCreateView: Results_Pages =  " + results_total_pages);
                    Log.d("TAG", "onReCreateView: Results_Counter =  " + results_page_counter);
                    Log.d("TAG", "onReCreateView: Suggestions Visibility =  " + suggestion_visibility);
                    Log.d("TAG", "onReCreateView: Results Visibility =  " + results_visibility);
                    Log.d("TAG", "onReCreateView: Search Field Last Text =  " + search_field_text);
                    Log.d("TAG", "onReCreateView: Query Result Last Text =  " + result_keyword);
                    Log.d("TAG", "onReCreateView: Query Suggestion Last Text =  " + suggestion_keyword);

                    //  Cases are 3 , suggestions are visible && results not visible, results visible and suggestions not visible, no reslts visible.

                    if (suggestion_visibility == View.VISIBLE && results_visibility != View.VISIBLE) // 1st case, suggestion visible
                    {
                        Log.d("TAG", "onReCreateView: presenter 5");

                        if (suggestion_keyword != null && search_field_text != null) {
                            Log.d("TAG", "onReCreateView: presenter 6");

                            if (suggestion_keyword.equals(search_field_text) && !suggestions.isEmpty()) // if they are equal
                            {
                                Log.d("TAG", "onReCreateView: presenter 7");

                                mySearchView.hideProgress();
                                mySearchView.hideNoResultFoundText();
                                mySearchView.hideResultList();
                                mySearchView.receiveSuggestions(suggestions, suggestion_keyword);
                                mySearchView.showSuggestionList();
                            }
                        }
                    } else if (suggestion_visibility != View.VISIBLE && results_visibility == View.VISIBLE)// 2nd case, result visible
                    {
                        Log.d("TAG", "onReCreateView: presenter 8");

                        if (result_keyword != null && search_field_text != null && !results.isEmpty()) {
                            Log.d("TAG", "onReCreateView: presenter 9");

                            if (result_keyword.equals(search_field_text) && !results.isEmpty())
                            {
                                Log.d("TAG", "onReCreateView: presenter 10");

                                mySearchView.hideCursor();
                                mySearchView.hideNoResultFoundText();
                                mySearchView.hideSuggestionList();
                                mySearchView.hideProgress();
                                mySearchView.receiveResults(results, results_total_pages, results_page_counter, result_keyword);
                                mySearchView.showResultList();
                                mySearchView.hideCursor();
                            }
                        }
                    }
                }
            }
        }
    }



    @Override
    public void onSaveInstanceState() {
        Log.d("TAG", "onSaveInstanceState: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onSaveInstanceState: presenter 2");
            mSearchInteractor.newCall();
        }
    }

    @Override
    public void onSearchFieldClickedSuggestionsExist()
    {
        Log.d("TAG", "onSearchFieldClickedSuggestionsExist: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onSearchFieldClickedSuggestionsExist: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onSearchFieldClickedSuggestionsExist: presenter 3");
                mySearchView.hideResultList();
                mySearchView.showSuggestionList();
                mySearchView.showCursor();
            }
        }
    }

    @Override
    public void onItemClicked(String id, String movie_title) {

        Log.d("TAG", "onRecommendedMovieClicked: presenter 1");
        if (mySearchView != null) {
            Log.d("TAG", "onRecommendedMovieClicked: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onRecommendedMovieClicked: presenter 3");

                mySearchView.moveToMovieFragment(id,movie_title);
            }
        }
    }

    /*********************************************************************************************/

    @Override
    public void onFailed(String message) {
        Log.d("TAG", "onFailed: presenter 1");
        if (mySearchView!=null) {
            Log.d("TAG", "onFailed: presenter 2");

            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onFailed: presenter 3");
                mySearchView.hideProgress();
        /*mySearchView.hideResultList();
        mySearchView.hideSuggestionList();*/
                if (message != null) {
                    Log.d("TAG", "onFailed: presenter 4");

                    if (message.equals("No results found")) {
                        Log.d("TAG", "onFailed: presenter 5");

                        mySearchView.hideResultList();
                        mySearchView.hideSuggestionList();
                        mySearchView.showNo(message);
                    } else
                    {
                        Log.d("TAG", "onFailed: presenter 6");
                        mySearchView.recieveErrorMessage(message);
                    }

                } else{
                    Log.d("TAG", "onFailed: presenter 7");

                    mySearchView.showNo(null);
                }
            }
        }
    }

    @Override
    public void onSuggestionSuccess(ArrayList<SearchSuggestionsModel> movies) { // show suggestions
        Log.d("TAG", "onSuggestionSuccess: 1 ");
        if (mySearchView!=null) {
            Log.d("TAG", "onSuggestionSuccess: 2 ");
            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onSuggestionSuccess: 3 ");

                if (((SearchFragment) mySearchView).newSuggestion != null) {
                    Log.d("TAG", "onSuggestionSuccess: 4");
                    mySearchView.receiveSuggestions(movies, null); // null is tag (for server request data)
                    mySearchView.hideResultList();
                    mySearchView.hideProgress();
                    mySearchView.hideNoResultFoundText();
                    mySearchView.showSuggestionList(); // -1
                } else {
                    Log.d("TAG", "onSuggestionSuccess: 5 ");
                }
            }
        }
    }

    @Override
    public void onResultSuccess(ArrayList<SearchResultsModel> searchResultsModels, int total_pages, int results_page_counter)
    { // - pass received data- show results
        Log.d("TAG", "onResultSuccess: 1 ");

        if (mySearchView!=null)
        {
            Log.d("TAG", "onResultSuccess: 2 ");
            if (((SearchFragment) mySearchView).v != null) {
                Log.d("TAG", "onResultSuccess: 3 ");

                if (((SearchFragment) mySearchView).newResults != null) {
                    Log.d("TAG", "onResultSuccess: 4 ");
                    mySearchView.hideProgress();
                    mySearchView.hideNoResultFoundText();
                    mySearchView.hideSuggestionList();
                    mySearchView.receiveResults(searchResultsModels, total_pages, results_page_counter, null); // -1
                    mySearchView.showResultList(); // -2
                    mySearchView.hideCursor();
                } else {
                    Log.d("TAG", "onResultSuccess: 5 ");
                }
            }
        }
    }
}
