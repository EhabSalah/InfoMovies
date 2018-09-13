package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class Videos {
    @SerializedName("results")
    private List<Result> results;

    public Videos(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

}
