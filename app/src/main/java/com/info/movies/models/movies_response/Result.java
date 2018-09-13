package com.info.movies.models.movies_response;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public class Result {
    private String vote_count;
    private String id;
    private boolean video;
    private String vote_average;
    private String title;
    private String popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private ArrayList<Integer> genre_ids;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private String release_date;

    public String getVote_count() {
        return vote_count;
    }

    public String getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public ArrayList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }
}
