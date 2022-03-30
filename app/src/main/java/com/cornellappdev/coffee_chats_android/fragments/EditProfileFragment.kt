package com.cornellappdev.coffee_chats_android.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.OnFilledOutListener
import com.cornellappdev.coffee_chats_android.OnFilledOutObservable
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.Demographics
import com.cornellappdev.coffee_chats_android.models.Major
import com.cornellappdev.coffee_chats_android.models.User
import com.cornellappdev.coffee_chats_android.networking.getAllMajors
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.updateDemographics
import com.cornellappdev.coffee_chats_android.networking.updateProfilePic
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditProfileFragment : Fragment(), OnFilledOutObservable {
    // variables to keep track if editTexts are filled out
    private var majorFilled = false
    private var hometownFilled = false
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private var bitmap: Bitmap? = null
    private lateinit var gradStudent: String

    private lateinit var user: User

    private lateinit var allMajorsList: List<Major>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initializing the class spinner
        val classArray = ArrayList<String>()
        val month = Calendar.getInstance().get(Calendar.MONTH)
        if (month >= 6) year++  // sets minimum graduation year to next year
        for (i in 0..4) {
            classArray.add("Class of " + (year + i))
        }
        gradStudent = requireContext().getString(R.string.grad_student)
        classArray.add(gradStudent)
        val classArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.profile_spinner_item,
            classArray
        )
        classSpinner.adapter = classArrayAdapter

        CoroutineScope(Dispatchers.Main).launch {
            user = getUser()
            // pre-fills existing user profile information
            if (!user.profilePicUrl.isNullOrBlank()) {
                Glide.with(requireContext()).load(user.profilePicUrl).centerInside().circleCrop()
                    .into(user_image)
            }

            if (!user.hometown.isNullOrBlank()) {
                hometownEditText.setText(user.hometown)
                hometownFilled = true
            }
            if (user.majors.isNotEmpty()) {
                majorACTV.setText(user.majors.first().name)
                majorFilled = true
            }
            // nextButton is disabled until user has filled out all required info
            if (user.hometown.isNullOrBlank() || user.majors.isEmpty()) {
                callback!!.onSelectionEmpty()
            }

            if (!user.graduationYear.isNullOrBlank()) {
                classSpinner.setSelection(
                    when (user.graduationYear) {
                        gradStudent -> classArray.size - 1
                        else -> Integer.parseInt(user.graduationYear!!) - year
                    }
                )
            }

            // Initializing the major AutoCompleteTextView
            allMajorsList = getAllMajors()
            val majorAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                allMajorsList.map { it.name }
            )
            majorACTV.setAdapter(majorAdapter)

            // Initializing the pronoun EditText
            if (!user.pronouns.isNullOrBlank()) {
                pronounEditText.setText(user.pronouns)
            }
        }
        upload_image.setOnClickListener {
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra("crop", "true")
                putExtra("scale", "true")
                putExtra("outputX", 250)
                putExtra("outputY", 250)
                putExtra("aspectX", 1)
                putExtra("aspectY", 1)
                putExtra("return-data", true)
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivityForResult(this, REQUEST_IMAGE_GET)
                }
            }
        }

        // monitor changes in major editText and enable button if both major and hometown != empty
        majorACTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                majorFilled = s.toString().trim().isNotEmpty()
                toggleNextButton()
            }
        })

        // monitor changes in major editText and enable button if both major and hometown != empty
        hometownEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hometownFilled = s.toString().trim().isNotEmpty()
                toggleNextButton()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_GET) {
            bitmap = (data?.getParcelableExtra("data") as Bitmap?) ?: data?.data?.let {
                return@let MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    it
                )
            }
            Glide.with(this).load(bitmap).centerInside().circleCrop().into(user_image)
        }
    }

    // checks whether the major and hometown fields are completed, enabling or disabling the next button accordingly
    private fun toggleNextButton() {
        if (majorFilled && hometownFilled) {
            callback!!.onFilledOut()
        } else {
            callback!!.onSelectionEmpty()
        }
    }

    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        val pronouns = pronounEditText.text.toString()
        val graduationYear =
            if (classSpinner.selectedItem as String == gradStudent) gradStudent
            else (classSpinner.selectedItemPosition + year).toString()
        val major = majorACTV.text.toString()
        val majorIndex = allMajorsList.firstOrNull { it.name == major }?.id
        val hometown = hometownEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val photoUrl = bitmap?.let {
                return@let updateProfilePic(it)?.data
            }
            val demographics = Demographics(
                pronouns,
                graduationYear,
                if (majorIndex != null) listOf(majorIndex) else emptyList(),
                hometown,
                photoUrl
            )
            val updateDemographicsResponse = updateDemographics(demographics)
            if (updateDemographicsResponse == null || !updateDemographicsResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_GET = 42
    }
}