package com.info.movies.fargments.movie;

import com.info.movies.models.movie_page.ListItemCastCrew;
import com.info.movies.models.movie_page.ListItemVideo;
import com.info.movies.models.movie_page.MoviePage;
import com.info.movies.models.posters.MoviePosterAR;

import java.util.List;

/**
 * Created by EhabSalah on 1/12/2018.
 */

public interface MovieView {

    void showMainInfoLayout();
    void hideMainInfoLayout();
    void showExternalMediaLayout();
    void hideExternalMediaLayout();

    void showDetailesLayout();
    void hideDetailesLayout();

    void showTrailerLayout();
    void hideTrailerLayout();

    void showOverviewLayout();
    void hideOverviewLayout();

    void showVideoLayout();
    void hideVideoLayout();

    void showImageLayout();
    void hideImageLayout();

    void showCastCrewDirectorLayout();
    void hideCastCrewDirectorLayout();

    void showProgress();
    void hideProgress();

    void showCastLayout();
    void hideCastLayout();

        void showDirectorLayout();
    void hideDirectorLayout();


    void receiveTrailerKey(String trailer_key);

    void receiveMainInfo(String title, String poster_path, String genres, String release_date, String runtime, String vote_average, String vote_count, String languages, String revenue);

    void recieveOverview(String c_overview);

    void recieveVideos(List<ListItemVideo> videos);


    void recieveImages(List<String> images);

    void receiveCrewCastDirector(List<ListItemCastCrew> casts, String directoe_name);

    void recieveExternalMedia(String home_page, String imdp_id);

    void hideBelongsToLayout();

    void showBelongsToLayout();

    void recieveBelongsTo(String belongs_to);

    void hideProductionLayout();

    void showProductionLayout();

    void receiveProduction(List<String> companies, List<String> countries);

    void showTaglineLayout();

    void hideTaglineLayout();

    void receiveTagline(String tagline);

    void showSnackAlert(String s);

    void receiveSourceOfDate(MoviePage moviePage);


    void showYoutubeDisabledDialog();

    void showRatePd();

    void hideRatePd();

    void showMessage(String s);

    void hideRatingLayout();

    void showRatingLayout();

    void disableRateButton();

    void enableRateButton();

    void showRecommendationsLayout();

    void receiveRecommendations(List<MoviePosterAR> recommendations);

    void hideRecommendationsLayout();

    void moveToMovie(String movie_id, String title);

    void showImageSlider(int position, List<String> images);
}
