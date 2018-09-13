package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class Images
{
    @SerializedName("backdrops")
    private List<Image> images;
    @SerializedName("posters")
    private List<Image> posters;

    public Images(List<Image> images, List<Image> posters) {
        this.images = images;
        this.posters = posters;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Image> getPosters() {
        return posters;
    }
}
