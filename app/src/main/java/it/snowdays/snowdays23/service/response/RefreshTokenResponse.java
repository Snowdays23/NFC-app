package it.snowdays.snowdays23.service.response;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse extends RestResponse {

    @SerializedName("access")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
