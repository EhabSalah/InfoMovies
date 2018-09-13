package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class BelongsToCollection {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private String poster_path;
    @SerializedName("backdrop_path")
    private String backdrop_path;

    public BelongsToCollection(String id, String name, String poster_path, String backdrop_path) {
        this.id = id;
        this.name = name;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }
}
