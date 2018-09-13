package com.info.movies.fargments.upcoming;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.info.movies.constants.Common;
import com.info.movies.models.ApiError;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.models.posters.TopRatedMovie;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.ApiInterface;
import com.info.movies.rest.helpers.ErrorUtils;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by EhabSalah on 11/12/2017.
 */

public class UpComingInteractorImp implements UpComingInteractor {

    public static final String SAVED_MOVIES_FILTER = "movies_filter";
    private ApiInterface apiInterface;
    public static ArrayList<Integer> movies_filter;
        private static int  total_pages;
        private Movies movies;
        private static Call<Movies> call;


    public UpComingInteractorImp() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        movies_filter = new ArrayList<>();
    }

        @Override
        public void fetchUpcomingMovies(OnMoviesResponseListener responseListener, String min_date, String max_date, int required_page_no, int genre_code) {
            Log.d("TAG", "fetchUpcomingMovies: min_date: = "+min_date+" max_date: = "+max_date+" page = "+required_page_no +" genre code = "+genre_code);
            serverRequest(responseListener,min_date,max_date, required_page_no, genre_code);

    }

    private void serverRequest(final OnMoviesResponseListener responseListener, final String min_date, final String max_date, final int required_page_no, int genre_code) {
        call = apiInterface.getUpcoming(min_date,min_date,max_date,max_date,required_page_no,genre_code ==0? "" : String.valueOf(genre_code));
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull final Call<Movies> call, @NonNull Response<Movies> response) {
                Log.d("TAG", "onResponse: upcoming: 1");
                if(response.isSuccessful()){
                    Log.d("TAG", "onResponse: upcoming: 2");

                    movies = response.body();
                    if (movies!=null) {
                        Log.d("TAG", "onResponse: upcoming: 3");
                        checkTotalPages();
                        if ( !call.isCanceled()) {
                            Log.d("TAG", "onResponse: popular: 2.1");
                            AsyncTask executeUpcomingTask = new ExecuteUpcomingTask(responseListener).execute(movies);
                        }
                    } else Log.d("TAG", "onResponse: upcoming: 3.5: movies = null");

                }
                else
                {
                    Log.d("TAG", "onResponse: upcoming: 4");

                    try {
                        Log.d("TAG", "onResponse: upcoming: 5");

                        if(response.body().toString().contains("{\"results\":[],"))
                        {
                            Log.d("TAG", "onResponse: upcoming: 6");
                            Log.d("TAG", "onResponse: empty array "+response.body().toString());
                        }
                        else
                        {
                            Log.d("TAG", "onResponse: upcoming: 7");

                            ApiError error = ErrorUtils.parseError(response);
                            responseListener.onFailed(error.getMessage());
                        }
                    } catch (NullPointerException x )
                    {
                        Log.d("TAG", "onResponse: upcoming: 8");

                        x.printStackTrace();
                        Log.d("TAG", "onResponse: min_date = "+min_date);
                        Log.d("TAG", "onResponse: max_date = "+max_date);
                        Log.d("TAG", "onResponse: page_no = "+required_page_no);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                Log.d("TAG", "onFailure: upcoming: 1");
                if (!call.isCanceled())
                {
                    Log.d("TAG", "onFailure: upcoming: 2");
                    responseListener.onFailed(null);
                }
                else Log.d("TAG", "onFailure: upcoming: 3");
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


    private static ArrayList<TopRatedMovie> extractNeededMovieData(Movies topRatedMovieMovies)  {
        Log.d("TAG", "extractNeededMovieData:upcomingInteractorIMP: 1");
        int size = topRatedMovieMovies.getMoviesList().size();
        ArrayList<TopRatedMovie> arrayList = new ArrayList<>();
        String release_date = "";
        for (int i = 0; i < size; i++) {

            if (call == null||call.isCanceled()) {
                Log.d("TAG", "extractNeededMovieData: 2");
                break;
            }

            String poster = topRatedMovieMovies.getMoviesList().get(i).getPoster_path();
            String id = topRatedMovieMovies.getMoviesList().get(i).getId();
            String vote_average = topRatedMovieMovies.getMoviesList().get(i).getVote_average();
            String overView  = topRatedMovieMovies.getMoviesList().get(i).getOverview();
            if (overView.isEmpty()) {
                overView = "No reliable overview available now.";
            }
            String title  = topRatedMovieMovies.getMoviesList().get(i).getTitle();
            try {
                release_date = Common.mUpcomingDF.format(Common.sDF.parse(topRatedMovieMovies.getMoviesList().get(i).getRelease_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String geners = "";
            for (int genre_id : topRatedMovieMovies.getMoviesList().get(i).getGenre_ids()){
                for(int x = 0; x< Common.basic_geners.size(); x++){
                    if (genre_id == Common.basic_geners.get(x).getId())
                    {
                        if (geners.isEmpty()) {
                            geners=Common.basic_geners.get(x).getName();
                        }
                        else if (!geners.isEmpty())
                        {
                            geners+=" | "+Common.basic_geners.get(x).getName();
                        }
                    }
                }
            }

            if(poster != null && id != null /*&& overView !=null*/ && !release_date.isEmpty() && !geners.isEmpty()){
                TopRatedMovie movie = new TopRatedMovie(poster, id, vote_average, title, release_date, geners, overView);
                if (!movies_filter.contains(Integer.parseInt(movie.getMovie_id())))
                {
                    arrayList.add(movie);
                    movies_filter.add(Integer.parseInt(movie.getMovie_id()));
                }
            }
        }
        Log.d("TAG", "extractNeededMovieData :upcomingInteractorIMP: 3");
        return arrayList;
    }

    @Override
    public void refreshInteractor() {
            Log.d("TAG", "refreshInteractor :upcomingInteractorIMP: 1");
            if (call!=null ) {
                Log.d("TAG", "refreshInteractor :upcomingInteractorIMP: 2");
                call.cancel();
                call = null;
            }
            movies_filter.clear();
            movies = null;
    }
    @Override
    public void newCall() {
        Log.d("TAG", "newCall :upcomingInteractorIMP: 1");
        if (call!=null) {
            Log.d("TAG", "newCall :upcomingInteractorIMP: 2");
            call.cancel();
            call = null;
        }
    }
    private static class ExecuteUpcomingTask extends AsyncTask<Movies, Void, ArrayList<TopRatedMovie>> {
        OnMoviesResponseListener responseListener;

        public ExecuteUpcomingTask(OnMoviesResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        protected ArrayList<TopRatedMovie> doInBackground(Movies... topRatedMovieMovies) {
            Log.d("TAG", "doInBackground: upcoming: 1");
            return extractNeededMovieData(topRatedMovieMovies[0]); // do extraction
        }


        @Override
        protected void onPostExecute(ArrayList<TopRatedMovie> topRatedMovieMovies) {
            Log.d("TAG", "onPostExecute: upcoming: 1");
            if (UpComingInteractorImp.call!=null&&!UpComingInteractorImp.call.isCanceled())
            {
                Log.d("TAG", "onPostExecute: upcoming: 2");
                responseListener.onSuccess(topRatedMovieMovies, total_pages); // update UI
            } else {
                Log.d("TAG", "onPostExecute: upcoming: 3 call canceled");
            }
        }
    }
}