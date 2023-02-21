package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @SerializedName("phone")
    private String phone;

    @SerializedName("internal")
    private boolean internal;

    @SerializedName("university")
    private University university;

    @SerializedName("eating_habits")
    private EatingHabits eatingHabits;

    @SerializedName("additional_notes")
    private String additionalNotes;

    @SerializedName("bracelet_id")
    private String braceletId;

    @SerializedName("schlafi")
    private Participant schlafi;

    @SerializedName("residence")
    private Residence residence;

    @SerializedName("rented_gear")
    private List<Gear> rentedGear;

    @SerializedName("selected_sport")
    private String selectedSport;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("shoe_size")
    private int shoeSize;

    @SerializedName("helmet_size")
    private String headSize;

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

    public String getPhone() {
        return phone;
    }

    public boolean isInternal() {
        return internal;
    }

    public EatingHabits getEatingHabits() {
        return eatingHabits;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public String getBraceletId() {
        return braceletId;
    }

    public University getUniversity() {
        return university;
    }

    public Participant getSchlafi() {
        return schlafi;
    }

    public Residence getResidence() {
        return residence;
    }

    public List<Gear> getRentedGear() {
        return rentedGear;
    }

    public String getSelectedSport() {
        return selectedSport;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getShoeSize() {
        return shoeSize;
    }

    public String getHeadSize() {
        return headSize;
    }

    private Participant(Parcel src) {
        this.id = src.readInt();
        this.firstName = src.readString();
        this.lastName = src.readString();
        this.username = src.readString();
        this.email = src.readString();
        this.dateOfBirth = (Date) src.readSerializable();
        this.phone = src.readString();
        this.braceletId = src.readString();
        this.internal = src.readInt() == 1;
        this.eatingHabits = src.readParcelable(EatingHabits.class.getClassLoader());
        this.additionalNotes = src.readString();
        this.university = src.readParcelable(University.class.getClassLoader());
        this.schlafi = src.readParcelable(Participant.class.getClassLoader());
        this.residence = src.readParcelable(Residence.class.getClassLoader());
        this.rentedGear = new ArrayList<>();
        src.readTypedList(this.rentedGear, Gear.CREATOR);
        this.selectedSport = src.readString();
        this.height = src.readInt();
        this.weight = src.readInt();
        this.shoeSize = src.readInt();
        this.headSize = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeSerializable(this.dateOfBirth);
        dest.writeString(phone);
        dest.writeString(this.braceletId);
        dest.writeInt(this.internal ? 1 : 0);
        dest.writeParcelable(this.eatingHabits, 0);
        dest.writeString(this.additionalNotes);
        dest.writeParcelable(this.university, 0);
        dest.writeParcelable(this.schlafi, 0);
        dest.writeParcelable(this.residence, 0);
        dest.writeTypedList(this.rentedGear);
        dest.writeString(this.selectedSport);
        dest.writeInt(this.height);
        dest.writeInt(this.weight);
        dest.writeInt(this.shoeSize);
        dest.writeString(this.headSize);
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
