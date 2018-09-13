package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class SpokenLanguage {
    @SerializedName("iso_639_1")
    private String iso;
    @SerializedName("person_profile_placeholder")
    private String name;

    public SpokenLanguage(String iso, String name) {
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
