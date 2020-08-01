package com.hastarfitness.hastarfitnessapp.fitnessCalculators

import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

/**
 * Class for all fitness calculators
 *
 * @author Bhushan Kadu
 */
class FitnessCalculators {

    /**
     *  calculate BMR using metric values - kg, cm
     *
     *  @param height height in cm
     *  @param weight weight in kg
     *  @param age age
     *  @param isMale true if male false if female
     */
    fun calculateBMRMetric(height: Double, weight: Double, age: Int, isMale: Boolean): Double {
//        return if (isMale) {
//            10 * weight + 6.25 * height - 5 * age + 5
//        } else {
//            10 * weight + 6.25 * height - 5 * age + 161
//        }
        return if (isMale) {
            (13.75 * weight) + (5 * height) - (6.76 * age) + 66
        } else {
            (9.56 * weight) + (1.85 * height) - (4.68 * age) + 655
        }
    }
    /**
     *  calculate TDEE using metric values - kg, cm
     *
     *  @param height height in cm
     *  @param weight weight in kg
     *  @param age age
     *  @param isMale true if male false if female
     */
    fun calculateTDEE(bmr: Double, exerciseLevel:String): Double {
        when(exerciseLevel){
            AppConstants.LITTLE_OR_NO_EXERCISE -> {
                return bmr * 1.2
            }
            AppConstants.LIGHT_EXERCISE -> {
                return bmr * 1.375
            }
                AppConstants.MODERATE_EXERCISE -> {
                return bmr * 1.55
            }
            AppConstants.HARD_EXERCISE -> {
                return bmr * 1.725
            }
            AppConstants.VERY_HARD_EXERCISE -> {
                return bmr * 1.9
            }
        }
        return -1.0
    }

    /**
     *  calculate BMR using Imperial values - lb, inch
     *
     *  @param height height in inches
     *  @param weight weight in pounds(lb)
     *  @param age age
     *  @param isMale true if male false if female
     */
    fun calculateBMRImperial(height: Double, weight: Double, age: Int, isMale: Boolean): Double {
        var height = height
        var weight = weight
        weight *= 0.453592
        height *= 2.54
        return calculateBMRMetric(height, weight, age, isMale)
    }

    /**
     *  calculate BMI using Imperial values - lb, inch
     *
     *  @param height height in inches
     *  @param weight weight in pounds(lb)
     */
    fun calculateBMIImperial(height: Double, weight: Double): Double {
        return weight * 703 / height.pow(2.0)
    }

    /**
     *  calculate BMI using Metric values - kg, cm
     *
     *  @param height height in cm
     *  @param weight weight in kg
     */
    fun calculateBMIMetric(height: Double, weight: Double): Double {
        return weight / height / height * 10000.0
    }

    /**
     *  convert KG to pound(lb)
     *
     *  @param kg weight in kg
     *  @return weight in pound(lb)
     */
    fun kgToLb(kg: Double): Double {
        return round(kg * 2.20462)
    }

    /**
     *  convert lb to kg
     *
     *  @param lb pound in Double
     *  @return weight in kg
     */
    fun lbToKg(lb: Double): Double {
        return round(lb / 2.20462)
    }


    /**
     *  convert feet and inches to cm
     *
     *  @param ftIn feet and inches
     *  @return height in cm
     */
    fun ftInToCm(ftIn: String): Double {
        val split = ftIn.split(" ")
        val ft = split[0].toDouble()
        val inch = split[1].toDouble()
        return round((ft * 30.48) + (inch * 2.54))
    }

    /**
     *  convert inches to cm
     *
     *  @param inches inches in Double
     *  @return height in cm
     */
    fun inToCm(inches: Double): Double {
        return inches * 2.54
    }



    /**
     *  convert cm to feet and inches
     *
     *  @param cm height in cm
     *  @return height in feet and inches
     */
    fun cmToftIn(cm: Double): String {

        val foot = cm / 2.54/12
        var  numberD = (foot).toString()
        numberD = numberD.substring ( numberD.indexOf ( "." ) );
        val inches = numberD.toString().toDouble()*12
        val footInt = foot.toInt()

        return "$footInt $inches"

    }

    /**
     *  calculate Body fat of women - Imperial values
     *
     *  @param height height in inches
     *  @param waistMeasure waistMeasure in inches
     *  @param hipMeasure hipMeasure in inches
     *  @param neakMeasure neakMeasure in inches
     *  @return body fat of women
     */
    fun bodyFatWomen(height: Double,
                     waistMeasure: Double,
                     hipMeasure: Double,
                     neakMeasure: Double): Double {
        return 163.205 * log10(waistMeasure + hipMeasure - neakMeasure) - 97.684 * log10(height) + 36.76
    }

    /**
     *  calculate Body fat of men - Imperial values
     *
     *  @param height height in inches
     *  @param waistMeasure waistMeasure in inches
     *  @param neakMeasure neakMeasure in inches
     *  @return body fat of men
     */
    fun bodyFatMen(height: Double,
                   waistMeasure: Double,
                   neakMeasure: Double): Double {
        return (86.010 * log10(waistMeasure - neakMeasure) - 70.041 * log10(height) + 36.76)
    }

    /**
     *  calculate macros
     *
     *  @param calories calories in Kcal
     *  @param protein protein ratio
     *  @param fat fat ratio
     *  @param carbs carbs ratio
     *  @return hash map containing protein carbs and fat values in gm
     */
    fun macroCalc(calories: Int, protein: Int, carbs: Int, fat: Int): HashMap<String, Double> {

        val proteinGm = (protein / 100.0 * calories) / 4
        val carbsGm = (carbs / 100.0 * calories) / 4
        val fatGm = (fat / 100.0 * calories) / 9


        val hashMap = hashMapOf<String, Double>()
        hashMap[AppConstants.PROTEIN] = proteinGm
        hashMap[AppConstants.CARBS] = carbsGm
        hashMap[AppConstants.FAT] = fatGm

        return hashMap
    }

}