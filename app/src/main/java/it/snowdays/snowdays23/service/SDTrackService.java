package it.snowdays.snowdays23.service;

import java.util.List;

import it.snowdays.snowdays23.model.Event;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.response.RestResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SDTrackService {

    @GET("participant/{uid}")
    Call<Participant> getParticipantByBraceletId(@Path("uid") String braceletId);

    @POST("participant/{id}/{uid}")
    Call<RestResponse> assignBraceletToParticipant(@Path("id") int participantId, @Path("uid") String braceletId);

    @POST("events")
    Call<Event> createEvent(@Body Event event);

    @GET("events")
    Call<List<Event>> getEvents();

    @GET("event/{slug}")
    Call<Event> getEvent(@Path("slug") String slug);

    @POST("event/{event_slug}/check-in/{bracelet_uid}")
    Call<RestResponse> checkIn(@Path("event_slug") String eventSlug, @Path("bracelet_uid") String braceletId);
}
