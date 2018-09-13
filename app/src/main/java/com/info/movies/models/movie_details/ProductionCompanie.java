package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class ProductionCompanie {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public ProductionCompanie(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
