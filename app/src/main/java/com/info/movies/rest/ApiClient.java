package com.info.movies.rest;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by EhabSalah on 9/10/2017.
 */

public class ApiClient {
    public static final String BASE_URL = "https://api.themoviedb.org/";
    public static Retrofit retrofit = null;
    public static Retrofit getApiClient()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(createOkHttpClient()) // <- This custom client will append the "api_key=4b681fdc4cf3c1807a59765da5b70b33" query after every request.
                    .addConverterFactory(GsonConverterFactory.create())
                    .build(); // this will return an instance of retrofit.
        }
        return retrofit;
    }
    private static OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", "4b681fdc4cf3c1807a59765da5b70b33")
                        .build();

                // Request customization: add request headers
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return httpClient.build();
    }

}
