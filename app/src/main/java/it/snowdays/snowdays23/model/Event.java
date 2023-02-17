package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Event implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    @SerializedName("only_participants")
    private boolean participantsOnly;

    @SerializedName("left")
    private int left;

    @SerializedName("checked_in")
    private int checkedIn;

    @SerializedName("expected")
    private int expected;


    public Event() {
    }

    public Event(String name, String description, String icon, boolean participantsOnly) {
        this.name = name;
        this.slug = "-";
        this.description = description;
        this.icon = icon;
        this.participantsOnly = participantsOnly;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isParticipantsOnly() {
        return participantsOnly;
    }

    public int getLeft() {
        return left;
    }

    public int getCheckedIn() {
        return checkedIn;
    }

    public int getExpected() {
        return expected;
    }

    private Event(Parcel src) {
        this.name = src.readString();
        this.slug = src.readString();
        this.description = src.readString();
        this.icon = src.readString();
        this.participantsOnly = src.readInt() == 1;
        this.left = src.readInt();
        this.checkedIn = src.readInt();
        this.expected = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.slug);
        dest.writeString(this.description);
        dest.writeString(this.icon);
        dest.writeInt(this.participantsOnly ? 1 : 0);
        dest.writeInt(this.left);
        dest.writeInt(this.checkedIn);
        dest.writeInt(this.expected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
