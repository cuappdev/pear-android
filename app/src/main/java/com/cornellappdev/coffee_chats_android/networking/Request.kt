package com.cornellappdev.coffee_chats_android.networking

import android.util.Log
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

object Request {
    val httpClient = OkHttpClient()

    suspend inline fun <reified T> makeRequest(request: okhttp3.Request, typeToken: Type): T? {
        val response = httpClient.newCall(request).await()
        val responseBody = response.body
        val responseBodyString = responseBody?.string() ?: ""
        Log.d("BACKEND_RESPONSE_CODE", response.code.toString())
        Log.d("BACKEND_RESPONSE", responseBodyString)
        val responseBodyJSON = Gson()

        return responseBodyJSON.fromJson<T>(responseBodyString, typeToken)
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