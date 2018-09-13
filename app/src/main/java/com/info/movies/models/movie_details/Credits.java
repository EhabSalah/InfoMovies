package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public  class Credits {
    @SerializedName("cast")
    private List<Cast> cast;
    @SerializedName("crew")
    private List<Crew> crew;

    public Credits(List<Cast> cast, List<Crew> crew) {
        this.cast = cast;
        this.crew = crew;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }
}
