package com.hastarfitness.hastarfitnessapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Model to be used for representing exercises
 */
data class Exercise(
        @SerializedName("name_of_exercise")
        val name: String,

        @SerializedName("type_of_exercise")
        val type: String,

        @SerializedName("desc_of_exercise")
        val desc: String,

        @SerializedName("url_of_gif")
        val img: String,

        @SerializedName("intensity_type")
        val intensity: String,

        @SerializedName("id")
        val id: Int,
        @SerializedName("id")
        val time: Int,
        @SerializedName("id")
        val fmet: Double,
        @SerializedName("id")
        val mmet: Double,

        var isSelected:Boolean): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readDouble(),
                parcel.readByte() != 0.toByte()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(name)
                parcel.writeString(type)
                parcel.writeString(desc)
                parcel.writeString(img)
                parcel.writeString(intensity)
                parcel.writeInt(id)
                parcel.writeInt(time)
                parcel.writeDouble(mmet)
                parcel.writeDouble(fmet)
                parcel.writeByte(if (isSelected) 1 else 0)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Exercise> {
                override fun createFromParcel(parcel: Parcel): Exercise {
                        return Exercise(parcel)
                }

                override fun newArray(size: Int): Array<Exercise?> {
                        return arrayOfNulls(size)
                }
        }
}