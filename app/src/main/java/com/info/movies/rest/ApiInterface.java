package com.info.movies.rest;

import com.info.movies.models.ApiError;
import com.info.movies.models.generslise_response.GenreMovieList;
import com.info.movies.models.movie_details.DetailsResponse;
import com.info.movies.models.movies_response.Movies;
import com.info.movies.models.sign_up.GuestSession;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by EhabSalah on 9/10/2017.
 */

public interface ApiInterface {

    @GET("3/genre/movie/list?&")
    Call<GenreMovieList> getGenersList();

    @GET("3/discover/movie?language=en-US&region=IN|US&sort_by=revenue.desc&include_adult=false&include_video=false&page=1&primary_release_date.gte=2018-3-1&primary_release_date.lte=2018-4-17&vote_count.gte=1&with_genres=28&with_runtime.gte=40&")
        //&region=US
    Call<Movies> getNowPlayingMovies(@Query("page") int required_page_no,
                                     @Query("primary_release_date.gte") String month_before,
                                     @Query("primary_release_date.lte") String current_date,
                                     @Query("with_genres") String genre_code);

    @GET("3/discover/movie?language=en-US&&region=IN|US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&vote_count.gte=250&with_genres=28&with_runtime.gte=40&")
    Call<Movies> getPopularMovies(
            @Query("page") int required_page_no,
            @Query("with_genres") String genre_code
    ); // response with PopularMovies.

    @GET("3/discover/movie?language=en-US&region=IN|US&sort_by=vote_average.desc&include_adult=false&include_video=false&page=1&vote_count.gte=75&without_genres=99,10755&with_runtime.gte=40&")
    Call<Movies> getTopRatedMovies(@Query("page") int required_page_no,
                                   @Query("primary_release_year") String release_year,
                                   @Query("with_genres") String with_genre,
                                   @Query("without_genres") String without_genres
    );

    @GET("3/discover/movie?language=en-US&region=US|IN&sort_by=primary_release_date.desc&include_adult=false&include_video=false&with_runtime.gte=40&")
    Call<Movies> getUpcoming(
            @Query("primary_release_date.gte") String min_date,
            @Query("release_date.gte") String min_dat,
            @Query("primary_release_date.lte") String max_date,
            @Query("release_date.lte") String max_dat,
            @Query("page") int required_page_no,
            @Query("with_genres") String genre
    );

    @GET("3/search/movie?language=en-US&query=final&page=1&include_adult=false&")
    Call<Movies> getQuery(@Query("query") String search_query, @Query("page") int page); // response with results.

    @GET("3/movie/{id}?append_to_response=videos,images,recommendations,credits&language=en-US&include_image_language=en,null&")
    Call<DetailsResponse> getMovieDetails(@Path("id") String movie_id); // response with movie details.


    @GET("3/discover/movie?language=en-US&region=US|IN&include_adult=false&include_video=false&page=1&with_runtime.gte=40&")
    Call<Movies> getDailyUpComming(
            @Query("primary_release_date.gte") String m,
            @Query("primary_release_date.lte") String mi,
            @Query("release_date.gte") String min,
            @Query("release_date.lte") String max
    );

    @GET("3/authentication/guest_session/new?")
    Call<GuestSession> requestGuestSession();

    @FormUrlEncoded
    @POST("3/movie/{id}/rating?guest_session_id=c8aa1fc4164f7bfedb7b2a538d4dce7b&")
    Call<ApiError> rateMovie(@Path("id") String movie_id,
                             @Query("guest_session_id") String session_id,
                             @Field("value") float rate);
}
