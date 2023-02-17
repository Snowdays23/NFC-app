package it.snowdays.snowdays23.service;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.service.request.RefreshTokenRequest;
import it.snowdays.snowdays23.util.platform.Prefs;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SDRestApi {

    private static final Retrofit sdLoginRetrofit;
    private static final Retrofit sdApiRetrofit;
    private static final Retrofit sdTrackRetrofit;

    private static final SDLoginService sdLoginService;
    private static final SDApiService sdApiService;
    private static final SDTrackService sdTrackService;

    private static String accessToken, refreshToken;

    public static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    static {
        sdLoginRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://snowdays-staging.herokuapp.com/api/")
                .build();
        sdApiRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://snowdays-staging.herokuapp.com/api/")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + accessToken)
                                        .build())
                        ).build())
                .build();
        sdTrackRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://snowdays-staging.herokuapp.com/api/track/")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + accessToken)
                                        .build())
                        ).build())
                .build();
        sdLoginService = sdLoginRetrofit.create(SDLoginService.class);
        sdApiService = sdApiRetrofit.create(SDApiService.class);
        sdTrackService = sdTrackRetrofit.create(SDTrackService.class);
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static void setRefreshToken(String token) {
        refreshToken = token;
    }

    public static SDLoginService getSDLoginService() {
        return sdLoginService;
    }

    public static SDApiService getSdApiService() {
        return sdApiService;
    }

    public static SDTrackService getSDTrackingService() {
        return sdTrackService;
    }

    public static <T> void refreshTokenAndReplicate(@NonNull RefreshTokenCallback.OnTokenRefreshedCallback ok,
                                                    @NonNull RefreshTokenCallback.OnTokenRefreshFailureCallback ko) {
        getSDLoginService().refreshToken(new RefreshTokenRequest(refreshToken))
                .enqueue(new RefreshTokenCallback(ok, ko));
    }
}
