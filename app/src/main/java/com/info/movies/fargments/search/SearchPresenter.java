package com.info.movies.fargments.search;

import android.os.Bundle;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public interface SearchPresenter {
    void onSubmit(String query_text, int page);
    void onTexting(String query_text, int page);

    void onLoadMore(String search_word, int page_counter);

    void onCloseButtonClick();

    /**
     * @param suggested_title
     */
    void onSuggestionClick(String suggested_title);

    void onSuggestionLeftUpArrowClick(String suggested_title);

    void onEmptiedSearchView();

    void onReCreateView(Bundle savedData);

    void onSaveInstanceState();

    void onSearchFieldClickedSuggestionsExist();

    void onItemClicked(String id, String movie_title);
}
