package com.info.movies.rest.helpers;

import com.info.movies.models.ApiError;
import com.info.movies.rest.ApiClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by EhabSalah on 11/3/2017.
 */

public class ErrorUtils {
    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody,ApiError> converter =
                ApiClient.retrofit
                .responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }
        return error;
    }
}
