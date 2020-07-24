package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity

data class YogaExerciseDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("name_of_exercise")
        @ColumnInfo val name: String,

        @SerializedName("type_of_exercise")
        @ColumnInfo val type: String,

        @SerializedName("desc_of_exercise")
        @ColumnInfo val desc: String,

        @SerializedName("url_of_gif")
        @ColumnInfo val img: String,

        @SerializedName("intensity_type")
        @ColumnInfo val intensity: String




) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(desc)
        parcel.writeString(img)
        parcel.writeString(intensity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<YogaExerciseDbModel> {
        override fun createFromParcel(parcel: Parcel): YogaExerciseDbModel {
            return YogaExerciseDbModel(parcel)
        }

        override fun newArray(size: Int): Array<YogaExerciseDbModel?> {
            return arrayOfNulls(size)
        }
    }
}