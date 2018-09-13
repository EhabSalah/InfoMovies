package com.info.movies.fargments.toprated;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.info.movies.rest.ApiInterface;
import com.info.movies.constants.Common;
import com.info.movies.models.ApiError;
import com.info.movies.models.posters.TopRatedMovie;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.helpers.ErrorUtils;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class TopRatedInteractorImp implements TopRatedInteractor {
    public static final String SAVED_MOVIES_FILTER = "movies_filter";
    private ApiInterface apiInterface;
    public static ArrayList<Integer> movies_filter;
    private static int total_pages;
    private static String genre;
    private static String year;

    private Movies movies;
    private static Call<Movies> call;
    private static AsyncTask executeTopRatedTask;

    public TopRatedInteractorImp() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        movies_filter = new ArrayList<>();
    }


    @Override
    public void newCall() {
        Log.d("TAG", "newCall :topRated:InteractorIMP: 1");
        if (call != null) {
            Log.d("TAG", "newCall :topRatedInteractorIMP: 2");
            call.cancel();
            call = null;
        }
    }

    @Override
    public void refreshInteractor() {
        Log.d("TAG", "refreshInteractor :topRatedInteractorIMP: 1");
        if (call != null) {
            Log.d("TAG", "refreshInteractor :topRatedInteractorIMP: 2");
            call.cancel();
            call = null;
        }
        movies_filter.clear();
        movies = null;
    }

    public void fetchTopRatedMovies(OnMoviesResponseListener responseListener, int required_page_no, String year, int genre) {

        serverRequest(responseListener, required_page_no, year, genre);
    }


    private void serverRequest(final OnMoviesResponseListener responseListener, int required_page_no, final String year, final int genre) {
        Log.d("TAG", "fetchTopRatedMovies: 1 , required_page_no: " + required_page_no + "year = " + year + " genre = " + genre);
        Log.d("TAG", "fetchTopRatedMovies: 2 , movies filter size = : " + movies_filter.size());
        call = apiInterface.getTopRatedMovies(required_page_no, year, genre == 0 ? "" : String.valueOf(genre),genre == 0 ? "99,10755" : "");


        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Log.d("TAG", "onResponse: top rated: 1");

                if (response.isSuccessful()) {
                    Log.d("TAG", "onResponse: top rated: 2");

                    movies = response.body();
                    if (movies != null) {
                        Log.d("TAG", "onResponse: top rated: 2.1");

                        checkTotalPages();
                        checkGenreAndYear(genre, year);

                        if (TopRatedInteractorImp.call != null && !TopRatedInteractorImp.call.isCanceled()) {
                            Log.d("TAG", "onResponse: top rated: 2.2" + "TopRatedInteractorImp.genre = " + TopRatedInteractorImp.genre + " TopRatedInteractorImp.year " + TopRatedInteractorImp.year);

                            executeTopRatedTask = new ExecuteTopRatedTask(responseListener).execute(movies);
                        }
                    }

                } else {
                    Log.d("TAG", "onResponse: top rated: 3");

                    ApiError error = ErrorUtils.parseError(response);
                    responseListener.onFailed(error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                Log.d("TAG", "onFailure: top rated: 1");
                if (!call.isCanceled()) {
                    Log.d("TAG", "onFailure: top rated: 2");
                    responseListener.onFailed(null);
                } else Log.d("TAG", "onFailure: top rated: 3");
            }
        });

    }

    private void checkGenreAndYear(int genre, String year) {
        Log.d("TAG", "checkGenreAndYear: 1 ");
        if (genre == 0) {
            Log.d("TAG", "checkGenreAndYear: 2 ");
            TopRatedInteractorImp.genre = "None";
        } else {
            Log.d("TAG", "checkGenreAndYear: 3 ");
            TopRatedInteractorImp.genre = Common.getGenreString(genre);
        }
        if (year == null) {
            Log.d("TAG", "checkGenreAndYear: 4 ");
            TopRatedInteractorImp.year = "None";
        } else {
            Log.d("TAG", "checkGenreAndYear: 5 ");
            TopRatedInteractorImp.year = year;
        }
    }

    private void checkTotalPages() {
        Log.d("TAG", "checkTotalPages: 1");
        total_pages = Integer.parseInt(movies.getTotal_pages());
    }

    private static ArrayList<TopRatedMovie> extractNeededMovieData(Movies topRatedMovieMovies) {
        Log.d("TAG", "extractNeededMovieData: 1");

        int size = topRatedMovieMovies.getMoviesList().size();
        ArrayList<TopRatedMovie> arrayList = new ArrayList<>();
        String release_date = "";
        for (int i = 0; i < size; i++) {

            if (executeTopRatedTask == null || executeTopRatedTask.isCancelled()) {
                Log.d("TAG", "extractNeededMovieData: 2");
                break;
            }

            String poster = topRatedMovieMovies.getMoviesList().get(i).getPoster_path();
            String id = topRatedMovieMovies.getMoviesList().get(i).getId();
            String vote_average = topRatedMovieMovies.getMoviesList().get(i).getVote_average();
            String overView = topRatedMovieMovies.getMoviesList().get(i).getOverview();
            String title = topRatedMovieMovies.getMoviesList().get(i).getTitle();
            try {
                release_date = Common.mTopRatedDF.format(Common.sDF.parse(topRatedMovieMovies.getMoviesList().get(i).getRelease_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String geners = "";
            for (int genre_id : topRatedMovieMovies.getMoviesList().get(i).getGenre_ids()) {
                for (int x = 0; x < Common.basic_geners.size(); x++) {
                    if (genre_id == Common.basic_geners.get(x).getId()) {
                        if (geners.isEmpty()) {
                            geners = Common.basic_geners.get(x).getName();
                        } else if (!geners.isEmpty()) {
                            geners += " | " + Common.basic_geners.get(x).getName();
                        }
                    }
                }
            }

            if (poster != null && !vote_average.equals("0") && id != null && overView != null && !release_date.isEmpty() && !geners.isEmpty()) {
                TopRatedMovie movie = new TopRatedMovie(poster, id, vote_average, title, release_date, geners, overView);
                if (!movies_filter.contains(Integer.parseInt(movie.getMovie_id()))) {
                    arrayList.add(movie);
                    movies_filter.add(Integer.parseInt(movie.getMovie_id()));
                }
            }
        }
        Log.d("TAG", "extractNeededMovieData:topRatedInteractorIMP: 3");
        return arrayList;
    }

    private static class ExecuteTopRatedTask extends AsyncTask<Movies, Void, ArrayList<TopRatedMovie>> {
        OnMoviesResponseListener responseListener;

        public ExecuteTopRatedTask(OnMoviesResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        protected ArrayList<TopRatedMovie> doInBackground(Movies... topRatedMovieMovies) {
            Log.d("TAG", "onResponse: top rated: 1");
            return extractNeededMovieData(topRatedMovieMovies[0]); // do extraction
        }

        @Override
        protected void onPostExecute(ArrayList<TopRatedMovie> topRatedMovieMovies) {
            Log.d("TAG", "onResponse: top rated: 1");
            if (TopRatedInteractorImp.call != null && !TopRatedInteractorImp.call.isCanceled()) {
                Log.d("TAG", "onPostExecute: upcoming: 2");
                responseListener.onSuccess(topRatedMovieMovies, total_pages, TopRatedInteractorImp.year, TopRatedInteractorImp.genre); // update UI
            } else {
                Log.d("TAG", "onPostExecute: upcoming: 3 call canceled");
            }
        }
    }

}
