package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodNutrient

@Entity
data class LastSearchedFoods(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val fdcId:Int,

        @SerializedName("name")
        val name:String,

        @SerializedName("protein")
        @ColumnInfo val protein: Double,

        @SerializedName("fats")
        @ColumnInfo val fats: Double,

        @SerializedName("cardbs")
        @ColumnInfo val carbs: Double,

        @SerializedName("energy")
        @ColumnInfo val energy: Double
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(fdcId)
        parcel.writeString(name)
        parcel.writeDouble(protein)
        parcel.writeDouble(carbs)
        parcel.writeDouble(fats)
        parcel.writeDouble(energy)
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