package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PartyBeast implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("bracelet_id")
    private String braceletId;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBraceletId() {
        return braceletId;
    }

    private PartyBeast(Parcel src) {
        this.id = src.readInt();
        this.firstName = src.readString();
        this.lastName = src.readString();
        this.email = src.readString();
        this.phone = src.readString();
        this.braceletId = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.braceletId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PartyBeast> CREATOR = new Creator<PartyBeast>() {
        @Override
        public PartyBeast createFromParcel(Parcel source) {
            return new PartyBeast(source);
        }

        @Override
        public PartyBeast[] newArray(int size) {
            return new PartyBeast[size];
        }
    };
}
