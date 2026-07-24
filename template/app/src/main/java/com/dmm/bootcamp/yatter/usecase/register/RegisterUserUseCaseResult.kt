package com.dmm.bootcamp.yatter.usecase.register

sealed class RegisterUserUseCaseResult {
  object Success : RegisterUserUseCaseResult()
  sealed class Failure : RegisterUserUseCaseResult() {
    object EmptyUsername : Failure()
    object EmptyPassword : Failure()
    object InvalidPassword : Failure()
    data class CreateUserError(val throwable: Throwable) : Failure()
    data class LoginError(val throwable: Throwable) : Failure()
  }
}
