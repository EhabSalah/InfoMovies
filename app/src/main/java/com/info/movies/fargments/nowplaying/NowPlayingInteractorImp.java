package com.info.movies.fargments.nowplaying;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.info.movies.models.ApiError;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.models.posters.MoviePosterAR;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.ApiInterface;
import com.info.movies.rest.helpers.ErrorUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by EhabSalah on 10/31/2017.
 */

public class NowPlayingInteractorImp implements NowPlayingInteractor {

    private static final String TAG  = NowPlayingInteractorImp.class.getSimpleName();
    public static final String SAVED_MOVIES_FILTER = "movies_filter";
    private com.info.movies.rest.ApiInterface apiInterface;
    public static ArrayList<Integer> movies_filter;
    private static Call<com.info.movies.models.movies_response.Movies> call;

    private static int total_pages;
    private Movies movies;

    private static AsyncTask executeMoviesTask;


    public NowPlayingInteractorImp() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        movies_filter = new ArrayList<>();
    }

    @Override
    public void newCall() {
        Log.d(TAG, "newCall :NowPlayingInteractorImp: 1");
        if (call!=null)
        {
            Log.d(TAG, "newCall :NowPlayingInteractorImp: 2");
            call.cancel();
            call = null;
        }
        if (executeMoviesTask !=null)
        {
            Log.d(TAG, "newCall :: 3");
            executeMoviesTask.cancel(true);
            executeMoviesTask = null;
        }
    }

    @Override
    public void refreshInteractor() {
        Log.d(TAG, "refreshInteractor :: 1");
        if (call!=null ) {
            Log.d(TAG, "refreshInteractor :: 2");
            call.cancel();
            call = null;
        }
        if (executeMoviesTask !=null)
        {
            Log.d(TAG, "newCall :: 3");
            executeMoviesTask.cancel(true);
            executeMoviesTask = null;
        }
        movies_filter.clear();
        movies = null;
    }

    @Override
    public void fetchNowPlayingMovies(OnMoviesResponseListener responseListener, int required_page_no, String month_before, String current_date, int genre_code) {
        Log.d(TAG, "fetchNowPlayingMovies: 1 , required_page_no: "+required_page_no+" month be= "+month_before+" curent date = "+current_date+" sort = "+ genre_code);
        serverRequest(responseListener, required_page_no,month_before,current_date, genre_code);
    }



    private void serverRequest(final OnMoviesResponseListener responseListener, int required_page_no, String month_before, String current_date, int genre_code)
   {
       call = apiInterface.getNowPlayingMovies(required_page_no,month_before,current_date,genre_code==0?"":String.valueOf(genre_code));
       call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Log.d(TAG, "onResponse: : 1");

                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: : 2");

                    movies = response.body();
                    if (movies != null) {
                        Log.d(TAG, "onResponse: : 3");


                        checkTotalPages();
                        if (!call.isCanceled())
                        {
                            Log.d(TAG, "onResponse: : 3.1");
                            executeMoviesTask = new ExecuteNowPlayingMoviesTask(responseListener).execute(movies);
                        }
                    }
                    }
                    else
                    {
                        Log.d(TAG, "onResponse: : 4");
                        ApiError error = ErrorUtils.parseError(response);
                        responseListener.onFailed(error.getMessage());
                    }
                }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: : 1");
                if (!call.isCanceled())
                {
                    Log.d(TAG, "onFailure: : 2");
                    Log.d(TAG, "onFailure: : 2: issue = "+ t.getMessage()+" cause = "+t.getCause());
                    responseListener.onFailed(null);
                }
                else Log.d(TAG, "onFailure: : 3");
            }
        });
   }
    private void checkTotalPages() {
        Log.d(TAG, "checkTotalPages: 1");
        if (movies!=null)
        {
            Log.d(TAG, "checkTotalPages: 2");
            total_pages = Integer.parseInt(movies.getTotal_pages());
        }
        else
        {
            Log.d(TAG, "checkTotalPages: 3");
        }
    }

    private static ArrayList<MoviePosterAR> extractNeededMovieData(Movies movies) {
        Log.d(TAG, "extractNeededMovieData:: 1");

        int size = movies.getMoviesList().size();

        ArrayList<MoviePosterAR> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            if (executeMoviesTask ==null|| executeMoviesTask.isCancelled()) {
                Log.d(TAG, "extractNeededMovieData: 2");
                break;
            }

            String poster = movies.getMoviesList().get(i).getPoster_path();
            String id = movies.getMoviesList().get(i).getId();
            String vote_average = movies.getMoviesList().get(i).getVote_average();
            String title = movies.getMoviesList().get(i).getTitle();
            if(poster != null /*&& !vote_average.equals("0")*/  && id != null ){
                MoviePosterAR movie = new MoviePosterAR(poster, id, vote_average, title);
                if (!movies_filter.contains(Integer.parseInt(movie.getMovie_id())))
                {
                    arrayList.add(movie);
                    movies_filter.add(Integer.parseInt(movie.getMovie_id()));
                }
            }
        }
        Log.d(TAG, "extractNeededMovieData: 3");
        return arrayList;
    }

    private static class ExecuteNowPlayingMoviesTask extends AsyncTask<Movies, Void, ArrayList<MoviePosterAR>> {
        OnMoviesResponseListener responseListener;
        ExecuteNowPlayingMoviesTask(OnMoviesResponseListener responseListener)
        {
            this.responseListener = responseListener;
        }

        @Override
        protected ArrayList<MoviePosterAR> doInBackground(Movies... movies) {
            Log.d(TAG, "doInBackground: 1");

            return extractNeededMovieData(movies[0]); // do extraction
        }

        @Override
        protected void onPostExecute(ArrayList<MoviePosterAR> moviePosterAR) {
            Log.d(TAG, "onPostExecute: 1");
            if (!NowPlayingInteractorImp.call.isCanceled()) {
                Log.d(TAG, "onPostExecute: 2");
                responseListener.onSuccess(moviePosterAR, total_pages); // update UI
            } else {
                Log.d(TAG, "onPostExecute: 3");
            }
        }
    }
}
