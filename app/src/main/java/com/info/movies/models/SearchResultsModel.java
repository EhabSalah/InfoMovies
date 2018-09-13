package com.info.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EhabSalah on 11/25/2017.
 */

public class SearchResultsModel implements Parcelable {
    String id ;
    String movie_title;
    String image_url;
    String release_year;

    public SearchResultsModel(String id, String movie_title, String image_url, String release_year) {
        this.id = id;
        this.movie_title = movie_title;
        this.image_url = "https://image.tmdb.org/t/p/w185"+image_url;
        this.release_year = release_year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getPoster_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRelease_year() {
        return release_year;
    }

    public void setRelease_year(String release_year) {
        this.release_year = release_year;
    }

    /********************************************************************************************/

    private SearchResultsModel(Parcel in) {
        id = in.readString();
        movie_title = in.readString();
        image_url = in.readString();
        release_year = in.readString();
    }

    public static final Creator<SearchResultsModel> CREATOR = new Creator<SearchResultsModel>() {
        @Override
        public SearchResultsModel createFromParcel(Parcel in) {
            return new SearchResultsModel(in);
        }

        @Override
        public SearchResultsModel[] newArray(int size) {
            return new SearchResultsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(movie_title);
        parcel.writeString(image_url);
        parcel.writeString(release_year);
    }
}
