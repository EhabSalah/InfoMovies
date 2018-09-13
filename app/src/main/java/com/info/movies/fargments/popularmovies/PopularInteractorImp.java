package com.info.movies.fargments.popularmovies;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.info.movies.rest.ApiInterface;
import com.info.movies.models.ApiError;
import com.info.movies.models.posters.PopularMovie;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.helpers.ErrorUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.info.movies.fargments.popularmovies.PopularFragment.movie_rank;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class PopularInteractorImp implements PopularInteractor {
    public static final String SAVED_MOVIES_FILTER = "movies_filter";
    private ApiInterface apiInterface;
    public static ArrayList<Integer> movies_filter;
    private static int total_pages;
    private Movies movies;
    private static Call<Movies> call;
    private static AsyncTask executePopularMoviesTask;

    public PopularInteractorImp() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        movies_filter = new ArrayList<>();
    }

    @Override
    public void newCall() {
        Log.d("TAG", "newCall :PopularInteractorImp: 1");
        if (call!=null) {
            Log.d("TAG", "newCall :PopularInteractorImp: 2");
            call.cancel();
            call = null;
        }
        if (executePopularMoviesTask !=null)
        {
            Log.d("TAG", "newCall :PopularInteractorImp: 3");
            executePopularMoviesTask.cancel(true);
            executePopularMoviesTask = null;
        }
    }

    @Override
    public void refreshInteractor() {
        Log.d("TAG", "refreshInteractor :PopularInteractorImp: 1");
        if (call!=null )
        {
            Log.d("TAG", "refreshInteractor :PopularInteractorImp: 2");
            call.cancel();
            call = null;
        }
        if (executePopularMoviesTask !=null)
        {
            Log.d("TAG", "refreshInteractor :PopularInteractorImp: 3");
            executePopularMoviesTask.cancel(true);
            executePopularMoviesTask = null;
        }
        movies_filter.clear();
        movies = null;
        movie_rank = 1;
    }

    @Override
    public void fetchPopularMovies(OnMoviesResponseListener responseListener, int required_page_no, int popular_genre) {
        Log.d("TAG", "fetchTopRatedMovies: required_page_no= "+required_page_no);
        serverRequest(responseListener, required_page_no, popular_genre);
    }

    private void serverRequest(final OnMoviesResponseListener responseListener, int required_page_no, int popular_genre) {
        call = apiInterface.getPopularMovies(required_page_no,popular_genre ==0? "" : String.valueOf(popular_genre));
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Log.d("TAG", "onResponse: popular: 1");

                if(response.isSuccessful()) {
                    Log.d("TAG", "onResponse: popular: 2");

                    movies = response.body();
                    if (movies != null) {
                        Log.d("TAG", "onResponse: popular: 2.1");


                        checkTotalPages();
                        if (PopularInteractorImp.call != null && !PopularInteractorImp.call.isCanceled()) {
                            Log.d("TAG", "onResponse: popular: 2.2");

                            executePopularMoviesTask = new ExecutePopularMoviesTask(responseListener).execute(movies);
                        }

                    }
                    else
                    {
                        Log.d("TAG", "onResponse: popular: 3");
                        ApiError error = ErrorUtils.parseError(response);
                        responseListener.onFailed(error.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t)
            {
                Log.d("TAG", "onFailure: popular: 1");
                if (!call.isCanceled())
                {
                    Log.d("TAG", "onFailure: popular: 2");
                    responseListener.onFailed(null);
                }
                else
                {
                    Log.d("TAG", "onFailure: popular: 3");
                }
            }
        });

    }
    private void checkTotalPages() {
        Log.d("TAG", "checkTotalPages: 1");
        if (movies!=null)
        {
            Log.d("TAG", "checkTotalPages: 2");
            total_pages = Integer.parseInt(movies.getTotal_pages());
        }
    }

    private static ArrayList<PopularMovie> extractNeededMovieData(Movies popularMovies) {
        Log.d("TAG", "extractNeededMovieData:popularInteractorIMP: 1");
        int size = popularMovies.getMoviesList().size();

        ArrayList<PopularMovie> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if ( executePopularMoviesTask== null||executePopularMoviesTask.isCancelled()) {
                Log.d("TAG", "extractNeededMovieData:popularInteractorIMP: 2");
                break;
            }
            String poster = popularMovies.getMoviesList().get(i).getPoster_path();
            String id = popularMovies.getMoviesList().get(i).getId();
            String vote_average = popularMovies.getMoviesList().get(i).getVote_average();
            String title = popularMovies.getMoviesList().get(i).getTitle();
            int rank = movie_rank;

            if(poster != null /*&& !vote_average.equals("0") */ && id != null ){
                PopularMovie movie = new PopularMovie(poster, id, vote_average,String.valueOf(rank), title);
                if (!movies_filter.contains(Integer.parseInt(movie.getMovie_id())))
                {
                    arrayList.add(movie);
                    movies_filter.add(Integer.parseInt(movie.getMovie_id()));
                    movie_rank++;
                }
            }

        }
        Log.d("TAG", "extractNeededMovieData:popularInteractorIMP: 3");
        return arrayList;
    }

    private static class ExecutePopularMoviesTask extends AsyncTask<Movies, Void, ArrayList<PopularMovie>> {
        OnMoviesResponseListener responseListener;
        ExecutePopularMoviesTask(OnMoviesResponseListener responseListener)
        {
            this.responseListener = responseListener;
        }
        @Override
        protected ArrayList<PopularMovie> doInBackground(Movies... popularMovies) {
            Log.d("TAG", "onResponse: popular: 1");
            return extractNeededMovieData(popularMovies[0]); // do extraction
        }

        @Override
        protected void onPostExecute(ArrayList<PopularMovie> popularMovies) {
            Log.d("TAG", "onResponse: popular: 1");
            if (PopularInteractorImp.call != null && !PopularInteractorImp.call.isCanceled()) {
                responseListener.onSuccess(popularMovies, total_pages); // update UI
            }
            else
            {
                Log.d("TAG", "onPostExecute: popular: 3 call canceled");
            }
        }
    }
}
