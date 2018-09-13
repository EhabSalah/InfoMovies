package com.info.movies.fargments.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.info.movies.activities.player.PlayerActivity;
import com.info.movies.constants.Common;
import com.info.movies.activities.player.PlayerConfig;
import com.info.movies.constants.Utils;
import com.info.movies.db.DBHelper;
import com.info.movies.models.movie_details.Genre;
import com.info.movies.models.movie_details.Image;
import com.info.movies.models.movie_details.Recommendations;
import com.info.movies.models.movie_details.Result;
import com.info.movies.models.movie_page.ListItemCastCrew;
import com.info.movies.models.movie_page.ListItemVideo;
import com.info.movies.models.posters.MoviePosterAR;
import com.info.movies.rest.ApiInterface;
import com.info.movies.rest.helpers.ErrorUtils;
import com.info.movies.models.ApiError;
import com.info.movies.models.movie_details.Cast;
import com.info.movies.models.movie_details.Credits;
import com.info.movies.models.movie_details.Crew;
import com.info.movies.models.movie_details.DetailsResponse;
import com.info.movies.models.movie_details.Images;
import com.info.movies.models.movie_details.ProductionCompanie;
import com.info.movies.models.movie_details.ProductionCountrie;
import com.info.movies.models.movie_details.Videos;
import com.info.movies.models.movie_page.MoviePage;
import com.info.movies.rest.ApiClient;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.info.movies.constants.Common.GSID;
import static com.info.movies.constants.Common.coolFormat;
import static com.info.movies.constants.Common.mUpcomingDF;
import static com.info.movies.constants.Common.sDF;

/**
 * Created by EhabSalah on 1/13/2018.
 */

class MoviesInteractorIMP implements MoviesInteractor {
    private static final String TAG = MoviesInteractorIMP.class.getSimpleName();
    private ApiInterface apiInterface;
    private static Call<DetailsResponse> call;

    private static MovieDataExtraction movieDataExtraction;

    public MoviesInteractorIMP() {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
    }


    @Override
    public void fetchDetails(final OnDetailsResponseListener onDetailsResponseListener, String id) {
        Log.d(TAG, "fetchDetails: 1 ");
        call = apiInterface.getMovieDetails(id);
        call.enqueue(new Callback<DetailsResponse>() {
            @Override
            public void onResponse(Call<DetailsResponse> call, @NonNull Response<DetailsResponse> response) {
                Log.d(TAG, "fetchDetails : onResponse: 1 ");
                if (response.isSuccessful()) {
                    Log.d(TAG, "fetchDetails : onResponse: 2 ");
                    // hna hnt3aml m3 el response b2aa
                    if (!call.isCanceled()) {
                        Log.d(TAG, "fetchDetails : onResponse: 3 ");
                        movieDataExtraction = new MovieDataExtraction(onDetailsResponseListener);
                        movieDataExtraction.execute(response.body());
                    } else Log.d(TAG, "fetchDetails : onResponse: 4 ");
                } else {
                    Log.d(TAG, "fetchDetails : onResponse not successful: 2 ");
                    ApiError error = ErrorUtils.parseError(response);
                    onDetailsResponseListener.onFailed(error.getMessage());
                    Log.d(TAG, "fetchDetails : onResponse not successful: 2 error.getMessage() = " + error.getMessage());
                }

            }

            @Override
            public void onFailure(Call<DetailsResponse> call, Throwable t) {
                Log.d(TAG, "fetchDetails: onFailure: 1 ");
                if (!call.isCanceled()) {
                    Log.d(TAG, "fetchDetails: onFailure: 2 ");
                    Log.d(TAG, "fetchDetails: onFailure: 2  message = " + t.getMessage());
                    onDetailsResponseListener.onFailed(null);
                } else {
                    Log.d(TAG, "fetchDetails: onFailure: 3 ");
                }
            }
        });


    }

    @Override
    public void refreshInteractor() {
        Log.d(TAG, "refreshInteractor 1 ");
        if (call != null) {
            Log.d(TAG, "refreshInteractor 2 ");
            call.cancel();
            call = null;
        }
        if (movieDataExtraction != null) {
            Log.d(TAG, "refreshInteractor 3 ");
            movieDataExtraction.cancel(true);
            movieDataExtraction = null;
        }
    }

