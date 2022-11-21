package it.snowdays.snowdays23.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EatingHabits implements Parcelable {

    @SerializedName("vegetarian")
    private boolean vegetarian;

    @SerializedName("vegan")
    private boolean vegan;

    @SerializedName("gluten_free")
    private boolean glutenFree;

    @SerializedName("lactose_free")
    private boolean lactoseFree;

    public boolean isVegetarian() {
        return vegetarian;
    }

    public boolean isVegan() {
        return vegan;
    }

    public boolean isGlutenFree() {
        return glutenFree;
    }

    public boolean isLactoseFree() {
        return lactoseFree;
    }

    private EatingHabits(Parcel src) {
        this.vegetarian = src.readInt() == 1;
        this.vegan = src.readInt() == 1;
        this.glutenFree = src.readInt() == 1;
        this.lactoseFree = src.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.vegetarian ? 1 : 0);
        dest.writeInt(this.vegan ? 1 : 0);
        dest.writeInt(this.glutenFree ? 1 : 0);
        dest.writeInt(this.lactoseFree ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EatingHabits> CREATOR = new Creator<EatingHabits>() {
        @Override
        public EatingHabits createFromParcel(Parcel source) {
            return new EatingHabits(source);
        }

        @Override
        public EatingHabits[] newArray(int size) {
            return new EatingHabits[size];
        }
    };
}
