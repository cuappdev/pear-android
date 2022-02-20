package com.cornellappdev.coffee_chats_android.networking

import android.graphics.Bitmap
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

/** Post list data */
suspend fun <T> postListDataHelper(endpoint: Endpoint, typeToken: Type): ApiResponse<List<T>>? =
    withContext(Dispatchers.IO) {
        Request.makeMoshiRequest<List<T>>(
            endpoint.okHttpRequest(),
            Types.newParameterizedType(List::class.java, typeToken)
        )
    }

// AUTH

suspend fun authenticateUser(idToken: String): UserSession =
    postDataHelper<UserSession>(
        Endpoint.authenticateUser(idToken),
        UserSession::class.java
    )!!.data!!

// PROFILE

suspend fun getAllUsers(query: String = ""): List<PearUser> =
    getListHelper(Endpoint.getAllUsers(query), PearUser::class.java)

suspend fun getUser(): User = getDataHelper(Endpoint.getSelfProfile(), User::class.java)

suspend fun getUser(userId: Int): PearUser =
    getDataHelper(Endpoint.getUserProfile(userId), PearUser::class.java)

suspend fun updateDemographics(demographics: Demographics): ApiResponse<Demographics>? =
    postDataHelper(Endpoint.updateDemographics(demographics), Demographics::class.java)

suspend fun updateProfilePic(bitmap: Bitmap): ApiResponse<String>? =
    postDataHelper(Endpoint.updateProfilePic(bitmap), String::class.java)

// MAJORS

suspend fun getAllMajors(): List<Major> = getListHelper(Endpoint.getAllMajors(), Major::class.java)

// INTERESTS

suspend fun getAllInterests(): List<Interest> =
    getListHelper(Endpoint.getAllInterests(), Interest::class.java)

suspend fun updateInterests(interestIdsList: List<Int>): ApiResponse<List<Int>>? =
    postListDataHelper(Endpoint.updateInterests(interestIdsList), Integer::class.java)

// GROUPS

suspend fun getAllGroups(): List<Group> =
    getListHelper(Endpoint.getAllGroups(), Group::class.java)

suspend fun updateGroups(groupIdsList: List<Int>): ApiResponse<List<Int>>? =
    postListDataHelper(Endpoint.updateGroups(groupIdsList), Integer::class.java)

// PROMPTS

suspend fun getAllPrompts(): List<Prompt> =
    getListHelper(Endpoint.getAllPrompts(), Prompt::class.java)

suspend fun updatePrompts(promptList: List<Prompt>): ApiResponse<List<Prompt>>? =
    postListDataHelper(Endpoint.updatePrompts(promptList), Prompt::class.java)

// PURPOSES

suspend fun getAllPurposes(): List<Purpose> =
    getListHelper(Endpoint.getAllPurposes(), Purpose::class.java)

suspend fun updatePurposes(purposeIdsList: List<Int>): ApiResponse<List<Int>>? =
    postListDataHelper(Endpoint.updatePurposes(purposeIdsList), Integer::class.java)

// SOCIAL MEDIA

suspend fun updateSocialMedia(socialMedia: SocialMedia): ApiResponse<SocialMedia>? =
    postDataHelper(Endpoint.updateSocialMedia(socialMedia), SocialMedia::class.java)

// ONBOARDING STATUS

suspend fun updateOnboardingStatus(hasOnboarded: Boolean): ApiResponse<OnboardingStatus>? =
    postDataHelper(Endpoint.updateOnboardingStatus(hasOnboarded), OnboardingStatus::class.java)

// MATCHES

suspend fun getCurrentMatch(): SingleMatch? =
    getDataHelper(Endpoint.getCurrentMatch(), SingleMatch::class.java)

suspend fun getSelfMatches(userId: Int): List<PearUser> =
    getListHelper<DoubleMatch>(
        Endpoint.getSelfMatches(),
        DoubleMatch::class.java
    ).map { it.users.first { matchedUser -> matchedUser.id != userId } }
