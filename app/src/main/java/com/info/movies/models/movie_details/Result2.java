package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Ehab Salah on 4/17/2018.
 */

public class Result2 {
    @SerializedName("adult")
    private boolean adult;

    @SerializedName("backdrop_path")
    private String backdrop_path;

    @SerializedName("genre_ids")
    private ArrayList<Integer> genre_ids;

    @SerializedName("id")
    private int id;

    @SerializedName("original_language")
    private String original_language;

    @SerializedName("original_title")
    private String original_title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("release_date")
    private String release_date;

    @SerializedName("title")
    private String title;

    @SerializedName("video")
    private boolean video;

    @SerializedName("vote_average")
    private double vote_average;

    @SerializedName("vote_count")
    private int vote_count;

    public boolean isAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public ArrayList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }
}