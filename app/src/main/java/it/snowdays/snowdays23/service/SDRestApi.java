package it.snowdays.snowdays23.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SDRestApi {

    private static final Retrofit sdRetrofit;
    private static final SnowdaysService sdService;

    static {
        sdRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://snowdays-staging.herokuapp.com/api/")
                .build();
        sdService = sdRetrofit.create(SnowdaysService.class);
    }

    public static SnowdaysService getSnowdaysService() {
        return sdService;
    }
}
