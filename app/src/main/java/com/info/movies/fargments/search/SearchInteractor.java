package com.info.movies.fargments.search;

import com.info.movies.models.SearchResultsModel;
import com.info.movies.models.SearchSuggestionsModel;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public interface SearchInteractor {
    void fetchQuerySuggestions(OnMoviesResponseListener responseListener, String query_text, int page);

    void newCall();

    void fetchQueryResults(OnMoviesResponseListener responseListener, String query_text, int page);

    interface OnMoviesResponseListener {
        void onFailed(String message);

        void onSuggestionSuccess(ArrayList<SearchSuggestionsModel> movies);

        void onResultSuccess(ArrayList<SearchResultsModel> searchResultsModels, int total_pages, int results_page_counter);
    }
}
