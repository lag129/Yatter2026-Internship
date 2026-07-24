package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class GetLoginUsernameServiceImplSpec {
  private val loginUserPreferences = mockk<LoginUserPreferences>()
  private val subject = GetLoginUsernameServiceImpl(loginUserPreferences)

  @Test
  fun getLoginUsername() {
    val username = "username"

    every { loginUserPreferences.getUsername() } returns username

    val result = subject.execute()

    assertThat(result).isEqualTo(Username(value = username))
  }

  @Test
  fun getLoginUsernameNull() {
    every { loginUserPreferences.getUsername() } returns null

    val result = subject.execute()

    assertThat(result).isNull()
  }
}
