package com.dmm.bootcamp.yatter.usecase.impl.login

import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.service.LoginService
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCase
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCaseResult

internal class LoginUseCaseImpl(
  private val loginService: LoginService,
  private val loginUserPreferences: LoginUserPreferences,
) : LoginUseCase {
  override suspend fun execute(username: Username, password: Password): LoginUseCaseResult {
    try {
      if (username.value.isBlank()) return LoginUseCaseResult.Failure.EmptyUsername
      if (password.value.isBlank()) return LoginUseCaseResult.Failure.EmptyPassword

      loginService.execute(username, password)
      loginUserPreferences.putUsername(username.value)
      return LoginUseCaseResult.Success
    } catch (e: Exception) {
      return LoginUseCaseResult.Failure.OtherError(e)
    }
  }
}
