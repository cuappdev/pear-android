package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_profile.*
import java.util.*
import kotlin.collections.ArrayList


class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val name: String? = intent.extras.getString("name")
        if (name != null) {
            demoTop.text = getString(R.string.demographics_header, name)
        } else demoTop.text = getString(R.string.demographics_header_no_name)

        // Initializing the class spinner
        val classArray = ArrayList<String>()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        if (month <= 6) {
            for (i in 0..4) {
                classArray.add("Class of " + (year + i))
            }
        } else {
            for (i in 1..5) {
                classArray.add("Class of " + (year + i))
            }
        }

        val classArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.profile_spinner_item,
            classArray
        )
        classSpinner.adapter = classArrayAdapter

        // Initializing the major AutoCompleteTextView
        val majors = arrayOf(
             "Communication", "Cognitive Science", "Computer Science"
        )
        val majorAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            majors
        )
        majorACTV.setAdapter(majorAdapter)

        // Initializing the pronoun spinner
        val pronoun = arrayOf(
            "He", "She", "They"
        )
        val pronounAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.profile_spinner_item,
            pronoun
        )
        pronounSpinner.adapter = pronounAdapter
    }
}