package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity

data class WorkoutPlansDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("name")
        @ColumnInfo val name: String,

        @SerializedName("description")
        @ColumnInfo val desc: String,

        @SerializedName("intensity")
        @ColumnInfo val intensity: String,

        @SerializedName("type")
        @ColumnInfo val type: String,

        @SerializedName("is_user_plan")
        @ColumnInfo val isUserPlan: Int,

        @SerializedName("is_fav")
        @ColumnInfo val isFav: Int

): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt()

    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(id)
        dest.writeString(name)
        dest.writeString(desc)
        dest.writeString(intensity)
        dest.writeString(type)
        dest.writeInt(isUserPlan)
        dest.writeInt(isFav)

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

