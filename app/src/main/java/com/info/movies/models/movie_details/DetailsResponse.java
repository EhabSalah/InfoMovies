package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class DetailsResponse {
    @SerializedName("adult")
    private boolean adult;

    @SerializedName("backdrop_path")
    private String backdrop_path;

    @SerializedName("belongs_to_collection")
    private BelongsToCollection belongs_to_collection;

    @SerializedName("budget")
    private String budget;

    @SerializedName("genres")
    private List<Genre> Genre;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("id")
    private String id;

    @SerializedName("imdb_id")
    private String imdb_id;

    @SerializedName("original_language")
    private String original_language;

    @SerializedName("original_title")
    private String original_title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private String popularity;

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("production_companies")
    private List<ProductionCompanie> production_companies;

    @SerializedName("production_countries")
    private List<ProductionCountrie> production_countries;


    @SerializedName("release_date")
    private String release_date;


    @SerializedName("revenue")
    private String revenue;


    @SerializedName("runtime")
    private String runtime;


    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spoken_languages;


    @SerializedName("status")
    private String status;


    @SerializedName("tagline")
    private String tagline;


    @SerializedName("title")
    private String title;


    @SerializedName("video")
    private boolean video;


    @SerializedName("vote_average")
    private String vote_average;


    @SerializedName("vote_count")
    private String vote_count;


    @SerializedName("videos")
    private Videos videos;


    @SerializedName("images")
    private Images images;


    @SerializedName("credits")
    private Credits credits;

    @SerializedName("recommendations")
    private Recommendations recommendations;

    public Recommendations getRecommendations() {
        return recommendations;
    }

    public BelongsToCollection getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }


    public String getBudget() {
        return budget;
    }

    public List<Genre> getGenre() {
        return Genre;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getId() {
        return id;
    }

    public String getImdb_id() {
        return imdb_id;
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

    public String getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public List<ProductionCompanie> getProduction_companies() {
        return production_companies;
    }

    public List<ProductionCountrie> getProduction_countries() {
        return production_countries;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public List<SpokenLanguage> getSpoken_languages() {
        return spoken_languages;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public Videos getVideos() {
        return videos;
    }

    public Images getImages() {
        return images;
    }

    public Credits getCredits() {
        return credits;
    }
}
