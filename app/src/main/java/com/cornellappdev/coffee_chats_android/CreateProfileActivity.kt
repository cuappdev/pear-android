package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import kotlinx.android.synthetic.main.activity_create_profile.*
import java.util.*
import kotlin.collections.ArrayList


class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        // tries to retrieve User Profile from internal storage
        var profile = InternalStorage.readObject(this, "profile") as UserProfile
        demoTop.text = getString(R.string.demographics_header, profile.userName)

        // nextButton is disabled until user has filled out all required info
        nextButton.isEnabled = false
        nextButton.isClickable = false

        // variables to keep track if editTexts are filled out
        var majorFilled = false
        var hometownFilled = false

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

        // monitor changes in major editText and enable button if both major and hometown != empty
        majorACTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                majorFilled = s.toString().trim().isNotEmpty()
                if (majorFilled && hometownFilled) {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            }
        })

        // monitor changes in major editText and enable button if both major and hometown != empty
        hometownET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                hometownFilled = s.toString().trim().isNotEmpty()
                if (majorFilled && hometownFilled) {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            }
        })

        nextButton.setOnClickListener{
            val intent = Intent(this, ClubInterestActivity::class.java)
            startActivity(intent)
        }
    }
}