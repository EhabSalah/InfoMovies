package com.info.movies;

import android.content.Context;
import android.util.Log;

import com.info.movies.models.sign_up.GuestSession;
import com.info.movies.rest.ApiClient;
import com.info.movies.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by EhabSalah on 10/31/2017.
 */

public class MainInteractorImp implements MainInteractor {
    Call<GuestSession> call;
    private static final String TAG  = MainInteractorImp.class.getSimpleName();
    @Override
    public void getSessionId(final OnRequestSessionIdListener listener, final Context context) {
        Log.d(TAG, "getSessionId: ");
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        call = apiInterface.requestGuestSession();
        call.enqueue(new Callback<GuestSession>() {
            @Override
            public void onResponse(Call<GuestSession> call, Response<GuestSession> response) {
                Log.d(TAG, "onResponse: ");
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: ");
                    listener.onSuccess(response.body(),context);
                }
            }

            @Override
            public void onFailure(Call<GuestSession> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void refreshInteractor() {
        Log.d(TAG, "refreshInteractor: ");
        if (call!=null) {
            Log.d(TAG, "refreshInteractor: ");
            call.cancel();
        }
    }
}
