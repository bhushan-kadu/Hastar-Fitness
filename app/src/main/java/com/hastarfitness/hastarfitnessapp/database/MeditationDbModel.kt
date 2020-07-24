package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * table in database as well as a model to save meditation exercises
 */
@Entity
data class MeditationDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("type")
        @ColumnInfo val type: String,

        @SerializedName("name")
        @ColumnInfo val name: String,

        @SerializedName("desc")
        @ColumnInfo val desc: String,

        @SerializedName("audio_url")
        @ColumnInfo val audio: String?,

        @SerializedName("img_url")
        @ColumnInfo val img: String,

        @SerializedName("img_url")
        @ColumnInfo val fav: Int


): Parcelable, Serializable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString(),
                parcel.readString()!!,
                parcel.readInt()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)
                parcel.writeString(name)
                parcel.writeString(type)
                parcel.writeString(desc)
                parcel.writeString(audio)
                parcel.writeString(img)
                parcel.writeInt(fav)

        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<MeditationDbModel> {
                override fun createFromParcel(parcel: Parcel): MeditationDbModel {
                        return MeditationDbModel(parcel)
                }

                override fun newArray(size: Int): Array<MeditationDbModel?> {
                        return arrayOfNulls(size)
                }
        }
}