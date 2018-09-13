package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class ProductionCountrie {
    @SerializedName("iso_3166_1")
    private String iso;
    @SerializedName("name")
    private String name;

    public ProductionCountrie(String iso, String name) {
        this.iso = iso;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public String getName() {
        return name;
    }
}
