package it.snowdays.snowdays23.service.request;

import com.google.gson.annotations.SerializedName;

public class VerifyTokenRequest {

    @SerializedName("token")
    private final String token;

    public VerifyTokenRequest(String token) {
        this.token = token;
    }

}
