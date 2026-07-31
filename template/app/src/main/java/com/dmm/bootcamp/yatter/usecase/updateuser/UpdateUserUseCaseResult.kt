package com.dmm.bootcamp.yatter.usecase.updateuser

sealed class UpdateUserUseCaseResult {
  object Success : UpdateUserUseCaseResult()
  sealed class Failure : UpdateUserUseCaseResult() {
    object NotLoggedIn : Failure()
  }
}
