package com.info.movies.models.posters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class TopRatedMovie implements Parcelable
    {
        public final String POSTER_URL_START_POINT = "https://image.tmdb.org/t/p/w300";
        String poster_image_url ;
        String movie_id;
        String vote_average;
        String title;
        String release_date;
        String geners;
        String overView;

        public TopRatedMovie(String poster_image_url, String movie_id, String vote_average, String title, String release_date, String geners, String overView){
            this.setPoster_image_url(POSTER_URL_START_POINT+poster_image_url);
            this.setMovie_id(movie_id);
            this.setVote_average(vote_average);
            this.setTitle(title);
            this.setRelease_date(release_date);
            this.setGeners(geners);
            this.setOverView(overView);

        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRelease_date() {
            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public String getGeners() {
            return geners;
        }

        public void setGeners(String geners) {
            this.geners = geners;
        }

        public String getOverView() {
            return overView;
        }

        public void setOverView(String overView) {
            this.overView = overView;
        }

    public String getPoster_image_url() {
        return poster_image_url;
    }

    public void setPoster_image_url(String poster_image_url) {
        this.poster_image_url = poster_image_url;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    //***********************************************************************************************//

    private TopRatedMovie(Parcel in) {
        poster_image_url = in.readString();
        movie_id = in.readString();
        vote_average = in.readString();
    }
    public static final Creator<TopRatedMovie> CREATOR = new Creator<TopRatedMovie>() {
        public TopRatedMovie createFromParcel(Parcel in) {
            return new TopRatedMovie(in);
        }

        public TopRatedMovie[] newArray(int size) {
            return new TopRatedMovie[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(poster_image_url);
        parcel.writeString(movie_id);
        parcel.writeString(vote_average);
    }
}


