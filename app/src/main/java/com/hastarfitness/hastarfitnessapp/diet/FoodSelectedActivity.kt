package com.hastarfitness.hastarfitnessapp.diet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.FoodNutrientDbModel
import com.hastarfitness.hastarfitnessapp.database.LastSearchedFoods
import com.hastarfitness.hastarfitnessapp.database.UserFoodConsumedDataDbModel
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodNutrient
import com.hastarfitness.hastarfitnessapp.models.singleFood.SingleFood
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Max
import com.mobsandgeeks.saripaar.annotation.Min
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.activity_food_search.*
import kotlinx.android.synthetic.main.activity_food_selected.*
import kotlinx.android.synthetic.main.activity_food_selected.toolbar
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class FoodSelectedActivity : AppCompatActivity(), Validator.ValidationListener {
    private val roundingFormat = DecimalFormat("#.##")
    private var proteinPer100Gm: Double = 0.0
    private var carbsPer100Gm: Double = 0.0
    private var fatPer100Gm: Double = 0.0
    private var energyPer100Gm: Double = 0.0

    @NotEmpty
    @Max(1000, message = "Please Select maximum 8 ")
    @Min(1, message = "Please Select minimum 1 ")
    private lateinit var quantityEditText: EditText
    private lateinit var globalValidator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_selected)
        setSupportActionBar(toolbar)
        quantityEditText = quantity_editText

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        initialize()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isCustomFood) {
                    if (position == 0) {
                        quantity_editText.setText("100")
                        setPer100GmData()
                    } else {
                        quantity_editText.setText("1")
                        setPerServingData()
                    }
                }
            }

        }

        quantity_editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    if (!isCustomFood) {
                        if (spinner.selectedItemPosition == 0) {
                            quantity_textView.text = "${s} gm"
                            setPer100GmData()
                        } else {
                            quantity_textView.text = "${singleFood.servingSize * s.toString().toInt()} gm"
                            setPerServingData()
                        }
                    } else {
                        if (measurement == AppConstants.SERVINGS_MEASUREMENT) {
                            quantity_textView.text = "${servingsOrGramValue * s.toString().toInt()} gm"
                            setPerUserSelectedServingData()
                        } else {
                            quantity_textView.text = "${s} gm"
                            setPerUserSelectedGmData()
                        }
                    }
                }

            }
        })

        addFood_button.setOnClickListener {
            globalValidator.validate()
        }

    }

    fun addFood() {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val energy = kcal_textView.text.split("kcal")[0].toDouble()
        val protein = protein_textView.text.split("gm")[0].toDouble()
        val fat = fat_textView.text.split("gm")[0].toDouble()
        val carbs = carbs_textView.text.split("gm")[0].toDouble()
        val grams = quantity_textView.text.split("gm")[0].toDouble()

        val consumedFoodData = UserFoodConsumedDataDbModel(
                0,
                energy,
                protein,
                fat,
                carbs,
                foodName,
                foodId,
                today.time,
                mealType,
                grams)
        viewModel.insertUserFoodConsumedData(db, consumedFoodData)
        viewModel.insertedRowLong.observe(this, Observer {
            it
            val i = Intent(this, ActivityDashboard::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            i.putExtra(AppConstants.SWITCH_FRAGMENT, R.id.navigation_diet)
            startActivity(i)
        })

    }


    private fun setNutrients(multiplyFactor: Double) {
        nutrients_linearLayout.removeAllViews()
        for (nutrientItr in nutrients) {
            if (nutrientItr.nutrientId == AppConstants.PROTEIN_ID ||
                    nutrientItr.nutrientId == AppConstants.FAT_ID ||
                    nutrientItr.nutrientId == AppConstants.CARBS_ID ||
                    nutrientItr.nutrientId == AppConstants.ENERGY_ID) {
                continue
            }
            val row = layoutInflater.inflate(R.layout.layout_nutrient_row, null)
            val tvName = row.findViewById<TextView>(R.id.name)
            val tvValue = row.findViewById<TextView>(R.id.value)

            tvName.text = nutrientItr.nutrientName
            tvValue.text = "${roundingFormat.format(nutrientItr.value * multiplyFactor)} ${nutrientItr.unitName.toLowerCase()}"

            nutrients_linearLayout.addView(row)
        }
    }

    lateinit var singleFood: SingleFood
    fun setPerServingData() {

        val servingQuantity = quantity_editText.text.toString().toDouble()
        var multiplyFactor = (singleFood.servingSize / 100) * servingQuantity

        val proteinInServing = roundingFormat.format(proteinPer100Gm * multiplyFactor)
        val carbsInServing = roundingFormat.format(carbsPer100Gm * multiplyFactor)
        val fatInServing = roundingFormat.format(fatPer100Gm * multiplyFactor)
        val energyInServing = roundingFormat.format(energyPer100Gm * multiplyFactor)
        protein_textView.text = "${proteinInServing} gm"

        carbs_textView.text = "${carbsInServing} gm"

        fat_textView.text = "${fatInServing} gm"

        kcal_textView.text = "${energyInServing} kcal"

        setNutrients(multiplyFactor)

    }

    fun setPerUserSelectedServingData() {
        val multiplyFactor = quantity_editText.text.toString().toDouble()

        val proteinInServing = roundingFormat.format(proteinPer100Gm * multiplyFactor)
        val carbsInServing = roundingFormat.format(carbsPer100Gm * multiplyFactor)
        val fatInServing = roundingFormat.format(fatPer100Gm * multiplyFactor)
        val energyInServing = roundingFormat.format(energyPer100Gm * multiplyFactor)
        protein_textView.text = "${proteinInServing} gm"

        carbs_textView.text = "${carbsInServing} gm"

        fat_textView.text = "${fatInServing} gm"

        kcal_textView.text = "${energyInServing} kcal"

    }

    fun setPer100GmData() {

        val servingQuantity = quantity_editText.text.toString().toDouble()
        var multiplyFactor = (servingQuantity / 100)

        protein_textView.text = "${roundingFormat.format(proteinPer100Gm * multiplyFactor)} gm"

        carbs_textView.text = "${roundingFormat.format(carbsPer100Gm * multiplyFactor)} gm"

        fat_textView.text = "${roundingFormat.format(fatPer100Gm * multiplyFactor)} gm"

        kcal_textView.text = "${roundingFormat.format(energyPer100Gm * multiplyFactor)} kcal"

        setNutrients(multiplyFactor.toDouble())
    }

    fun setPerUserSelectedGmData() {
        val servingQuantity = quantity_editText.text.toString().toDouble()
        var multiplyFactor = (servingQuantity / servingsOrGramValue)

        protein_textView.text = "${roundingFormat.format(proteinPer100Gm * multiplyFactor)} gm"

        carbs_textView.text = "${roundingFormat.format(carbsPer100Gm * multiplyFactor)} gm"

        fat_textView.text = "${roundingFormat.format(fatPer100Gm * multiplyFactor)} gm"

        kcal_textView.text = "${roundingFormat.format(energyPer100Gm * multiplyFactor)} kcal"

    }

    //    var protein = 0.0
//    var carbs = 0.0
//    var fats = 0.0
    private lateinit var viewModel: DietViewModel
    lateinit var db: AppDatabase
    private var mealType = ""
    private lateinit var nutrients: java.util.ArrayList<FoodNutrient>
    private var foodName = ""
    private var foodId = 0
    private var isCustomFood = false
    var measurement: Int = 0
    var servingsOrGramValue = 0.0
    private lateinit var internetSnackbar: Snackbar
    fun initialize() {

        internetSnackbar = Snackbar.make(parentRl, "Internet Disconnected", Snackbar.LENGTH_LONG)
                .setAction("CLOSE") { }
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))


        globalValidator = Validator(this);

        globalValidator.setValidationListener(this);

        roundingFormat.roundingMode = RoundingMode.CEILING

        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(DietViewModel()::class.java)


        isCustomFood = intent.getBooleanExtra(AppConstants.IS_CUSTOM_FOOD, false)
        foodName = intent.getStringExtra(AppConstants.FOOD_NAME)!!
        mealType = intent.getStringExtra(AppConstants.MEAL_TYPE)!!



        if (!isCustomFood) {

            nutrients = intent.getParcelableArrayListExtra(AppConstants.NUTRIENTS)!!
            foodId = intent.getIntExtra(AppConstants.FOOD_ID, 366922)

            val foodNutrientList = nutrients.map { itr ->
                FoodNutrientDbModel(
                        id = 0,
                        fdcId = foodId,
                        nutrientId = itr.nutrientId,
                        nutrientName = itr.nutrientName,
                        unitName = itr.unitName,
                        value = itr.value
                )
            }
            viewModel.getFoodNutrientsById(db, foodId)
            viewModel.foodNutrientDbModel.observe(this, Observer {
                if (it.isEmpty()) {
                    viewModel.insertAllFoodNutrient(db, foodNutrientList)
                    viewModel.insertLastSearchedFood(db, LastSearchedFoods(foodId, foodName,
                            proteinPer100Gm, fatPer100Gm, carbsPer100Gm, energyPer100Gm))
                }
            })

            viewModel.insertedRowLong.observe(this, Observer {
                it
            })
            viewModel.insertedRowLongList.observe(this, Observer {
                it
            })

            // Spinner Drop down elements
            val categories: MutableList<String> = ArrayList()
            categories.add("Gram")


            // Creating adapter for spinner
            val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // attaching data adapter to spinner
            spinner.adapter = dataAdapter

            var nutrient = nutrients.filter { it.nutrientId == AppConstants.PROTEIN_ID }
            if (nutrient.isEmpty()) {
                nutrient = nutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.PROTEIN) }
            }
            proteinPer100Gm = nutrient[0].value
            protein_textView.text = "${nutrient[0].value} ${nutrient[0].unitName.toLowerCase()}"

            nutrient = nutrients.filter { it.nutrientId == AppConstants.CARBS_ID }
            if (nutrient.isEmpty()) {
                nutrient = nutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.CARBS) }
            }
            carbsPer100Gm = nutrient[0].value
            carbs_textView.text = "${nutrient[0].value} ${nutrient[0].unitName.toLowerCase()}"

            nutrient = nutrients.filter { it.nutrientId == AppConstants.FAT_ID }
            if (nutrient.isEmpty()) {
                nutrient = nutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.FAT) }
            }
            fatPer100Gm = nutrient[0].value
            fat_textView.text = "${nutrient[0].value} ${nutrient[0].unitName.toLowerCase()}"

            nutrient = nutrients.filter { it.nutrientId == AppConstants.ENERGY_ID }
            if (nutrient.isEmpty()) {
                nutrient = nutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.CALORIES) }
            }
            energyPer100Gm = nutrient[0].value
            kcal_textView.text = "${nutrient[0].value} ${nutrient[0].unitName.toLowerCase()}"


            viewModel.checkIfInternetConnected()
            viewModel.isInternetConnected.observe(this, Observer {
                if (it) {
                    viewModel.fetchFoodDetails(foodId)
                    if (internetSnackbar.isShown) internetSnackbar.dismiss()
                } else {
                    internetSnackbar.show()
                }
            })



            viewModel.NewSingleFood.observe(this, Observer {
                singleFood = it
                categories.add("Serving")
                // Creating adapter for spinner
                val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // attaching data adapter to spinner
                spinner.adapter = dataAdapter
            })

        } else {
            proteinPer100Gm = intent.getDoubleExtra(AppConstants.PROTEIN, 0.0)
            carbsPer100Gm = intent.getDoubleExtra(AppConstants.CARBS, 0.0)
            fatPer100Gm = intent.getDoubleExtra(AppConstants.FAT, 0.0)
            energyPer100Gm = intent.getDoubleExtra(AppConstants.ENERGY, 0.0)
            servingsOrGramValue = intent.getDoubleExtra(AppConstants.SERVINGS_OR_GRAM_VALUE, 0.0)
            measurement = intent.getIntExtra(AppConstants.MEASUREMENT, 0)

            setCustomPlanVisibility()

            // Spinner Drop down elements
            val categories: MutableList<String> = ArrayList()
            if (measurement == AppConstants.SERVINGS_MEASUREMENT) {
                categories.add("Serving")
                quantity_editText.setText("1")
                quantity_textView.text = "$servingsOrGramValue gm"
            } else {
                categories.add("Gram")
                quantity_editText.setText(servingsOrGramValue.toString())
                quantity_textView.text = "$servingsOrGramValue gm"
            }

            // Creating adapter for spinner
            val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // attaching data adapter to spinner
            spinner.adapter = dataAdapter

            protein_textView.text = "$proteinPer100Gm gm"

            carbs_textView.text = "$carbsPer100Gm gm"

            fat_textView.text = "$fatPer100Gm gm"

            kcal_textView.text = "$energyPer100Gm kcal"

        }


        val blackColor = ContextCompat.getColor(this, android.R.color.black)
        val whiteColor = ContextCompat.getColor(this, android.R.color.white)

        val selectedBackGround = ContextCompat.getDrawable(this, R.drawable.blue_diet_button_background)
        val unSelectedBackGround = ContextCompat.getDrawable(this, R.drawable.diet_unselected_faint_border)

        breakfast_btn.setOnClickListener {
            breakfast_btn.background = selectedBackGround
            snacks_btn.background = unSelectedBackGround
            lunch_btn.background = unSelectedBackGround
            dinner_btn.background = unSelectedBackGround

            breakfast_btn.setTextColor(whiteColor)
            snacks_btn.setTextColor(blackColor)
            lunch_btn.setTextColor(blackColor)
            dinner_btn.setTextColor(blackColor)
        }

        snacks_btn.setOnClickListener {
            snacks_btn.background = selectedBackGround
            breakfast_btn.background = unSelectedBackGround
            lunch_btn.background = unSelectedBackGround
            dinner_btn.background = unSelectedBackGround

            snacks_btn.setTextColor(whiteColor)
            breakfast_btn.setTextColor(blackColor)
            lunch_btn.setTextColor(blackColor)
            dinner_btn.setTextColor(blackColor)
        }

        lunch_btn.setOnClickListener {
            lunch_btn.background = selectedBackGround
            snacks_btn.background = unSelectedBackGround
            breakfast_btn.background = unSelectedBackGround
            dinner_btn.background = unSelectedBackGround

            lunch_btn.setTextColor(whiteColor)
            snacks_btn.setTextColor(blackColor)
            breakfast_btn.setTextColor(blackColor)
            dinner_btn.setTextColor(blackColor)
        }

        dinner_btn.setOnClickListener {
            lunch_btn.background = unSelectedBackGround
            snacks_btn.background = unSelectedBackGround
            breakfast_btn.background = unSelectedBackGround
            dinner_btn.background = selectedBackGround

            dinner_btn.setTextColor(whiteColor)
            lunch_btn.setTextColor(blackColor)
            snacks_btn.setTextColor(blackColor)
            breakfast_btn.setTextColor(blackColor)
        }

        foodName_textView.text = foodName
        when (mealType) {
            AppConstants.BREAKFAST -> {
                breakfast_btn.callOnClick()
            }
            AppConstants.LUNCH -> {
                lunch_btn.callOnClick()
            }
            AppConstants.DINNER -> {
                dinner_btn.callOnClick()
            }
            AppConstants.SNACKS -> {
                snacks_btn.callOnClick()
            }
        }

    }

    private fun showSnackBar() {
        Snackbar.make(parentRl, "Internet Disconnected", Snackbar.LENGTH_LONG)
                .setAction("CLOSE") { }
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .show()
    }

    private fun setCustomPlanVisibility() {
        otherNutrient_tv.visibility = View.INVISIBLE
        nutrients_linearLayout.visibility = View.INVISIBLE
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed();
        }
        return true
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            (error.view as EditText).error = error.getCollatedErrorMessage(this)
        }
    }

    override fun onValidationSucceeded() {
        addFood()
    }
}