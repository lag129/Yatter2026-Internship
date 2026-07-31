package com.dmm.bootcamp.yatter.usecase.impl.updateuser

import com.dmm.bootcamp.yatter.domain.repository.UserRepository
import com.dmm.bootcamp.yatter.usecase.updateuser.UpdateUserUseCase
import com.dmm.bootcamp.yatter.usecase.updateuser.UpdateUserUseCaseResult
import java.io.File

class UpdateUserUseCaseImpl(
  private val userRepository: UserRepository,
) : UpdateUserUseCase {

  override suspend fun execute(displayName: String, avatar: File?): UpdateUserUseCaseResult {
    val me = userRepository.findLoginUser(true)
      ?: return UpdateUserUseCaseResult.Failure.NotLoggedIn
    userRepository.update(
      me = me,
      newDisplayName = displayName,
      newAvatar = avatar,
      newHeader = null,
      newNote = null,
    )

    return UpdateUserUseCaseResult.Success
  }
}
