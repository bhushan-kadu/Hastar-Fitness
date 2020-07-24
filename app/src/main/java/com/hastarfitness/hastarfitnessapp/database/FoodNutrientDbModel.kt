package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodNutrient

@Entity
data class FoodNutrientDbModel(
        @SerializedName("fdc_id")
        @PrimaryKey(autoGenerate = true) val id:Int,

        @SerializedName("id")
        val fdcId:Int,

        @SerializedName("nutrientId")
        @ColumnInfo val nutrientId: Int,

        @SerializedName("nutrientName")
        @ColumnInfo val nutrientName: String,

        @SerializedName("unitName")
        @ColumnInfo val unitName: String,

        @SerializedName("value")
        @ColumnInfo val value: Double
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(fdcId)
        parcel.writeInt(nutrientId)
        parcel.writeString(nutrientName)
        parcel.writeString(unitName)
        parcel.writeDouble(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodNutrient> {
        override fun createFromParcel(parcel: Parcel): FoodNutrient {
            return FoodNutrient(parcel)
        }

        override fun newArray(size: Int): Array<FoodNutrient?> {
            return arrayOfNulls(size)
        }
    }
}