package com.dmm.bootcamp.yatter.di

import com.dmm.bootcamp.yatter.auth.TokenInterceptor
import com.dmm.bootcamp.yatter.auth.TokenProvider
import com.dmm.bootcamp.yatter.auth.TokenProviderImpl
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences
import org.koin.core.qualifier.named
import org.koin.dsl.module
import remote.apis.AuthApi
import remote.apis.ImagesApi
import remote.apis.TimelinesApi
import remote.apis.UsersApi
import remote.apis.YweetsApi
import remote.infrastructure.ApiClient

internal val infraModule = module {
  single { LoginUserPreferences(get()) }
  single { TokenPreferences(get()) }
  single { TokenInterceptor(get()) }

  // APIの提供
  single<AuthApi> { ApiClient().createService(AuthApi::class.java) }
  single<ImagesApi> {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(ImagesApi::class.java)
  }
  single<TimelinesApi>(named("public")) {
    ApiClient().createService(TimelinesApi::class.java)
  }
  single<TimelinesApi>(named("home")) {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(TimelinesApi::class.java)
  }
  single<UsersApi> {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(UsersApi::class.java)
  }
  single<YweetsApi> {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(YweetsApi::class.java)
  }

  factory<TokenProvider> { TokenProviderImpl(get()) }
}
