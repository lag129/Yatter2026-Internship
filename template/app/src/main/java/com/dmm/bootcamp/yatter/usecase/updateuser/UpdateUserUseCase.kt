package com.dmm.bootcamp.yatter.usecase.updateuser

import java.io.File

interface UpdateUserUseCase {
  suspend fun execute(
    displayName: String,
    avatar: File?,
    note: String
  ): UpdateUserUseCaseResult
}
