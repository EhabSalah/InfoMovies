package com.info.movies.models.movies_response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public class Movies {

    @SerializedName("results")
    private List<Result> nowPlayingMoviesList;
    @SerializedName("page")
    private int page_no;
    @SerializedName("total_results")
    private int total_movies;
    @SerializedName("dates")
    private Dates release_dates_intervals;
    @SerializedName("total_pages")
    private String total_pages;

    public List<Result> getMoviesList() {
        return nowPlayingMoviesList;
    }

    public int getPage_no() {
        return page_no;
    }

    public int getTotal_movies() {
        return total_movies;
    }

    public Dates getRelease_dates_intervals() {
        return release_dates_intervals;
    }

    public String getTotal_pages() {
        return total_pages;
    }
}
