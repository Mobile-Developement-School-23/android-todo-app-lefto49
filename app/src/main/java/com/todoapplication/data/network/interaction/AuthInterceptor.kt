package com.todoapplication.data.network.interaction

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private val token = "psyches"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        return chain.proceed(request)
    }
}