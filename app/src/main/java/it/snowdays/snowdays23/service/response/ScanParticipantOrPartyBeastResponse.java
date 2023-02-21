package it.snowdays.snowdays23.service.response;

import com.google.gson.annotations.SerializedName;

import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.model.PartyBeast;

public class ScanParticipantOrPartyBeastResponse extends RestResponse {

    @SerializedName("participant")
    private Participant participant;

    @SerializedName("party_beast")
    private PartyBeast partyBeast;

    public Participant getParticipant() {
        return participant;
    }

    public PartyBeast getPartyBeast() {
        return partyBeast;
    }
}
