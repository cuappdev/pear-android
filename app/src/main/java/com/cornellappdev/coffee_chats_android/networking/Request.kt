package com.cornellappdev.coffee_chats_android.networking

import android.util.Log
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object Request {
    val httpClient = OkHttpClient()
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    suspend inline fun <reified T> makeRequest(request: okhttp3.Request, typeToken: Type): T? {
        Log.d("BACKEND_REQUEST", request.url.toString())
        Log.d("BACKEND_REQUEST_METHOD", request.method)
        Log.d("BACKEND_REQUEST_HEADERS", request.headers.toString())
        val response = httpClient.newCall(request).await()
        val responseBody = response.body
        val responseBodyString = responseBody?.string() ?: ""
        Log.d("BACKEND_RESPONSE_CODE", response.code.toString())
        Log.d("BACKEND_RESPONSE", responseBodyString)
        val responseBodyJSON = Gson()

        return responseBodyJSON.fromJson<T>(responseBodyString, typeToken)
    }

    suspend inline fun <T> makeMoshiRequest(
        request: okhttp3.Request,
        typeToken: Type
    ): ApiResponse<T>? {
        Log.d("BACKEND_REQUEST", request.url.toString())
        Log.d("BACKEND_REQUEST_METHOD", request.method)
        Log.d("BACKEND_REQUEST_HEADERS", request.headers.toString())
        val response = httpClient.newCall(request).await()
        val responseBody = response.body
        val responseBodySource = responseBody?.source()
        Log.d("BACKEND_RESPONSE_CODE", response.code.toString())
        val responseString = responseBodySource!!.readString(Charset.defaultCharset())
        Log.d("BACKEND_RESPONSE_STRING", responseString)
        val apiResponseType = newParameterizedType(ApiResponse::class.java, typeToken)
        val adapter: JsonAdapter<ApiResponse<T>> = moshi.adapter(apiResponseType)
        // Can read directly from source if it hasn't already been read from for logging
        //        val result = adapter.fromJson(responseBodySource!!)
        val result = adapter.fromJson(responseString)
        Log.d("BACKEND_RESULT_TYPE", if (result != null) result::class.simpleName ?: "" else "")
        return result
    }

    /**
     * Suspend extension that allows suspend [Call] inside coroutine.
     *
     * @return Result of request or throw exception
     */
    suspend fun Call.await(recordStackTrace: Boolean = true): Response {
        val stackTrace =
            if (recordStackTrace) IOException("Exception occurred while awaiting Call.") else null
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Don't bother with resuming the continuation if it is already cancelled.
                    if (stackTrace != null) {
                        stackTrace.initCause(e)
                        continuation.resumeWithException(stackTrace)
                    } else {
                        continuation.resumeWithException(e)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    cancel()
                } catch (ex: Throwable) {
                    //Ignore cancel exception
                }
            }
        }
    }
}