package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * model for qoutes
 *
 * @author Bhushan Kadu
 */
@Entity
data class QuotesDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("quote")
        @ColumnInfo val quote: String


): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!

            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(id)
        dest.writeString(quote)

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

