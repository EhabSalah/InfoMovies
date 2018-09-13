package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class Cast {
    @SerializedName("cast_id")
    private String cast_id;
    @SerializedName("character")
    private String character;
    @SerializedName("credit_id")
    private String credit_id;
    @SerializedName("gender")
    private String gender;
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("order")
    private String order;
    @SerializedName("profile_path")
    private String profile_path;

    public Cast(String cast_id, String character, String credit_id, String gender, String id, String name, String order, String profile_path) {
        this.cast_id = cast_id;
        this.character = character;
        this.credit_id = credit_id;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.order = order;
        this.profile_path = profile_path;
    }

    public String getCast_id() {
        return cast_id;
    }

    public String getCharacter() {
        return character;
    }

    public String getCredit_id() {
        return credit_id;
    }

    public String getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrder() {
        return order;
    }

    public String getProfile_path() {
        return profile_path;
    }
}
