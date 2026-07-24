package com.dmm.bootcamp.yatter.usecase.login

import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.Username

interface LoginUseCase {
  suspend fun execute(username: Username, password: Password): LoginUseCaseResult
}
