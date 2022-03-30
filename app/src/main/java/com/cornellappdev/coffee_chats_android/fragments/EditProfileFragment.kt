package com.cornellappdev.coffee_chats_android.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File
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

            // Initializing the pronoun spinner
            val pronoun = arrayOf("He/Him/His", "She/Her/Hers", "They/Them/Theirs")
            val pronounAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
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

        upload_image.setOnClickListener { uploadImage() }

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

    /** Checks for permissions and allows user to pick image if permissions granted */
    private fun uploadImage() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionsGranted(permissions)) {
            pickImage()
        } else {
            requestPermissions(permissions, PICK_IMAGE_CODE)
        }
    }

    /** True if all permissions are granted */
    private fun permissionsGranted(permissions: Array<String>): Boolean {
        val c = requireContext()
        // checks each permission and returns true if all permissions granted results are true
        return permissions
            .map { p -> (checkSelfPermission(c, p) == PackageManager.PERMISSION_GRANTED) }
            .all { b -> b }
    }

    /** Allows user to pick an image, and optionally crop it */
    private fun pickImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1) //You can skip this for free form aspect ratio)
            .start(requireContext(), this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PICK_IMAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.image_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val image = BitmapFactory.decodeFile(result.uri.path)
                Glide
                    .with(this)
                    .load(image)
                    .centerInside()
                    .circleCrop()
                    .into(user_image)
                val picture = File(result.uri.path!!)
                // compress image
                lifecycleScope.launch {
                    val compressedImageFile =
                        Compressor.compress(requireContext(), picture) {
                            resolution(250, 250)
                            format(Bitmap.CompressFormat.PNG)
                            size(2_097_152) // 2 MB
                        }
                    val byteArray = compressedImageFile.readBytes()
                    bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(byteArray))
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d("CROP_IMAGE_ERROR", error.toString())
                Toast.makeText(
                    requireContext(),
                    getString(R.string.image_upload_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        val pronouns = pronounSpinner.selectedItem as String
        val graduationYear =
            if (classSpinner.selectedItem as String == gradStudent) gradStudent
            else (classSpinner.selectedItemPosition + year).toString()
        val major = majorACTV.text.toString()
        val majorIndex = allMajorsList.firstOrNull { it.name == major }?.id
        val hometown = hometownEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            bitmap?.let { updateProfilePic(it) }
            val demographics = Demographics(
                pronouns,
                graduationYear,
                if (majorIndex != null) listOf(majorIndex) else emptyList(),
                hometown,
                null
            )
            val updateDemographicsResponse = updateDemographics(demographics)
            if (updateDemographicsResponse == null || !updateDemographicsResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_CODE = 42
    }
}