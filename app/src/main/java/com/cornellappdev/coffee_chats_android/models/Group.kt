package com.cornellappdev.coffee_chats_android.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Group(val id: Int, val name: String, @Json(name = "img_url") val imageUrl: String) :
    Parcelable