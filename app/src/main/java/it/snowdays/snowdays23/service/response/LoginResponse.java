package it.snowdays.snowdays23.service.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends RestResponse {

    @SerializedName("access")
    private String accessToken;

    @SerializedName("refresh")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
