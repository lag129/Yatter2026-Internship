package com.dmm.bootcamp.yatter.usecase.impl.register

import com.dmm.bootcamp.yatter.domain.model.RegisterPassword
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.repository.UserRepository
import com.dmm.bootcamp.yatter.domain.service.LoginService
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences
import com.dmm.bootcamp.yatter.usecase.register.RegisterUserUseCase
import com.dmm.bootcamp.yatter.usecase.register.RegisterUserUseCaseResult

class RegisterUserUseCaseImpl(
  private val userRepository: UserRepository,
  private val loginUserPreferences: LoginUserPreferences,
  private val loginService: LoginService,
) : RegisterUserUseCase {
  override suspend fun execute(username: String, password: String): RegisterUserUseCaseResult {
    if (username == "") {
      return RegisterUserUseCaseResult.Failure.EmptyUsername
    }
    if (password == "") {
      return RegisterUserUseCaseResult.Failure.EmptyPassword
    }
    val newUsername = Username(username)
    val newPassword = RegisterPassword(password)
    if (!newPassword.validate()) {
      return RegisterUserUseCaseResult.Failure.InvalidPassword
    }

    runCatching {
      val me = userRepository.create(
        newUsername,
        newPassword,
      )
      loginUserPreferences.putUsername(me.username.value)
    }.onFailure {
      return RegisterUserUseCaseResult.Failure.CreateUserError(it)
    }

    runCatching {
      loginService.execute(
        newUsername,
        newPassword,
      )
    }.onFailure {
      return RegisterUserUseCaseResult.Failure.LoginError(it)
    }

    return RegisterUserUseCaseResult.Success
  }
}
