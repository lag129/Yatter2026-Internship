package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.model.LoginPassword
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import remote.apis.AuthApi
import remote.models.Login200Response
import remote.models.LoginRequest
import retrofit2.Response

class LoginServiceImplSpec {
  private val authApi = mockk<AuthApi>()
  private val tokenPreferences = mockk<TokenPreferences>()
  private val subject = LoginServiceImpl(
    authApi = authApi,
    tokenPreferences = tokenPreferences,
  )

  @Test
  fun loginSuccess() = runTest {
    val username = Username("username")
    val password = LoginPassword("Password1%")
    val responseValue = Login200Response(
      username = username.value,
    )

    val accessToken = "username"
    justRun {
      tokenPreferences.putAccessToken(any())
    }

    coEvery {
      authApi.login(any())
    } returns Response.success(responseValue)

    subject.execute(username, password)

    coVerify {
      authApi.login(
        LoginRequest(
          username = username.value,
          password = password.value,
        ),
      )
    }

    coVerify {
      tokenPreferences.putAccessToken(accessToken)
    }
  }
}
