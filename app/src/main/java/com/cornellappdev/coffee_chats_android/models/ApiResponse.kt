package com.cornellappdev.coffee_chats_android.models

class ApiResponse<T>(val success: Boolean, val data: T, val timestamp: Long)