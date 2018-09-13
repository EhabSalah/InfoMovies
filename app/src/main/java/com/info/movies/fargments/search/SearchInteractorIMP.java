package com.info.movies.fargments.search;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.info.movies.models.movies_response.Movies;
import com.info.movies.rest.ApiInterface;
import com.info.movies.rest.helpers.ErrorUtils;
import com.info.movies.models.ApiError;
import com.info.movies.models.SearchResultsModel;
import com.info.movies.models.SearchSuggestionsModel;
import com.info.movies.rest.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public class SearchInteractorIMP implements SearchInteractor {

    private static final int RESULTS_PAGE_COUNTER = 0; // zero to detect that data sent from response
    private ApiInterface apiInterface;
    private static ArrayList<Integer> movies_filter;
    private static ArrayList<String> suggestions_filter;
    private Movies movies;
    private static int  total_pages;
    public static Call<Movies> call;
    private static AsyncTask executeResults;
    private static AsyncTask executeSuggestions;

    public SearchInteractorIMP() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        movies_filter = new ArrayList<>();
        suggestions_filter = new ArrayList<>();
    }

    @Override
    public void fetchQuerySuggestions(OnMoviesResponseListener responseListener, String query_text, int page) {
        Log.d("tag", "fetchQuerySuggestions: query_text = "+query_text);
        serverRequestSuggestion(responseListener,query_text, page);
    }

    @Override
    public void newCall()
    {
        Log.d("TAG", "newCall: 1");
        if (call!=null)
        {
            Log.d("TAG", "newCall: 2");

            call.cancel();
           // call = null;
        }
        if (executeResults!=null)
        {
            Log.d("TAG", "newCall: 3");

            executeResults.cancel(true);
            executeResults = null;
        }
        if (executeSuggestions!=null)
        {
            Log.d("TAG", "newCall: 4");

            executeSuggestions.cancel(true);
            executeSuggestions =null;
        }
        Log.d("TAG", "newCall: 5");
        movies_filter.clear();
        suggestions_filter.clear();
        movies = null;
    }

    @Override
    public void fetchQueryResults(OnMoviesResponseListener responseListener, String query_text, int page) {
        Log.d("tag", "fetchQueryResults: query_text = "+query_text + "page= "+page);

        serverRequestResult(responseListener,query_text, page);
    }

    private void serverRequestResult(final OnMoviesResponseListener responseListener, String query_text, int page) {
        Log.d("TAG", "serverRequestResult: query_text= "+query_text+" page = "+page);

        call = apiInterface.getQuery(query_text,page);
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull final Call<Movies> call, @NonNull Response<Movies> response)
            {
                Log.d("TAG", "onResponse: results 1");

                    if(response.isSuccessful()) {
                    Log.d("TAG", "onResponse: results 2");
                    movies = response.body();
                    if (movies != null) {
                        Log.d("TAG", "onResponse: results 2.1");
                        checkTotalPages();

                        if (movies.getTotal_movies() > 0)
                        {
                            Log.d("TAG", "onResponse: results 3");
                            executeResults = new ExecuteResultsTask(responseListener).execute(movies);
                        }
                        else
                        {
                            Log.d("TAG", "onResponse: results 4");
                            // we have in such cases show  a text view with text "No Results".
                            responseListener.onFailed("No results found");
                        }
                    }
                }
                else
                {
                        Log.d("TAG", "onResponse: results 5");

                        ApiError error = ErrorUtils.parseError(response);
                        responseListener.onFailed(error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                Log.d("TAG", "onFailure: results 1");

                if (!call.isCanceled()) {
                    Log.d("TAG", "onFailure: results 2");
                    responseListener.onFailed(null);
                }
                else
                {
                    Log.d("TAG", "onFailure: results 3");
                }
            }
        });
    }

    private void serverRequestSuggestion(final OnMoviesResponseListener responseListener, String query_text, int page) {
        call = apiInterface.getQuery(query_text,page);
        Log.d("TAG", "serverRequestSuggestion: text = "+query_text+" page = "+page);
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull final Call<Movies> call, @NonNull Response<Movies> response) {
                if(response.isSuccessful()){
                    Log.d("TAG", "onResponse: suggestions 1");
                    movies = response.body();
                    if (movies != null) {
                        Log.d("TAG", "onResponse: suggestions 2");

                        if (movies.getTotal_movies() > 0)
                        {
                            Log.d("TAG", "onResponse: suggestions 3");

                            executeSuggestions = new ExecuteSuggestionsTask(responseListener).execute(movies);
                        }
                        else
                        {
                            Log.d("TAG", "onResponse: suggestions 4");
                            // we have in such cases show  a text view with text "No Results".
                           // responseListener.onFailed("No results found");
                        }
                    }

                }
                else
                {
                    Log.d("TAG", "onResponse: suggestions 5");
                    ApiError error = ErrorUtils.parseError(response);
                    responseListener.onFailed(error.getMessage());
                    }
            }
            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                Log.d("TAG", "onFailure: suggestions 1");
                if (!call.isCanceled()) {
                    Log.d("TAG", "onFailure: suggestions 2");

                    // responseListener.onFailed(null);
                }else Log.d("TAG", "onFailure: suggestions 3");
            }
        });
    }

    private void checkTotalPages() {

            total_pages = Integer.parseInt(movies.getTotal_pages());
    }

    private static ArrayList<SearchSuggestionsModel> extractNeededSuggestionData(Movies movies)  {
        int size = movies.getMoviesList().size();
        ArrayList<SearchSuggestionsModel> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (executeSuggestions==null||executeSuggestions.isCancelled()) {
                Log.d("tag", "extractNeededSuggestionData: suggestionsTask is Canceled");
                break;
            }
            String movie_title = movies.getMoviesList().get(i).getTitle();


            if(movie_title != null && !suggestions_filter.contains(movie_title)){
                SearchSuggestionsModel movie = new SearchSuggestionsModel(movie_title);
                    arrayList.add(movie);
                    suggestions_filter.add(movie.getSuggested_title());
            }
        }
        return arrayList;
    }


    private static ArrayList<SearchResultsModel> extractNeededResultsData(Movies movies) {

        int size = movies.getMoviesList().size();
        ArrayList<SearchResultsModel> arrayList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (executeResults == null ||executeResults.isCancelled())
            {
                Log.d("tag", "extractNeededResultsData: resultsTask is Canceled");
                break;
            }
            String movie_title = movies.getMoviesList().get(i).getTitle();
            String id = movies.getMoviesList().get(i).getId();
            String poster_url = movies.getMoviesList().get(i).getPoster_path();
            String release_year = movies.getMoviesList().get(i).getRelease_date();
            if (movies_filter.contains(Integer.parseInt(id))) {
                Log.d("TAG", "extractNeededResultsData: movie already exists ");
            }
            if(movie_title != null && poster_url != null && id != null && !movies_filter.contains(Integer.parseInt(id)) ){
                SearchResultsModel movie = new SearchResultsModel(id, movie_title,poster_url,release_year);
                arrayList.add(movie);
                movies_filter.add(Integer.parseInt(movie.getId()));
            }
        }
        return arrayList;
    }

    static class ExecuteResultsTask extends  AsyncTask<Movies, Void, ArrayList<SearchResultsModel>>
    {
        OnMoviesResponseListener responseListener;

        ExecuteResultsTask(OnMoviesResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        protected ArrayList<SearchResultsModel> doInBackground(Movies... movies)
        {
            return extractNeededResultsData(movies[0]); // do extraction
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResultsModel> searchResultsModels) {
        Log.d("TAG", "onPostExecute: RESULTS 1");
        if (executeResults!=null&&!executeResults.isCancelled()) {
            Log.d("TAG", "onPostExecute: RESULTS 2");

            if (searchResultsModels.size() > 0) {
                Log.d("TAG", "onPostExecute: RESULTS 3");

                responseListener.onResultSuccess(searchResultsModels, total_pages, RESULTS_PAGE_COUNTER); // update UI
            } else responseListener.onFailed("No results found");
        }
    }
    }
    static class ExecuteSuggestionsTask extends AsyncTask<Movies, Void, ArrayList<SearchSuggestionsModel>>
    {
        OnMoviesResponseListener responseListener;

        ExecuteSuggestionsTask(OnMoviesResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        protected ArrayList<SearchSuggestionsModel> doInBackground(Movies... topRatedMovieMovies) {
            return extractNeededSuggestionData(topRatedMovieMovies[0]); // do extraction
        }

        @Override
        protected void onPostExecute(ArrayList<SearchSuggestionsModel> topRatedMovieMovies) {
            Log.d("TAG", "onPostExecute: SUGGESTIONS ");
            if (executeSuggestions!=null&&!executeSuggestions.isCancelled()) {
                Log.d("TAG", "onPostExecute: SUGGESTIONS call not canceled ");
                if (topRatedMovieMovies.size()>0)
                {
                    responseListener.onSuggestionSuccess(topRatedMovieMovies); // update UI
                }
            }
            else Log.d("TAG", "onPostExecute: SUGGESTIONS  call is canceled ");
        }
    }
}
