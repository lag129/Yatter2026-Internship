package com.dmm.bootcamp.yatter.auth

import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenPreferences: TokenPreferences) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val token = tokenPreferences.getAccessToken()

    val authenticatedRequest = if (!token.isNullOrEmpty()) {
      request.newBuilder()
        .addHeader("Authentication", "username $token")
        .build()
    } else {
      request
    }

    return chain.proceed(authenticatedRequest)
  }
}
