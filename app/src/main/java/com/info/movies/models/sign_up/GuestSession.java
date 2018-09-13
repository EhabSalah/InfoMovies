package com.info.movies.models.sign_up;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ehab Salah on 4/11/2018.
 */

public class GuestSession {
    @SerializedName("success")
    private boolean success;
    @SerializedName("guest_session_id")
    private String guest_session_id;
    @SerializedName("expires_at")
    private String expires_at;

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getGuestSessionId() {
        return this.guest_session_id;
    }

    public void setGuestSessionId(String guest_session_id) {
        this.guest_session_id = guest_session_id;
    }


    public String getExpiresAt() {
        return this.expires_at;
    }

    public void setExpiresAt(String expires_at) {
        this.expires_at = expires_at;
    }

}

