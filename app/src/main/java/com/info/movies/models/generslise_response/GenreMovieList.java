package com.info.movies.models.generslise_response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 10/18/2017.
 */

public class GenreMovieList {
    @SerializedName("genres")
    List<Genre> geners;

    public List<Genre> getGeners() {
        return geners;
    }
}
