package com.dmm.bootcamp.yatter.usecase.impl

import com.dmm.bootcamp.yatter.domain.model.LoginPassword
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.service.LoginService
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences
import com.dmm.bootcamp.yatter.usecase.impl.login.LoginUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCaseResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LoginUseCaseImplSpec {
  private val loginService = mockk<LoginService>()
  private val loginUserPreferences = mockk<LoginUserPreferences>()
  private val subject = LoginUseCaseImpl(loginService, loginUserPreferences)

  @Test
  fun loginSuccess() = runTest {
    val username = Username("username")
    val password = LoginPassword("Password1%")

    coJustRun {
      loginService.execute(any(), any())
    }
    coJustRun {
      loginUserPreferences.putUsername(any())
    }

    val result = subject.execute(username, password)

    coVerify {
      loginService.execute(username, password)
    }
    coVerify {
      loginUserPreferences.putUsername(username.value)
    }

    assertThat(result).isEqualTo(LoginUseCaseResult.Success)
  }

  @Test
  fun loginFailureUsernameEmpty() = runTest {
    val username = Username("")
    val password = LoginPassword("Password1%")

    coJustRun {
      loginService.execute(any(), any())
    }
    coJustRun {
      loginUserPreferences.putUsername(any())
    }

    val result = subject.execute(username, password)

    coVerify(inverse = true) {
      loginService.execute(any(), any())
    }
    coVerify(inverse = true) {
      loginUserPreferences.putUsername(any())
    }

    assertThat(result).isEqualTo(LoginUseCaseResult.Failure.EmptyUsername)
  }

  @Test
  fun loginFailurePasswordEmpty() = runTest {
    val username = Username("username")
    val password = LoginPassword("")

    coJustRun {
      loginService.execute(any(), any())
    }
    coJustRun {
      loginUserPreferences.putUsername(any())
    }

    val result = subject.execute(username, password)

    coVerify(inverse = true) {
      loginService.execute(any(), any())
    }
    coVerify(inverse = true) {
      loginUserPreferences.putUsername(any())
    }

    assertThat(result).isEqualTo(LoginUseCaseResult.Failure.EmptyPassword)
  }

  @Test
  fun loginSuccessWithSimplePassword() = runTest {
    val username = Username("username")
    val password = LoginPassword("password")

    coJustRun {
      loginService.execute(any(), any())
    }
    coJustRun {
      loginUserPreferences.putUsername(any())
    }

    val result = subject.execute(username, password)

    coVerify {
      loginService.execute(username, password)
    }
    coVerify {
      loginUserPreferences.putUsername(username.value)
    }

    assertThat(result).isEqualTo(LoginUseCaseResult.Success)
  }

  @Test
  fun loginFailurePasswordOther() = runTest {
    val username = Username("username")
    val password = LoginPassword("Password1%")
    val error = Exception()

    coEvery {
      loginService.execute(any(), any())
    } throws error
    coJustRun {
      loginUserPreferences.putUsername(any())
    }

    val result = subject.execute(username, password)

    coVerify {
      loginService.execute(any(), any())
    }
    coVerify(inverse = true) {
      loginUserPreferences.putUsername(any())
    }

    assertThat(result).isEqualTo(LoginUseCaseResult.Failure.OtherError(error))
  }
}
