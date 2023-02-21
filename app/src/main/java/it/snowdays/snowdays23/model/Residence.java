package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Residence implements Parcelable {

    @SerializedName("address")
    private String address;

    @SerializedName("street_nr")
    private String streetNumber;

    @SerializedName("city")
    private String city;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("is_college")
    private boolean isCollege;

    public Residence() {
    }

    public String getAddress() {
        return address;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean isCollege() {
        return isCollege;
    }

    private Residence(Parcel src) {
        this.address = src.readString();
        this.streetNumber = src.readString();
        this.city = src.readString();
        this.postalCode = src.readString();
        this.isCollege = src.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(streetNumber);
        dest.writeString(city);
        dest.writeString(postalCode);
        dest.writeInt(isCollege ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Residence> CREATOR = new Creator<Residence>() {
        @Override
        public Residence createFromParcel(Parcel source) {
            return new Residence(source);
        }

        @Override
        public Residence[] newArray(int size) {
            return new Residence[size];
        }
    };
}
