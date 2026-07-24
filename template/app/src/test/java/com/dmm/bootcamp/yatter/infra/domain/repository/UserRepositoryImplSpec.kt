package com.dmm.bootcamp.yatter.infra.domain.repository

import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.service.GetLoginUsernameService
import com.dmm.bootcamp.yatter.infra.domain.converter.UserConverter
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import remote.apis.UsersApi
import retrofit2.Response
import java.time.OffsetDateTime
import remote.models.User as ApiUser

class UserRepositoryImplSpec {
  private val usersApi = mockk<UsersApi>()
  private val getLoginUsernameService = mockk<GetLoginUsernameService>()
  private val subject = UserRepositoryImpl(usersApi, getLoginUsernameService)

  @Test
  fun findByUsername() = runTest {
    val username = Username("username")
    val apiUser = ApiUser(
      id = 1,
      username = "username",
      displayName = "display name",
      note = null,
      avatar = "https://www.google.com",
      header = "https://www.google.com",
      followingCount = 0,
      followersCount = 0,
      createdAt = OffsetDateTime.now(),
    )

    val expect = UserConverter.convertFromApiModel(apiUser)

    coEvery {
      usersApi.findUserByUsername(any())
    } returns Response.success(apiUser)

    val result = subject.findByUsername(username, disableCache = false)

    coVerify {
      usersApi.findUserByUsername(username.value)
    }

    assertThat(result).isEqualTo(expect)
  }

  @Test
  fun findLoginUser() = runTest {
    val username = "username"
    val apiUser = ApiUser(
      id = 1,
      username = "username",
      displayName = "display name",
      note = null,
      avatar = "https://www.google.com",
      header = "https://www.google.com",
      followingCount = 0,
      followersCount = 0,
      createdAt = OffsetDateTime.now(),
    )
    val expect = UserConverter.convertFromApiModel(apiUser)

    coEvery {
      getLoginUsernameService.execute()
    } returns Username(username)
    coEvery {
      usersApi.findUserByUsername(any())
    } returns Response.success(apiUser)

    val result = subject.findLoginUser(disableCache = false)

    coVerify {
      usersApi.findUserByUsername(username)
    }
    coVerify {
      getLoginUsernameService.execute()
    }

    assertThat(result).isEqualTo(expect)
  }
}
