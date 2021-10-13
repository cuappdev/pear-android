package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.models.SocialMedia
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.updateOnboardingStatus
import com.cornellappdev.coffee_chats_android.networking.updateSocialMedia
import kotlinx.android.synthetic.main.fragment_social_media.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SocialMediaFragment : Fragment(), OnFilledOutObservable {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_social_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        callback!!.onSelectionEmpty()
        CoroutineScope(Dispatchers.Main).launch {
            val user = getUser()
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    toggleSaveButton()
                }
            }
            instagramEditText.addTextChangedListener(textWatcher)
            facebookEditText.addTextChangedListener(textWatcher)
            instagramEditText.setText(user.instagramUsername)
            facebookEditText.setText(user.facebookUrl)
        }
    }

    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    private fun toggleSaveButton() {
        if (instagramEditText.text.isNotEmpty() || facebookEditText.text.isNotEmpty()) {
            callback!!.onFilledOut()
        } else {
            callback!!.onSelectionEmpty()
        }
    }

    override fun saveInformation() {
        val socialMedia = SocialMedia(
            facebookEditText.text.toString().trim(),
            instagramEditText.text.toString().trim()
        )
        CoroutineScope(Dispatchers.Main).launch {
            val updateSocialMediaResponse = updateSocialMedia(socialMedia)
            if (updateSocialMediaResponse == null || !updateSocialMediaResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
            // let backend know user has finished onboarding
            updateOnboardingStatus(true)
        }
    }
}