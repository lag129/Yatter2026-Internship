package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.service.LoginService
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import remote.apis.AuthApi
import remote.models.LoginRequest

class LoginServiceImpl(
  private val authApi: AuthApi,
  private val tokenPreferences: TokenPreferences,
) : LoginService {

  override suspend fun execute(username: Username, password: Password) = withContext(IO) {
    val loginRequest = LoginRequest(
      username = username.value,
      password = password.value,
    )
    val response = authApi.login(loginRequest)
    if (!response.isSuccessful) {
      throw Exception("Login failed: ${response.code()} ${response.message()}")
    }
    val token =
      response.body()?.username ?: throw Exception("Login failed: No username in response")
    tokenPreferences.putAccessToken(token)
  }
}
