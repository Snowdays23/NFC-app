package it.snowdays.snowdays23.service;

import java.util.List;

import it.snowdays.snowdays23.model.Participant;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SnowdaysService {

    @GET("participant/{uid}")
    Call<Participant> getParticipantByBraceletId(@Path("uid") String braceletId);

    @POST("participant/{id}/{uid}")
    Call<Response> assignBraceletToParticipant(@Path("id") int participantId, @Path("uid") String braceletId);

    @GET("participants")
    Call<List<Participant>> getAllParticipants();

}
