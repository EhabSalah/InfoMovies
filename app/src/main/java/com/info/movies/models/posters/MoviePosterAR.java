package com.info.movies.models.posters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ehab Salah A. LAtif on 6/12/2017.
 */

public class MoviePosterAR implements Parcelable
{
    public final String POSTER_URL_START_POINT = "https://image.tmdb.org/t/p/w300";
    String poster_image_url ;
    String movie_id;
    String vote_average;
    String title;



    public MoviePosterAR(String poster_image_url, String movie_id, String vote_average, String title){
        this.setPoster_image_url(POSTER_URL_START_POINT+poster_image_url);
        this.setMovie_id(movie_id);
        this.setVote_average(vote_average);
        this.setTitle(title);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
 //***********************************************************************************************//

 private MoviePosterAR(Parcel in) {
     poster_image_url = in.readString();
     movie_id = in.readString();
     vote_average = in.readString();
 }
    public static final Creator<MoviePosterAR> CREATOR = new Creator<MoviePosterAR>() {
        public MoviePosterAR createFromParcel(Parcel in) {
            return new MoviePosterAR(in);
        }

        public MoviePosterAR[] newArray(int size) {
            return new MoviePosterAR[size];
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
