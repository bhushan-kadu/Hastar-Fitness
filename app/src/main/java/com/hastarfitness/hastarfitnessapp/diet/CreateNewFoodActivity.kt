package com.hastarfitness.hastarfitnessapp.diet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.annotation.DecimalMax
import com.mobsandgeeks.saripaar.annotation.DecimalMin
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.hastarfitness.hastarfitnessapp.R
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.CustomFoodsDbModel
import kotlinx.android.synthetic.main.activity_create_new_food.*

class CreateNewFoodActivity : AppCompatActivity(), Validator.ValidationListener  {

    lateinit var validator: Validator

    @NotEmpty(message = "Please enter name")
    lateinit var name: TextInputEditText

    @NotEmpty
    @DecimalMax(1000.0, message = "Please Select maximum 1000 kcal")
    @DecimalMin(1.0, message = "Please Select minimum 1 kcal")
    lateinit var calories: TextInputEditText

    @NotEmpty
    @DecimalMax(1000.0, message = "Please Select maximum 1000 gm")
    @DecimalMin(1.0, message = "Please Select minimum 1 gm")
    lateinit var servingOrGram: TextInputEditText

    @NotEmpty
    @DecimalMax(1000.0, message = "Please Select maximum 1000 gm")
    @DecimalMin(1.0, message = "Please Select minimum 1 gm")
    lateinit var protein: TextInputEditText

    @NotEmpty
    @DecimalMax(1000.0, message = "Please Select maximum 1000 gm")
    @DecimalMin(1.0, message = "Please Select minimum 1 gm")
    lateinit var carbs: TextInputEditText

    @NotEmpty
    @DecimalMax(1000.0, message = "Please Select maximum 1000 gm")
    @DecimalMin(1.0, message = "Please Select minimum 1 gm")
    lateinit var fat: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_food)

        initialize()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Create Food"

        addFood_button.setOnClickListener {
            validator.validate()
        }

        gram_radioBtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servingsUnit_View.hint = "grams"
                    servingsOrGrams_editText.setText("")
                }
            }
        })
        serving_radioBtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    servingsUnit_View.hint = "per serving grams"
                }
            }
        })
    }

    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase

    fun initialize(){
        name = name_editText
        calories = calories_editText
        servingOrGram = servingsOrGrams_editText
        protein = protein_editText
        carbs = carbs_editText
        fat = fat_editText

        validator = Validator(this)
        validator.setValidationListener(this)

        instantialteDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }

    private fun instantialteDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = (error.view as TextInputEditText)
                view.error = error.getCollatedErrorMessage(this)
        }
    }

    override fun onValidationSucceeded() {
        val name = name.text!!.toString().trim()
        val calories = calories.text!!.toString().toDouble()
        val measurement = if(serving_radioBtn.isChecked){
            0
        }else{
            1
        }
        val servingOrGram = servingOrGram.text.toString().toDouble()
        val protein = protein.text.toString().toDouble()
        val carbs = carbs.text.toString().toDouble()
        val fat = fat.text.toString().toDouble()

        viewModel.insertCustomFood(db, CustomFoodsDbModel(0, name, calories, measurement, servingOrGram, protein, carbs, fat))
        viewModel.insertedRowLong.observe(this, Observer {
            if(it>0){
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(this, "error occurred!", Toast.LENGTH_LONG).show()

            }
            onBackPressed()
        })
    }
}