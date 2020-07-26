package com.hastarfitness.hastarfitnessapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * table in database as well as a model to save exercises only
 * and to show which exercise belongs to which plan
 *
 * @author Bhushan Kadu
 */
@Entity(foreignKeys = [
    ForeignKey(entity = ExerciseDbModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exerciseId")),
    ForeignKey(entity = WorkoutPlansDbModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("planId"))])


data class PlanExercisesDbModel(


        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("exercise_id")
        @ColumnInfo val exerciseId: Int,

        @SerializedName("plan_id")
        @ColumnInfo val planId: Int

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
    )


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(id)
        dest.writeInt(exerciseId)
        dest.writeInt(planId)

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



