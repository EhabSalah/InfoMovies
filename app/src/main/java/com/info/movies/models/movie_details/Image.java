package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class Image {
    @SerializedName("aspect_ratio")
    private String aspect_ratio ;

    @SerializedName("file_path")
    private String file_path ;

    @SerializedName("height")
    private String height ;

    @SerializedName("iso_639_1")
    private String iso_639_1 ;

    @SerializedName("vote_average")
    private String vote_average ;

    @SerializedName("vote_count")
    private String vote_count ;

    @SerializedName("width")
    private String width ;

    public Image(String aspect_ratio, String file_path, String height, String iso_639_1, String vote_average, String vote_count, String width) {
        this.aspect_ratio = aspect_ratio;
        this.file_path = file_path;
        this.height = height;
        this.iso_639_1 = iso_639_1;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.width = width;
    }

    public String getAspect_ratio() {
        return aspect_ratio;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getHeight() {
        return height;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public String getWidth() {
        return width;
    }
}
