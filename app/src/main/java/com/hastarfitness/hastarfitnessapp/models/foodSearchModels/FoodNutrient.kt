package com.hastarfitness.hastarfitnessapp.models.foodSearchModels

import android.os.Parcel
import android.os.Parcelable


data class FoodNutrient(
    val derivationCode: String,
    val derivationDescription: String,
    val nutrientId: Int,
    val nutrientName: String,
    val nutrientNumber: String,
    val unitName: String,
    val value: Double
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(derivationCode)
        parcel.writeString(derivationDescription)
        parcel.writeInt(nutrientId)
        parcel.writeString(nutrientName)
        parcel.writeString(nutrientNumber)
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