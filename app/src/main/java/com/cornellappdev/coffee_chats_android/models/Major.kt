package com.cornellappdev.coffee_chats_android.models
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Major(
    val id: Int,
    val name: String
) : Parcelable