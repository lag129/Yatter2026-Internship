package com.dmm.bootcamp.yatter.usecase.impl

import android.accounts.AuthenticatorException
import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.UserId
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.domain.model.YweetId
import com.dmm.bootcamp.yatter.domain.repository.ImageRepository
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.usecase.impl.post.PostYweetUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCaseResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import java.net.URL

class PostYweetUseCaseImplSpec {
  private val yweetRepository = mockk<YweetRepository>()
  private val imageRepository = mockk<ImageRepository>()
  private val subject = PostYweetUseCaseImpl(yweetRepository, imageRepository)

  @Test
  fun postYweetWithSuccess() = runTest {
    val content = "content"

    val yweet = Yweet(
      id = YweetId(value = ""),
      user = User(
        id = UserId(value = ""),
        username = Username(value = ""),
        displayName = null,
        note = null,
        avatar = URL("https://www.google.com"),
        header = URL("https://www.google.com"),
        followingCount = 0,
        followerCount = 0,
      ),
      content = content,
      attachmentImageList = listOf(),
    )

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } returns yweet

    val result = subject.execute(
      content,
      emptyList(),
    )

    coVerify {
      yweetRepository.create(
        content,
        emptyList(),
        emptyList(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Success)
  }

  @Test
  fun postYweetWithEmptyContent() = runTest {
    val content = ""

    val result = subject.execute(
      content,
      emptyList(),
    )

    coVerify(inverse = true) {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.EmptyContent)
  }

  @Test
  fun postYweetWithNotLoggedIn() = runTest {
    val content = "content"

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } throws AuthenticatorException()

    val result = subject.execute(
      content,
      emptyList(),
    )


    coVerify {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.NotLoggedIn)
  }

  @Test
  fun postYweetWithOtherError() = runTest {
    val content = "content"
    val exception = Exception()

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } throws exception

    val result = subject.execute(
      content,
      emptyList(),
    )


    coVerify {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.OtherError(exception))
  }

  @Test
  fun postYweetWithAttachments() = runTest {
    val content = "content with image"
    val file = File("/tmp/test.jpg")
    val imageId = 42

    val yweet = Yweet(
      id = YweetId(value = ""),
      user = User(
        id = UserId(value = ""),
        username = Username(value = ""),
        displayName = null,
        note = null,
        avatar = URL("https://www.google.com"),
        header = URL("https://www.google.com"),
        followingCount = 0,
        followerCount = 0,
      ),
      content = content,
      attachmentImageList = listOf(),
    )

    coEvery {
      imageRepository.upload(any())
    } returns imageId

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } returns yweet

    val result = subject.execute(
      content,
      listOf(file),
    )

    coVerify {
      imageRepository.upload(file)
    }

    coVerify {
      yweetRepository.create(
        content,
        listOf(file),
        listOf(imageId),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Success)
  }
}
