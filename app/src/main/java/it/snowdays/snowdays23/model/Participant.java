package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Participant implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("dob")
    private Date dateOfBirth;

    @SerializedName("internal")
    private boolean internal;

    @SerializedName("university")
    private University university;

    @SerializedName("eating_habits")
    private EatingHabits eatingHabits;

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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isInternal() {
        return internal;
    }

    public EatingHabits getEatingHabits() {
        return eatingHabits;
    }

    public String getBraceletId() {
        return braceletId;
    }

    public University getUniversity() {
        return university;
    }

    private Participant(Parcel src) {
        this.id = src.readInt();
        this.firstName = src.readString();
        this.lastName = src.readString();
        this.username = src.readString();
        this.email = src.readString();
        this.dateOfBirth = (Date) src.readSerializable();
        this.braceletId = src.readString();
        this.internal = src.readInt() == 1;
        this.eatingHabits = src.readParcelable(EatingHabits.class.getClassLoader());
        this.university = src.readParcelable(University.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeSerializable(this.dateOfBirth);
        dest.writeString(this.braceletId);
        dest.writeInt(this.internal ? 1 : 0);
        dest.writeParcelable(this.eatingHabits, 0);
        dest.writeParcelable(this.university, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel source) {
            return new Participant(source);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };
}
