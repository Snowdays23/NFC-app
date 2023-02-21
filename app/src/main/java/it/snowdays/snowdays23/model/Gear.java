package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Gear implements Parcelable {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public Gear() {
    }

    private Gear(Parcel src) {
        this.name = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Gear> CREATOR = new Creator<Gear>() {
        @Override
        public Gear createFromParcel(Parcel source) {
            return new Gear(source);
        }

        @Override
        public Gear[] newArray(int size) {
            return new Gear[size];
        }
    };
}
