package it.snowdays.snowdays23.service;

import androidx.annotation.NonNull;

import it.snowdays.snowdays23.service.response.RefreshTokenResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshTokenCallback implements Callback<RefreshTokenResponse> {

    private final OnTokenRefreshedCallback onTokenRefreshedCallback;
    private final OnTokenRefreshFailureCallback onTokenRefreshFailureCallback;

    public RefreshTokenCallback(OnTokenRefreshedCallback t1, OnTokenRefreshFailureCallback t2) {
        this.onTokenRefreshedCallback = t1;
        this.onTokenRefreshFailureCallback = t2;
    }

    @Override
    public void onResponse(@NonNull Call<RefreshTokenResponse> call, @NonNull Response<RefreshTokenResponse> response) {
        if (response.isSuccessful()) {
            final RefreshTokenResponse rtc = response.body();
            if (rtc != null) {
                if (onTokenRefreshedCallback != null) {
                    onTokenRefreshedCallback.onTokenRefreshed(rtc.getAccessToken());
                    return;
                }
            }
        }
        if (onTokenRefreshFailureCallback != null) {
            onTokenRefreshFailureCallback.onFailure(new RuntimeException("Refresh token request did not succeed"));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RefreshTokenResponse> call, @NonNull Throwable t) {
        if (onTokenRefreshFailureCallback != null) {
            onTokenRefreshFailureCallback.onFailure(t);
        }
    }

    public interface OnTokenRefreshedCallback {
        void onTokenRefreshed(String accessToken);
    }

    public interface OnTokenRefreshFailureCallback {
        void onFailure(Throwable t);
    }
}
