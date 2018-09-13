package com.info.movies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EhabSalah on 11/3/2017.
 */

public class ApiError {
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName("status_message")
    private String message = "Unknown error.";

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
