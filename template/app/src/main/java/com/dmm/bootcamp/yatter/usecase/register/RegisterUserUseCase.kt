package com.dmm.bootcamp.yatter.usecase.register

interface RegisterUserUseCase {
  suspend fun execute(username: String, password: String): RegisterUserUseCaseResult
}
