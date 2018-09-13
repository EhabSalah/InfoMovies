package com.info.movies.fargments.movie;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.info.movies.R;
import com.info.movies.constants.Utils;
import com.info.movies.models.movie_page.ListItemCastCrew;
import com.info.movies.models.movie_page.ListItemVideo;
import com.info.movies.models.movie_page.MoviePage;
import com.info.movies.models.posters.MoviePosterAR;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

class MoviePresenterIMP implements MoviePresenter, MoviesInteractor.OnDetailsResponseListener, MoviesInteractor.OnPlayTrailerListener, MoviesInteractor.OnMovieRateListener {
    MovieView mMoviewiew;
    MoviesInteractor mMoviesInteractor;
    Context context;
    private static final String TAG = MoviePresenterIMP.class.getSimpleName();

    public MoviePresenterIMP(MovieView mMovieView, Context context) {
        this.mMoviewiew = mMovieView;
        this.mMoviesInteractor = new MoviesInteractorIMP();
        this.context = context;
    }

    @Override
    public void onRecreateView(MoviePage moviePage) {
        Log.d(TAG, "onRecreateView: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onRecreateView: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onRecreateView: 3 ");
                mMoviewiew.showProgress();
                onSuccess(moviePage);
            }
        }

    }

    @Override
    public void onPlayTrailerClick(Activity activity, String key) {
        Log.d(TAG, "onPlayTrailerClick: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onPlayTrailerClick: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onPlayTrailerClick: 3 ");
                mMoviesInteractor.checkToPlayVideo(this, activity, key);
            }
        }
    }


    @Override
    public void onCreateView(String id) {
        Log.d(TAG, "onCreateView: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onCreateView: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onCreateView: 3 ");

                mMoviewiew.showProgress();
                if (Utils.isNetworkAvailable(context)) {
                    Log.d(TAG, "onCreateView: 4 ");

                    mMoviesInteractor.refreshInteractor();
                    mMoviesInteractor.fetchDetails(this, id);
                } else {
                    Log.d(TAG, "onCreateView: 5 ");
                    onFailed(null);
                }

            }
        }
    }

    @Override
    public void onDestroyView() {
        if (mMoviewiew != null) {
            Log.d(TAG, "onDestroyView:  1 ");
            mMoviesInteractor.refreshInteractor();
            mMoviewiew.hideProgress();
        }
    }

    @Override
    public void onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState:  1 ");
        mMoviesInteractor.refreshInteractor();
    }


    /************************************************************************************************/
    @Override
    public void onFailed(String message) {
        Log.d(TAG, "onFailed: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onFailed: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onFailed: 3 ");
                mMoviewiew.hideProgress();

                if (message == null) {
                    mMoviewiew.showSnackAlert("No Internet Connection !");
                }

            }
        }

    }

    @Override
    public void onSuccess(MoviePage moviePage) {
        Log.d(TAG, "onSuccess: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onSuccess: 2 ");
            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onSuccess: 3 ");

                updateUI(mMoviewiew, moviePage);
                mMoviewiew.receiveSourceOfDate(moviePage);
            }
        }
    }


    private void updateUI(MovieView mMoviewiew, MoviePage moviePage) {
        Log.d(TAG, "updateUI:  ");
        mMoviewiew.hideProgress();
        mMoviewiew.showDetailesLayout();
        Log.d(TAG, "updateUI: moviePage = " + new Gson().toJson(moviePage));
        updateTrailerLayout(mMoviewiew, moviePage.getTrailer_key());
        updateMainInfoLayout(mMoviewiew, moviePage.getOriginal_title(), moviePage.getPoster_path(), moviePage.getGenres(), moviePage.getRelease_date(), moviePage.getRuntime(), moviePage.getVote_average(), moviePage.getVote_count(), moviePage.getLanguages(), moviePage.getRevenue());
        updateOverViewLayout(mMoviewiew, moviePage.getOverview());
        updateVideosLayout(mMoviewiew, moviePage.getVideos());
        updateImagesLayout(mMoviewiew, moviePage.getImages());
        updateCastCrewWriterLayout(mMoviewiew, moviePage.getCast_crew(), moviePage.getDirector_name());
        updateExternalsMeadiaLayout(mMoviewiew, moviePage.getImdp_id(), moviePage.getHome_page());
        updateBelongsToLayout(mMoviewiew, moviePage.getBelongs_to());
        updateProductionLAyout(mMoviewiew, moviePage.getCompanies(), moviePage.getCountries());
        updateTaglineLayout(mMoviewiew, moviePage.getTagline());
        updateRecommendationsLayout(mMoviewiew, moviePage.getRecommendations());
    }

    private void updateRecommendationsLayout(MovieView mMoviewiew, List<MoviePosterAR> recommendations) {
        if (recommendations!=null&&!recommendations.isEmpty()) {
            Log.d(TAG, "updateRecommendationsLayout: 1");
            Log.d(TAG, "updateRecommendationsLayout: recommendations = "+recommendations);
            mMoviewiew.showRecommendationsLayout();
            mMoviewiew.receiveRecommendations(recommendations);
        }
        else
        {
            Log.d(TAG, "updateRecommendationsLayout: 2");
            mMoviewiew.hideRecommendationsLayout();
        }
    }

    private void updateTaglineLayout(MovieView mMoviewiew, String tagline) {
        if (tagline != null && !tagline.isEmpty()) {
            mMoviewiew.showTaglineLayout();
            mMoviewiew.receiveTagline(tagline);
        } else mMoviewiew.hideTaglineLayout();
    }

    private void updateProductionLAyout(MovieView mMoviewiew, List<String> companies, List<String> countries) {
        if (companies == null/*||companies.isEmpty()*/ && countries == null/*||countries.isEmpty()*/) {
            mMoviewiew.hideProductionLayout();
        } else {
            mMoviewiew.showProductionLayout();
            mMoviewiew.receiveProduction(companies, countries);
        }
    }

    private void updateBelongsToLayout(MovieView mMoviewiew, String belongs_to) {
        if (belongs_to != null && !belongs_to.isEmpty()) {
            mMoviewiew.showBelongsToLayout();
            mMoviewiew.recieveBelongsTo(belongs_to);
        } else mMoviewiew.hideBelongsToLayout();
    }

    private void updateExternalsMeadiaLayout(MovieView mMoviewiew, String imdp_id, String home_page) {
        String site;
        String imdp;
        if (imdp_id == null || imdp_id.isEmpty()) {
            imdp = null;
        } else {
            imdp = imdp_id;
        }

        if (home_page == null || home_page.isEmpty()) {
            site = null;
        } else {
            site = home_page;
        }

        if (site == null && imdp == null) {
            mMoviewiew.hideExternalMediaLayout();
        } else {
            mMoviewiew.showExternalMediaLayout();
            mMoviewiew.recieveExternalMedia(site, imdp);
        }
    }

    private void updateCastCrewWriterLayout(MovieView mMoviewiew, List<ListItemCastCrew> cast_crew, String director_name) {
        Log.d(TAG, "updateCastCrewWriterLayout: director_name = "+director_name);
        List<ListItemCastCrew> ca_cr;
        String d_name;
        if (cast_crew != null && !cast_crew.isEmpty() && cast_crew.size() != 0) {
            ca_cr = cast_crew;
            mMoviewiew.showCastLayout();
        } else {
            ca_cr = null;
            mMoviewiew.hideCastLayout();
        }


        if (director_name != null && !director_name.isEmpty()) {
            d_name = director_name;
            mMoviewiew.showDirectorLayout();
        } else {
            d_name = null;
            mMoviewiew.hideDirectorLayout();
        }

        if (ca_cr == null && d_name == null) {
            mMoviewiew.hideCastCrewDirectorLayout();
        } else {
            mMoviewiew.showCastCrewDirectorLayout();
            mMoviewiew.receiveCrewCastDirector(ca_cr, d_name);
        }

    }


    private void updateImagesLayout(MovieView mMoviewiew, List<String> images) {
        if (images != null && images.size() != 0 && !images.isEmpty()) {
            mMoviewiew.showImageLayout();
            mMoviewiew.recieveImages(images);
        } else {
            mMoviewiew.hideImageLayout();
        }
    }

    private void updateVideosLayout(MovieView mMoviewiew, List<ListItemVideo> videos) {
        Log.d(TAG, "updateVideosLayout:  ");

        if (videos != null && videos.size() != 0 && !videos.isEmpty()) {
            Log.d(TAG, "updateVideosLayout: videos = " + videos.size());
            mMoviewiew.showVideoLayout();
            mMoviewiew.recieveVideos(videos);
        } else mMoviewiew.hideVideoLayout();

    }

    private void updateOverViewLayout(MovieView mMoviewiew, String overview) {
        Log.d(TAG, "updateOverViewLayout: ");

        String c_overview;
        if (overview != null && !overview.isEmpty()) {
            c_overview = overview;
        } else c_overview = "There is no reliable overview yet.";
        mMoviewiew.showOverviewLayout();
        mMoviewiew.recieveOverview(c_overview);
    }

    private void updateMainInfoLayout(MovieView mMoviewiew, String title, String poster_path, String genres, String release_date, String runtime, String vote_average, String vote_count, String languages, String revenue) {
        Log.d(TAG, "updateMainInfoLayout: ");

        String c_title;
        String c_poster_path;
        String c_genres;
        String c_release_date;
        String c_runtime;
        String c_vote_average;
        String c_vote_count;
        String c_languages;
        String c_revenue;

        if (title != null && !title.isEmpty()) {
            c_title = title;
        } else c_title = null;

        if (poster_path != null && !poster_path.isEmpty()) {
            c_poster_path = poster_path;
        } else c_poster_path = null;

        if (genres != null && !genres.isEmpty()) {
            c_genres = genres;
        } else c_genres = null;

        if (release_date != null && !release_date.isEmpty()) {

            c_release_date = release_date;

        } else c_release_date = null;

        if (runtime != null && !runtime.isEmpty() && !runtime.equals("0")) {

            c_runtime = runtime;

        } else c_runtime = null;

        if (vote_average != null && !vote_average.trim().equals("0.0")) {
            c_vote_average = vote_average;
        } else c_vote_average = null;

        if (vote_count != null && !vote_count.trim().equals("0")) {
            c_vote_count = vote_count;
        } else c_vote_count = null;

        if (languages != null && !languages.isEmpty()) {
            c_languages = languages;
        } else c_languages = null;

        if (revenue != null && !revenue.isEmpty() && !revenue.equals("0") && !revenue.equalsIgnoreCase("0k")) {
            c_revenue = revenue;
        } else c_revenue = null;
        mMoviewiew.showMainInfoLayout();
        mMoviewiew.receiveMainInfo(c_title, c_poster_path, c_genres, c_release_date, c_runtime, c_vote_average, c_vote_count, c_languages, c_revenue);
    }

    private void updateTrailerLayout(MovieView mMoviewiew, String key) {
        if (key != null) {
            Log.d(TAG, "updateTrailerLayout: 2 ");
            mMoviewiew.showTrailerLayout();
            mMoviewiew.receiveTrailerKey(key);
        } else {
            Log.d(TAG, "updateTrailerLayout: 3 ");
            mMoviewiew.hideTrailerLayout();
        }
    }

    /**
     ***************************************************************************************************/

    @Override
    public void onDisabled() {
        Log.d(TAG, "onDisabled: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onDisabled: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onDisabled: 3 ");
                mMoviewiew.showYoutubeDisabledDialog();
            }
        }
    }

    @Override
    public void onNoInternet() {

        Log.d(TAG, "onNoInternet: 1 ");

        if (mMoviewiew != null) {
            Log.d(TAG, "onNoInternet: 2 ");

            if (((MovieFragment) mMoviewiew).v != null) {
                Log.d(TAG, "onNoInternet: 3 ");
                mMoviewiew.showSnackAlert("No Internet Connection Available!");
            }
        }
    }

    /**************************************** SEND RATE *********************************************/
    @Override
    public void sendRate(String ID, float rateValue) {
        Log.d(TAG, "sendRate: ");
        if (Utils.isNetworkAvailable(context))
        {
            if (rateValue>0.0) {
                mMoviewiew.showRatePd();
                mMoviewiew.disableRateButton();
                mMoviesInteractor.rateMovie(this,context, ID, rateValue);
            }
            else mMoviewiew.showMessage("Please rate!");
        }
        else
        {
            mMoviewiew.showMessage("Check internet connection and try again!");
        }
    }

    @Override
    public void onRateLayoutClicked() {
        mMoviewiew.showRatingLayout();
    }

    @Override
    public void onRecommendedMovieClicked(String movie_id, String title) {
        mMoviewiew.moveToMovie(movie_id,title);
    }

    @Override
    public void onImageSelected(int position, List<String> images) {
        mMoviewiew.showImageSlider(position,images);
    }

    @Override
    public void onCastClick(List<ListItemCastCrew> cast, int position) {

    }

    @Override
    public void onRateFailed(String message)
    {
        Log.d(TAG, "onRateFailed: ");
        mMoviewiew.hideRatePd();
        mMoviewiew.enableRateButton();
        mMoviewiew.showMessage(message);
    }

    @Override
    public void onRateSuccess() {
        Log.d(TAG, "onRateSuccess: ");
        mMoviewiew.hideRatePd();
        mMoviewiew.showMessage(context.getString(R.string.rate_sent_message));
        mMoviewiew.hideRatingLayout();

    }
}