    @Override
    public void checkToPlayVideo(OnPlayTrailerListener onPlayTrailerListener, Activity activity, String key) {
        if (Utils.isNetworkAvailable(activity)) {
            try {
                if (Common.isAppEnabled(PlayerConfig.YOUTUBE_PACKAGE_NAME, activity)) {
                    // app exist and enabled.
                    Log.d(TAG, "checkToPlayVideo: 1");

                    final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(activity);
                    if (result == YouTubeInitializationResult.SUCCESS) {
                        Log.d(TAG, "checkToPlayVideo: 2");
                        activity.startActivity(new Intent(activity, PlayerActivity.class).putExtra("trailer_key", key));
                    }
                } else {
                    // app exist and disabled.
                    Log.d(TAG, "checkToPlayVideo: 3");
                    onPlayTrailerListener.onDisabled();

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                // app is not installed.
                Log.d(TAG, "checkToPlayVideo: 4");
                String url = "http://www.youtube.com/watch?v=" + key;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                activity.startActivity(i);
            }
        } else {
            onPlayTrailerListener.onNoInternet();
        }
    }


    @Override
    public void rateMovie(final OnMovieRateListener listener, final Context context, final String id, final float rateValue) {
        Log.d(TAG, "rateMovie: ");
        Log.d(TAG, "rateMovie: GSID = " + GSID);
        Log.d(TAG, "rateMovie: id = " + id);
        Log.d(TAG, "rateMovie: rateValue = " + rateValue);
        float rat = Float.valueOf(String.format(Locale.ENGLISH, "%.1f", rateValue));
        Call<ApiError> rateCall = apiInterface.rateMovie(id, Common.GSID, rat);
        rateCall.enqueue(new Callback<ApiError>() {
            @Override
            public void onResponse(Call<ApiError> call, Response<ApiError> response) {
                Log.d(TAG, "onResponse: rateMovie 1");
                Log.d(TAG, "onResponse: response = " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: rateMovie 2");
                    ApiError r = response.body();
                    if (r.getStatusCode() == 1 && r.getMessage().contains("Success.")) {
                        Log.d(TAG, "onResponse: rateMovie 3");

                        DBHelper mDBHelper = new DBHelper(context); // db declaration
                        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
                        long insert = mDBHelper.insertRatedMovie(sqLiteDatabase, id, rateValue);
                        sqLiteDatabase.close();

                        if (insert == -1) {
                            Log.d(TAG, "onResponse: rateMovie: RATE NOT INSERTED ");
                        } else {
                            Log.d(TAG, "onResponse: rateMovie: rate inserted to db");
                        }
                        listener.onRateSuccess();

                    } else if (r.getStatusCode() == 12 && r.getMessage().toLowerCase().contains("success")) {
                        Log.d(TAG, "onResponse: rateMovie 4");
                        if (Common.MovieRate(id, context) == null) {
                            Log.d(TAG, "onResponse: rateMovie 4.5");

                            DBHelper mDBHelper = new DBHelper(context); // db declaration
                            SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
                            long insert = mDBHelper.insertRatedMovie(sqLiteDatabase, id, rateValue);
                            sqLiteDatabase.close();

                            if (insert == -1) {
                                Log.d(TAG, "onResponse: rateMovie 5 : RATE NOT INSERTED ");
                            } else {
                                Log.d(TAG, "onResponse: rateMovie 6 : rate inserted to db");
                            }
                        }
                        listener.onRateSuccess();
                    } else {
                        Log.d(TAG, "onResponse: rateMovie  7");
                        listener.onRateFailed(r.getMessage());
                    }
                } else {
                    listener.onRateFailed("Unknown error");
                    Log.d(TAG, "onResponse: rateMovie 8");
                }
            }

            @Override
            public void onFailure(Call<ApiError> call, Throwable t) {
                Log.d(TAG, "onFailure: rateMovie 1");
                if (!call.isCanceled()) {
                    Log.d(TAG, "onFailure: rateMovie 2");
                    listener.onRateFailed("Check internet connection and try again!");
                }
            }
        });
    }

    static class MovieDataExtraction extends AsyncTask<DetailsResponse, Void, MoviePage> {
        OnDetailsResponseListener listener;

        public MovieDataExtraction(OnDetailsResponseListener listener) {
            this.listener = listener;
        }

        @Override
        protected MoviePage doInBackground(DetailsResponse... detailsResponses) {
            return extractNeededData(detailsResponses[0]);
        }

        @Override
        protected void onPostExecute(MoviePage moviePage) {
            // check if call is not canceled , then update ui.
            if (!isCancelled() && moviePage != null) {
                Log.d(TAG, "onPostExecute: 1 ");
                listener.onSuccess(moviePage);
            } else {
                Log.d(TAG, "onPostExecute: 2 ");
            }
        }
    }

    private static MoviePage extractNeededData(DetailsResponse detailsRespons) {
        MoviePage mMoviePage = new MoviePage(); // hmla b2aa el object da
        Log.d(TAG, "extractNeededData: IMAGES " + new Gson().toJson(detailsRespons.getImages()));
        String trailer_key = extractTrailerKey(detailsRespons.getVideos());// extract official trailer -**************************************************-

        String movie_id = detailsRespons.getId();
        String original_title = detailsRespons.getOriginal_title();                                                  // 1
        String title = detailsRespons.getTitle();                                                  // 1

        String poster_path = detailsRespons.getPoster_path();                                               // 1.5

        String genres = extractGenres(detailsRespons.getGenre());                                           // 2

        String release_date = extractReleaseDate(detailsRespons.getRelease_date());                         // 3

        String runtime = detailsRespons.getRuntime();                                                       // 4

        String vote_average = String.format(Locale.ENGLISH, "%.1f", Float.valueOf(detailsRespons.getVote_average()));                                             // 5

        String vote_count = detailsRespons.getVote_count();                                                 // 6

        String languages = detailsRespons.getOriginal_language();                                           // 7

        String overView = detailsRespons.getOverview();                                                     // 8

        List<ListItemVideo> videos = extractVideos(detailsRespons.getVideos(), trailer_key);                             // 9

        List<String> images = extractImages(detailsRespons.getImages());                                    // 10

        String director_name = extractDirectorName(detailsRespons.getCredits());                            // 11

        List<ListItemCastCrew> cast = extractCast(detailsRespons.getCredits());                                 // 12


        String home_page = detailsRespons.getHomepage();                                                     // 14
        String imdp_id = detailsRespons.getImdb_id();                                                       // 15
        // String revenue = NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(detailsRespons.getRevenue()));                                                       // 16
        String revenue=null;                                                      // 16
        if (!TextUtils.isEmpty(detailsRespons.getRevenue())) {
            revenue = coolFormat(Double.valueOf(detailsRespons.getRevenue()), 0);
        }

        String belongs_to = detailsRespons.getBelongs_to_collection() != null ? detailsRespons.getBelongs_to_collection().getName() : null;                                                       // 16
        List<String> production_countries = detailsRespons.getProduction_countries() != null && !detailsRespons.getProduction_countries().isEmpty() ? getProductionStates(detailsRespons.getProduction_countries()) : null;
        List<String> production_companies = detailsRespons.getProduction_companies() != null && !detailsRespons.getProduction_companies().isEmpty() ? getProducionCompanies(detailsRespons.getProduction_companies()) : null;
        String tagline = detailsRespons.getTagline();
        ArrayList<MoviePosterAR> recommendations = extractRecommendations(detailsRespons.getRecommendations());

        mMoviePage.setMovie_id(movie_id);
        mMoviePage.setTrailer_key(trailer_key);
        mMoviePage.setTitle(title);
        mMoviePage.setOriginal_title(original_title);
        mMoviePage.setPoster_path(poster_path);
        mMoviePage.setGenres(genres);
        mMoviePage.setRelease_date(release_date);
        mMoviePage.setRuntime(runtime);
        mMoviePage.setVote_average(vote_average);
        mMoviePage.setVote_count(vote_count);
        mMoviePage.setLanguages(languages);
        mMoviePage.setOverview(overView);
        mMoviePage.setVideos(videos);
        mMoviePage.setImages(images);
        mMoviePage.setCast_crew(cast);
        mMoviePage.setDirector_name(director_name);
        mMoviePage.setHome_page(home_page);
        mMoviePage.setImdp_id(imdp_id);
        mMoviePage.setRevenue(revenue);
        mMoviePage.setBelongs_to(belongs_to);
        mMoviePage.setCompanies(production_companies);
        mMoviePage.setCountries(production_countries);
        mMoviePage.setTagline(tagline);
        mMoviePage.setRecommendations(recommendations);

        if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
            Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
            return null;
        }
        Log.d("TAG", "extractNeededData: FINISHED");
        return mMoviePage;
    }

