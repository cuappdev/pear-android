package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.User
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.getAllMajors
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class CreateProfileActivity : AppCompatActivity() {

    // variables to keep track if editTexts are filled out
    private var majorFilled = false
    private var hometownFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        CoroutineScope(Dispatchers.Main).launch {
            val getUserEndpoint = Endpoint.getUser()
            val userTypeToken = object : TypeToken<ApiResponse<User>>() {}.type
            val user = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<User>>(
                    getUserEndpoint.okHttpRequest(),
                    userTypeToken
                )
            }!!.data
            demographicsHeader.text = getString(R.string.demographics_header, user.firstName)
            // pre-fills existing user profile information
            if (!user.hometown.isNullOrBlank()) {
                hometownEditText.setText(user.hometown)
                hometownFilled = true
            }
            if (!user.major.isNullOrBlank()) {
                majorACTV.setText(user.major)
                majorFilled = true
            }
            // nextButton is disabled until user has filled out all required info
            if (user.hometown.isNullOrBlank() || user.major.isNullOrBlank()) {
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
                applicationContext,
                R.layout.profile_spinner_item,
                classArray
            )
            classSpinner.adapter = classArrayAdapter
            if (!user.graduationYear.isNullOrBlank()) classSpinner.setSelection(
                Integer.parseInt(
                    user.graduationYear
                ) - year
            )

            // Initializing the major AutoCompleteTextView
            val getMajorsEndpoint = Endpoint.getAllMajors()
            val majorsTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
            val majors = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<String>>>(
                    getMajorsEndpoint.okHttpRequest(),
                    majorsTypeToken
                )
            }!!.data
            val majorAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_dropdown_item_1line,
                majors
            )
            majorACTV.setAdapter(majorAdapter)

            // Initializing the pronoun spinner
            val pronoun = arrayOf(
                "He/Him/His", "She/Her/Hers", "They/Them/Theirs"
            )
            val pronounAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                R.layout.profile_spinner_item,
                pronoun
            )
            pronounSpinner.adapter = pronounAdapter
            pronounSpinner.setSelection(
                when (user.pronouns) {
                    pronoun[0] -> 0
                    pronoun[1] -> 1
                    pronoun[2] -> 2
                    else -> 0
                }
            )
        }

        // monitor changes in major editText and enable button if both major and hometown != empty
        majorACTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                majorFilled = s.toString().trim().isNotEmpty()
                toggleNextButton()
            }
        })

        // monitor changes in major editText and enable button if both major and hometown != empty
        hometownEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                hometownFilled = s.toString().trim().isNotEmpty()
                toggleNextButton()
            }
        })

        nextButton.setOnClickListener {
            // TODO: Update profile in backend

            val intent = Intent(this, ClubInterestActivity::class.java)
            startActivity(intent)
            intent.putExtra("page", 1)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // checks whether the major and hometown fields are completed, enabling or disabling the next button accordingly
    private fun toggleNextButton() {
        if (majorFilled && hometownFilled) {
            nextButton.isEnabled = true
            nextButton.isClickable = true
        } else {
            nextButton.isEnabled = false
            nextButton.isClickable = false
        }
    }
}