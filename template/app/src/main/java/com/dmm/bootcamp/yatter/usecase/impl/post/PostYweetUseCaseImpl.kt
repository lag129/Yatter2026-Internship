package com.dmm.bootcamp.yatter.usecase.impl.post

import android.accounts.AuthenticatorException
import com.dmm.bootcamp.yatter.domain.repository.ImageRepository
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCase
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCaseResult
import java.io.File

class PostYweetUseCaseImpl(
  private val yweetRepository: YweetRepository,
  private val imageRepository: ImageRepository,
) : PostYweetUseCase {
  override suspend fun execute(
    content: String,
    attachmentList: List<File>,
  ): PostYweetUseCaseResult {
    if (content == "" && attachmentList.isEmpty()) {
      return PostYweetUseCaseResult.Failure.EmptyContent
    }

    return try {
      val imageIds = attachmentList.map { file ->
        imageRepository.upload(file)
      }

      yweetRepository.create(
        content = content,
        attachmentList = attachmentList,
        imageIds = imageIds,
      )

      PostYweetUseCaseResult.Success
    } catch (e: AuthenticatorException) {
      PostYweetUseCaseResult.Failure.NotLoggedIn
    } catch (e: Exception) {
      PostYweetUseCaseResult.Failure.OtherError(e)
    }
  }
}
