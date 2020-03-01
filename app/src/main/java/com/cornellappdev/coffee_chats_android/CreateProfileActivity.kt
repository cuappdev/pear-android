package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

        // variables to keep track if editTexts are filled out
        var majorFilled = false
        var hometownFilled = false

        // tries to retrieve User Profile from internal storage
        var profile = InternalStorage.readObject(this, "profile") as UserProfile
        demoTop.text = getString(R.string.demographics_header, profile.userName)
        Log.d("CreateProfileActivity", profile.userName)
        if (profile.hometown.isNotEmpty())  {
            hometownET.setText(profile.hometown)
            hometownFilled = true
        }
        if (profile.major.isNotEmpty()) {
            majorACTV.setText(profile.major)
            majorFilled = true
        }

        // nextButton is disabled until user has filled out all required info
        if (!hometownFilled || !majorFilled) {
            nextButton.isEnabled = false
            nextButton.isClickable = false
        }

        // Initializing the class spinner
        val classArray = ArrayList<String>()
        var year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        if (month >= 6) year++  // sets minimum graduation year to next year
        for (i in 0..4) {
            classArray.add("Class of " + (year + i))
        }

        val classArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.profile_spinner_item,
            classArray
        )
        classSpinner.adapter = classArrayAdapter
        if (profile.classOf != 0) classSpinner.setSelection(profile.classOf - year)

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
            "He/Him/His", "She/Her/Hers", "They/Them/Theirs"
        )
        val pronounAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.profile_spinner_item,
            pronoun
        )
        pronounSpinner.adapter = pronounAdapter
        when (profile.pronoun) {
            "He" -> pronounSpinner.setSelection(0)
            "She" -> pronounSpinner.setSelection(1)
            "They" -> pronounSpinner.setSelection(2)
            else -> pronounSpinner.setSelection(0)
        }

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
            // update profile in internal storage
            profile.classOf = classSpinner.selectedItemPosition + year
            profile.major = majorACTV.text.toString()
            profile.hometown = hometownET.text.toString()
            profile.pronoun = pronounSpinner.selectedItem.toString()
            InternalStorage.writeObject(this, "profile", profile as Object)

            val intent = Intent(this, ClubInterestActivity::class.java)
            startActivity(intent)
        }
    }
}