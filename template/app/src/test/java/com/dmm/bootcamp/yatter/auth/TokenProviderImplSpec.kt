package com.dmm.bootcamp.yatter.auth

import android.accounts.AuthenticatorException
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TokenProviderImplSpec {
  private val tokenPreferences = mockk<TokenPreferences>()
  private val subject = TokenProviderImpl(tokenPreferences)

  @Test
  fun getTokenSuccess() = runTest {
    val username = "username"

    val expect = "username $username"

    coEvery {
      tokenPreferences.getAccessToken()
    } returns username

    val result = subject.provide()

    coVerify {
      tokenPreferences.getAccessToken()
    }

    assertThat(result).isEqualTo(expect)
  }

  @Test
  fun getTokenFailure() = runTest {
    coEvery {
      tokenPreferences.getAccessToken()
    } returns null

    var error: Throwable? = null
    var result: String? = null

    try {
      result = subject.provide()
    } catch (e: Exception) {
      error = e
    }

    coVerify {
      tokenPreferences.getAccessToken()
    }

    assertThat(result).isNull()
    assertThat(error).isInstanceOf(AuthenticatorException::class.java)
  }
}
