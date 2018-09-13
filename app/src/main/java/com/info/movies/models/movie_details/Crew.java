package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class Crew {
    @SerializedName("credit_id")
    private String credit_id;

    @SerializedName("department")
    private String department;

    @SerializedName("gender")
    private String gender;

    @SerializedName("id")
    private String id;

    @SerializedName("job")
    private String job;

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profile_path;

    public String getCredit_id() {
        return credit_id;
    }

    public String getDepartment() {
        return department;
    }

    public String getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getProfile_path() {
        return profile_path;
    }
}
