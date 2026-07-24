package com.dmm.bootcamp.yatter.auth

import android.accounts.AuthenticatorException
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences

class TokenProviderImpl(private val tokenPreferences: TokenPreferences) : TokenProvider {
  override suspend fun provide(): String = tokenPreferences.getAccessToken()?.let { "username $it" }
    ?: throw AuthenticatorException()
}
