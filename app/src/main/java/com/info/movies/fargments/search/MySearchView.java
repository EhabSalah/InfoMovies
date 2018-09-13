package com.info.movies.fargments.search;

import com.info.movies.models.SearchResultsModel;
import com.info.movies.models.SearchSuggestionsModel;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public interface MySearchView {
    void showResultList();
    void hideResultList();
    void showSuggestionList();
    void hideSuggestionList();
    void showProgress();
    void hideProgress();

    void recieveErrorMessage(String message);

    void receiveSuggestions(ArrayList<SearchSuggestionsModel> suggestions, String keyword);

    void receiveResults(ArrayList<SearchResultsModel> results, int pages, int results_page_counter, String result_keyword);

    void showNo(String message);
    void hideNoResultFoundText();

    void searchClickedSuggestion(String suggested_title);

    void changeSearchFieldText(String suggested_title);

    boolean stateOfSavedState();

    void showNoInternetLayout();
    void hideNoInternetLayout();


    void hideCursor();


    void showCursor();

    void moveToMovieFragment(String id, String movie_title);
}
