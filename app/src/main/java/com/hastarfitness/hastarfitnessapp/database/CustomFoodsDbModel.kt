package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * model as well as table to be used for exercise
 */
@Entity
data class CustomFoodsDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("name_of_food")
        @ColumnInfo val name: String,

        @SerializedName("calories")
        @ColumnInfo val calories: Double,

        @SerializedName("measurement")
        @ColumnInfo val measurement: Int,

        @SerializedName("serving_or_gram")
        @ColumnInfo val servingsOrGrams: Double,

        @SerializedName("protein")
        @ColumnInfo val protein: Double,

        @SerializedName("carbs")
        @ColumnInfo val carbs: Double,

        @SerializedName("fat")
        @ColumnInfo val fat: Double




): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readDouble()!!,
            parcel.readInt()!!,
            parcel.readDouble()!!,
            parcel.readDouble()!!,
            parcel.readDouble(),
            parcel.readDouble()
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(id)
        dest.writeString(name)
        dest.writeDouble(calories)
        dest.writeInt(measurement)
        dest.writeDouble(servingsOrGrams)
        dest.writeDouble(protein)
        dest.writeDouble(carbs)
        dest.writeDouble(fat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExerciseDbModel> {
        override fun createFromParcel(parcel: Parcel): ExerciseDbModel {
            return ExerciseDbModel(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseDbModel?> {
            return arrayOfNulls(size)
        }
    }
}

