package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * model as well as table to be used for exercise
 *
 * @author Bhushan Kadu
 */
@Entity
data class ExerciseDbModel(

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
        @ColumnInfo val intensity: String,

        @SerializedName("time")
        @ColumnInfo val time: Int,

        @SerializedName("m_met")
        @ColumnInfo val mmet: Double,

        @SerializedName("f_met")
        @ColumnInfo val fmet: Double


): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readDouble())

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                dest!!.writeInt(id)
                dest.writeString(name)
                dest.writeString(type)
                dest.writeString(desc)
                dest.writeString(img)
                dest.writeString(intensity)
                dest.writeInt(time)
                dest.writeDouble(mmet)
                dest.writeDouble(fmet)
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

