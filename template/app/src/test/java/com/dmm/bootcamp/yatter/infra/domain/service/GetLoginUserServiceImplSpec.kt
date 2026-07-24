package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.UserId
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.net.URL

class GetLoginUserServiceImplSpec {
  private val userRepository = mockk<UserRepository>()
  private val subject = GetLoginUserServiceImpl(userRepository)

  @Test
  fun getLoginUser() {
    val user = User(
      id = UserId(value = ""),
      username = Username(value = ""),
      displayName = null,
      note = null,
      avatar = URL("https://www.google.com"),
      header = URL("https://www.google.com"),
      followingCount = 0,
      followerCount = 0,
    )

    coEvery { userRepository.findLoginUser(disableCache = any()) } returns user

    val result = runBlocking { subject.execute() }

    assertThat(result).isEqualTo(user)
  }
}
