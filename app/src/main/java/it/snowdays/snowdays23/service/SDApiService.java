package it.snowdays.snowdays23.service;

import java.util.List;

import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.model.PartyBeast;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SDApiService {

    @GET("participants")
    Call<List<Participant>> getAllParticipants();

    @GET("partybeasts")
    Call<List<PartyBeast>> getAllPartyBeasts();

}
