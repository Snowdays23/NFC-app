package it.snowdays.snowdays23.service.request;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {

    @SerializedName("refresh")
    private final String refreshToken;

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
