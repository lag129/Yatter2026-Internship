package com.dmm.bootcamp.yatter.infra.domain.repository

import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.UserId
import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.domain.model.YweetId
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.infra.domain.converter.YweetConverter
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import remote.apis.TimelinesApi
import remote.apis.YweetsApi
import remote.models.AddYweetRequest
import remote.models.AttachmentRequest
import retrofit2.Response
import java.net.URL
import java.time.OffsetDateTime
import remote.models.User as ApiUser
import remote.models.Yweet as ApiYweet

class YweetRepositoryImplSpec {
  private val publicTimelinesApi = mockk<TimelinesApi>()
  private val homeTimelinesApi = mockk<TimelinesApi>()
  private val yweetsApi = mockk<YweetsApi>()
  private val subject = YweetRepositoryImpl(
    publicTimelinesApi,
    homeTimelinesApi,
    yweetsApi,
  )

  @Test
  fun getPublicTimelineFromApi() = runTest {
    val apiYweetList = listOf(
      ApiYweet(
        id = 1,
        user = ApiUser(
          id = 1,
          username = "username",
          createdAt = OffsetDateTime.now(),
          followersCount = 200,
          followingCount = 100,
          displayName = "display name",
          note = "note",
          avatar = "https://www.google.com",
          header = "https://www.google.com",
        ),
        content = "content",
        createdAt = OffsetDateTime.now(),
        imageAttachments = emptyList(),
      )
    )

    val expect = listOf(
      Yweet(
        id = YweetId(value = "1"),
        user = User(
          id = UserId("1"),
          username = Username("username"),
          displayName = "display name",
          note = "note",
          avatar = URL("https://www.google.com"),
          header = URL("https://www.google.com"),
          followingCount = 100,
          followerCount = 200,
        ),
        content = "content",
        attachmentImageList = emptyList()
      )
    )

    coEvery {
      publicTimelinesApi.findPublicTimelines(any(), any(), any())
    } returns Response.success(apiYweetList)

    val result: List<Yweet> = subject.findAllPublic()

    coVerify {
      publicTimelinesApi.findPublicTimelines(any(), any(), any())
    }

    assertThat(result).isEqualTo(expect)
  }

  @Test
  fun postYweetWhenLoggedIn() = runTest {
    val loginUsername = "token"
    val content = "content"

    val apiYweet = ApiYweet(
      id = 1,
      user = ApiUser(
        id = 1,
        username = loginUsername,
        createdAt = OffsetDateTime.now(),
        followersCount = 0,
        followingCount = 0,
        displayName = "",
        note = null,
        avatar = "https://www.google.com",
        header = "https://www.google.com",
      ),
      content = content,
      createdAt = OffsetDateTime.now(),
      imageAttachments = emptyList(),
    )

    coEvery {
      yweetsApi.addYweet(any())
    } returns Response.success(apiYweet)

    val expect = YweetConverter.convertFromApiModel(apiYweet)

    val result = subject.create(
      content,
      emptyList(),
      emptyList(),
    )

    assertThat(result).isEqualTo(expect)

    coVerify {
      yweetsApi.addYweet(
        AddYweetRequest(
          yweet = content,
          images = emptyList(),
        ),
      )
    }
  }

  @Test
  fun postYweetWithImageIds() = runTest {
    val loginUsername = "token"
    val content = "content"
    val imageIds = listOf(1, 2)

    val apiYweet = ApiYweet(
      id = 1,
      user = ApiUser(
        id = 1,
        username = loginUsername,
        createdAt = OffsetDateTime.now(),
        followersCount = 0,
        followingCount = 0,
        displayName = "",
        note = null,
        avatar = "https://www.google.com",
        header = "https://www.google.com",
      ),
      content = content,
      createdAt = OffsetDateTime.now(),
      imageAttachments = emptyList(),
    )

    coEvery {
      yweetsApi.addYweet(any())
    } returns Response.success(apiYweet)

    val expect = YweetConverter.convertFromApiModel(apiYweet)

    val result = subject.create(
      content,
      emptyList(),
      imageIds,
    )

    assertThat(result).isEqualTo(expect)

    coVerify {
      yweetsApi.addYweet(
        AddYweetRequest(
          yweet = content,
          images = listOf(
            AttachmentRequest(imageId = 1, description = ""),
            AttachmentRequest(imageId = 2, description = ""),
          ),
        ),
      )
    }
  }

  @Test
  fun postYweetWhenApiReturnsNull() = runTest {
    val content = "content"

    coEvery {
      yweetsApi.addYweet(any())
    } returns Response.success(null)

    var error: Throwable? = null
    var result: Yweet? = null

    try {
      result = subject.create(
        content,
        emptyList(),
        emptyList(),
      )
    } catch (e: Exception) {
      error = e
    }

    assertThat(result).isNull()
    assertThat(error).isNotNull()
  }
}
