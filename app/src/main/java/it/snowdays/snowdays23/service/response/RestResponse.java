package it.snowdays.snowdays23.service.response;

import com.google.gson.annotations.SerializedName;

public class RestResponse {

    @SerializedName("detail")
    private String status;

    public String getStatus() {
        return status;
    }
}
