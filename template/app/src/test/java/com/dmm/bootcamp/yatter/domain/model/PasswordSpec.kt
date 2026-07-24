package com.dmm.bootcamp.yatter.domain.model

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PasswordSpec {
  @Test
  fun checkRegisterPasswordValidate() = runTest {
    val testCase = listOf(
      "abc" to false,
      "abcdefghi" to false,
      "Abcdefghi" to false,
      "Abcdefghi10" to false,
      "Abcdefghi10%" to true,
    )

    testCase.forEach {
      assertThat(RegisterPassword(it.first).validate()).isEqualTo(it.second)
    }
  }

  @Test
  fun checkLoginPasswordValidate() = runTest {
    val testCase = listOf(
      "" to false,
      "a" to true,
      "abc" to true,
      "Abcdefghi10%" to true,
    )

    testCase.forEach {
      assertThat(LoginPassword(it.first).validate()).isEqualTo(it.second)
    }
  }
}