    private static ArrayList<MoviePosterAR> extractRecommendations(Recommendations movies) { // for extract recommendations
        Log.d(TAG, "extractRecommendations: 1");
        ArrayList<Integer> movies_filter = new ArrayList<>();

        int size = movies.getResults().size();

        ArrayList<MoviePosterAR> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            if (movieDataExtraction == null || movieDataExtraction.isCancelled()) {
                Log.d(TAG, "extractRecommendations: 2");
                break;
            }

            String poster = movies.getResults().get(i).getPoster_path();
            String id = movies.getResults().get(i).getId() + "";
            String vote_average = movies.getResults().get(i).getVote_average() + "";
            String title = movies.getResults().get(i).getTitle();
            if (poster != null) {
                MoviePosterAR movie = new MoviePosterAR(poster, id, vote_average, title);
                if (!movies_filter.contains(Integer.parseInt(movie.getMovie_id()))) {
                    arrayList.add(movie);
                    movies_filter.add(Integer.parseInt(movie.getMovie_id()));
                }
            }
        }
        Log.d(TAG, "extractRecommendations: 3");
        return arrayList;
    }

    private static List<String> getProducionCompanies(List<ProductionCompanie> production_companies) {
        ArrayList<String> companies = new ArrayList<>();
        for (int i = 0; i < production_companies.size(); i++) {
            if (production_companies.get(i).getName() != null && !production_companies.get(i).getName().isEmpty()) {
                companies.add(production_companies.get(i).getName());
            }
        }
        return companies;
    }

    private static List<String> getProductionStates(List<ProductionCountrie> production_countries) {
        ArrayList<String> companies = new ArrayList<>();
        for (int i = 0; i < production_countries.size(); i++) {
            if (production_countries.get(i).getName() != null && !production_countries.get(i).getName().isEmpty()) {
                companies.add(production_countries.get(i).getName());
            }
        }
        return companies;
    }


    private static List<ListItemCastCrew> extractCast(Credits credits) {
        ArrayList<ListItemCastCrew> cast_crew = new ArrayList<>();
        List<Cast> c = credits.getCast();
        List<Crew> cr = credits.getCrew();
        if (c != null || cr != null) {
            if (c != null && !c.isEmpty()) {
                for (int i = 0; i < c.size(); i++) {
                    if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                        Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                        break;
                    }
                    if (c.get(i).getProfile_path() != null && !c.get(i).getProfile_path().isEmpty()) {
                        String name = c.get(i).getName();
                        String character = c.get(i).getCharacter();
                        String image = c.get(i).getProfile_path();
                        cast_crew.add(new ListItemCastCrew(name, image, character));
                    }
                }
            }
            if (cr != null && !cr.isEmpty()) {

                for (int i = 0; i < cr.size(); i++) {

                    if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                        Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                        break;
                    }
                    if (cr.get(i).getProfile_path() != null && !cr.get(i).getProfile_path().isEmpty()) {
                        String name = cr.get(i).getName();
                        String job_title = cr.get(i).getJob();
                        String image = cr.get(i).getProfile_path();
                        cast_crew.add(new ListItemCastCrew(name, image, job_title));
                    }
                }
            }
            return cast_crew;
        }
        return null;
    }

    private static String extractDirectorName(Credits credits) {
        List<Crew> crew = credits.getCrew();
        String director_name;
        for (int i = 0; i < crew.size(); i++) {
            if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                break;
            }

            if (crew.get(i).getJob().trim().toLowerCase().equals("writer")) {
                director_name = crew.get(i).getName();
                return director_name;
            }
        }
        return null;
    }

    private static String extractTrailerKey(Videos videos) {
        String key;
        List<Result> video_results = videos.getResults();
        if (!video_results.isEmpty()) {
            for (int i = 0; i < video_results.size(); i++) {

                if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                    Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                    break;
                }

                if (video_results.get(i).getType().toLowerCase().contains("trailer")) {
                    if (video_results.get(i).getSite().trim().toLowerCase().contains("youtube")) {
                        if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("official")) {
                            key = video_results.get(i).getKey();
                            return key;
                        } else if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("trailer")) {
                            key = video_results.get(i).getKey();
                            return key;
                        }
                    }
                }
            }
            for (int i = 0; i < video_results.size(); i++) {

                if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                    Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                    break;
                }

                if (video_results.get(i).getType().toLowerCase().contains("teaser")) {
                    if (video_results.get(i).getSite().toLowerCase().trim().toLowerCase().contains("youtube")) {
                        if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("official")) {
                            key = video_results.get(i).getKey();
                            return key;
                        } else if (video_results.get(i).getName() != null && video_results.get(i).getName().toLowerCase().contains("teaser")) {
                            key = video_results.get(i).getKey();
                            return key;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static ArrayList<String> extractImages(Images images) {
        List<Image> movie_images = images.getImages();
        //List<Image> movie_posters = images.getPosters();
        ArrayList<String> images_list = new ArrayList<>();
        if (movie_images != null && !movie_images.isEmpty()) {
            for (int i = 0; i < movie_images.size(); i++) {

                if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                    Log.d(TAG, "extractTrailerKey: KILL ASYNC TASK 3");
                    break;
                }
                images_list.add(movie_images.get(i).getFile_path());
            }
        }
        if (!images_list.isEmpty()) {
            return images_list;
        }
        return null;
    }

    private static String extractReleaseDate(String date) {
        if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
            Log.d(TAG, "extractReleaseDate: KILL ASYNC TASK ");
        }
        String release_date;
        try {
            release_date = mUpcomingDF.format(sDF.parse(date));
        } catch (ParseException e) {
            release_date = ""; // 2
        }
        return release_date;
    }

    private static ArrayList<ListItemVideo> extractVideos(Videos videos, String trailer_key) {
        ArrayList<ListItemVideo> extracted_videos = new ArrayList<>();
        List<Result> video_results = videos.getResults(); // check site & person_profile_placeholder & key
        String key;
        String video_title;

        if (video_results != null && !video_results.isEmpty()) {
            for (int i = 0; i < video_results.size(); i++) {
                if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                    Log.d(TAG, "extractVideos: KILL ASYNC TASK ");
                    break;
                }
                if (video_results.get(i).getType() != null && video_results.get(i).getSite().trim().equalsIgnoreCase("youtube")) {
                    if (!video_results.get(i).getKey().equals(trailer_key)) {
                        key = video_results.get(i).getKey();
                        video_title = video_results.get(i).getName();
                        ListItemVideo listItemVideo = new ListItemVideo(video_title, key);
                        extracted_videos.add(listItemVideo);
                    }

                }
            }
            return extracted_videos;
        }
        return null;

    }

    private static String extractGenres(List<Genre> genre) {
        String g = "";
        StringBuilder genres = new StringBuilder();
        if (genre != null && !genre.isEmpty()) {
            for (int i = 0; i < genre.size(); i++) {
                if (movieDataExtraction == null || movieDataExtraction.isCancelled()) { // Check that user not canceled the call, if canceled call we will kill this task, if it is running
                    Log.d(TAG, "extractGenres: KILL ASYNC TASK ");
                    break;
                }

                if (genres.length() == 0) {
                    genres = new StringBuilder(genre.get(i).getName());
                } else if (genres.length() > 0) {
                    genres.append(" | ").append(genre.get(i).getName());
                }
            }
        }
        return genres.toString();
    }
}
