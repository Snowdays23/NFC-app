package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class University implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("email_domain")
    private String domain;

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    private University(Parcel src) {
        this.name = src.readString();
        this.domain = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.domain);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<University> CREATOR = new Creator<University>() {
        @Override
        public University createFromParcel(Parcel source) {
            return new University(source);
        }

        @Override
        public University[] newArray(int size) {
            return new University[size];
        }
    };
}
