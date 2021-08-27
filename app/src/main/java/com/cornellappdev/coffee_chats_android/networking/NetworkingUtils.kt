package com.cornellappdev.coffee_chats_android.networking

import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

/** Function that should be called before any networking calls are made */
fun setUpNetworking(accessToken: String) {
    Log.d("ACCESS_TOKEN", accessToken)
    UserSession.currentAccessToken = accessToken
}

/** Retrieves non-list data */
suspend fun <T> getDataHelper(endpoint: Endpoint, typeToken: Type): T =
    withContext(Dispatchers.IO) {
        Request.makeMoshiRequest<T>(
            endpoint.okHttpRequest(),
            typeToken
        )
    }!!.data!!

/** Retrieves a list of data */
suspend fun <T> getListHelper(endpoint: Endpoint, typeToken: Type): List<T> =
    withContext(Dispatchers.IO) {
        Request.makeMoshiRequest<List<T>>(
            endpoint.okHttpRequest(),
            Types.newParameterizedType(List::class.java, typeToken)
        )
    }!!.data!!

/** Posts non-list data */
suspend fun <T> postDataHelper(endpoint: Endpoint, typeToken: Type): ApiResponse<T>? =
    withContext(Dispatchers.IO) {
        Request.makeMoshiRequest<T>(
            endpoint.okHttpRequest(),
            typeToken
        )
    }

// AUTH

suspend fun authenticateUser(idToken: String): UserSession =
    postDataHelper<UserSession>(Endpoint.authenticateUser(idToken), UserSession::class.java)!!.data!!

// PROFILE

suspend fun getUser(): User = getDataHelper(Endpoint.getSelfProfile(), User::class.java)

suspend fun updateDemographics(demographics: Demographics): ApiResponse<Demographics>? =
    postDataHelper(Endpoint.updateDemographics(demographics), Demographics::class.java)

// MAJORS

suspend fun getAllMajors(): List<Major> = getListHelper(Endpoint.getAllMajors(), Major::class.java)

// INTERESTS

suspend fun getAllInterests(): List<Interest> =
    getListHelper(Endpoint.getAllInterests(), Interest::class.java)

// GROUPS

suspend fun getAllGroups(): List<Group> =
    getListHelper(Endpoint.getAllGroups(), Group::class.java)