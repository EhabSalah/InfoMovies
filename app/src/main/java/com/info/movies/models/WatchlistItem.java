package com.info.movies.models;

/**
 * Created by EhabSalah on 1/18/2018.
 */

public class WatchlistItem {
    String id;
    String title;
    String poster_path;
    String genres;

    public WatchlistItem() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getGenres() {
        return genres;
    }
}
