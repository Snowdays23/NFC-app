package it.snowdays.snowdays23.service;

import it.snowdays.snowdays23.service.request.LoginRequest;
import it.snowdays.snowdays23.service.request.RefreshTokenRequest;
import it.snowdays.snowdays23.service.request.VerifyTokenRequest;
import it.snowdays.snowdays23.service.response.LoginResponse;
import it.snowdays.snowdays23.service.response.RefreshTokenResponse;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SDLoginService {

    @POST("token/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("token/refresh/")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest request);

    @GET("token/verify/")
    Call<Response> verifyToken(@Body VerifyTokenRequest request);

}
