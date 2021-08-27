package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.Location
import com.cornellappdev.coffee_chats_android.models.SocialMedia
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.getUserSocialMedia
import com.cornellappdev.coffee_chats_android.networking.updateSocialMedia
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_social_media.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            val getUserSocialMediaEndpoint = Endpoint.getUserSocialMedia()
            val typeToken = object : TypeToken<ApiResponse<SocialMedia>>() {}.type
            val userSocialMedia = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<SocialMedia>>(
                    getUserSocialMediaEndpoint.okHttpRequest(),
                    typeToken
                )
            }!!.data
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    toggleSaveButton()
                }
            }
            instagramEditText.addTextChangedListener(textWatcher)
            facebookEditText.addTextChangedListener(textWatcher)
            instagramEditText.setText(userSocialMedia!!.instagram)
            facebookEditText.setText(userSocialMedia!!.facebook)
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
        val socialMedia = SocialMedia(facebookEditText.text.toString().trim(), instagramEditText.text.toString().trim())
        CoroutineScope(Dispatchers.Main).launch {
            val updateSocialMediaEndpoint = Endpoint.updateSocialMedia(socialMedia)
            val typeToken = object : TypeToken<ApiResponse<SocialMedia>>() {}.type
            val updateSocialMediaResponse = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Location>>>(
                    updateSocialMediaEndpoint.okHttpRequest(),
                    typeToken
                )
            }
            if (updateSocialMediaResponse == null || !updateSocialMediaResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}