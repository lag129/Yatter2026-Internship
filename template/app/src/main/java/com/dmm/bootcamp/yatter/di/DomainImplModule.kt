package com.dmm.bootcamp.yatter.di

import com.dmm.bootcamp.yatter.domain.repository.ImageRepository
import com.dmm.bootcamp.yatter.domain.repository.UserRepository
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.domain.service.CheckLoginService
import com.dmm.bootcamp.yatter.domain.service.GetLoginUserService
import com.dmm.bootcamp.yatter.domain.service.GetLoginUsernameService
import com.dmm.bootcamp.yatter.domain.service.LoginService
import com.dmm.bootcamp.yatter.infra.domain.repository.ImageRepositoryImpl
import com.dmm.bootcamp.yatter.infra.domain.repository.UserRepositoryImpl
import com.dmm.bootcamp.yatter.infra.domain.repository.YweetRepositoryImpl
import com.dmm.bootcamp.yatter.infra.domain.service.CheckLoginServiceImpl
import com.dmm.bootcamp.yatter.infra.domain.service.GetLoginUserServiceImpl
import com.dmm.bootcamp.yatter.infra.domain.service.GetLoginUsernameServiceImpl
import com.dmm.bootcamp.yatter.infra.domain.service.LoginServiceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val domainImplModule = module {
  single<UserRepository> { UserRepositoryImpl(get(), get()) }
  single<YweetRepository> {
    YweetRepositoryImpl(
      get(named("public")),
      get(named("home")),
      get(),
    )
  }
  single<ImageRepository> { ImageRepositoryImpl(get()) }

  factory<GetLoginUserService> { GetLoginUserServiceImpl(get()) }
  factory<GetLoginUsernameService> { GetLoginUsernameServiceImpl(get()) }
  factory<LoginService> { LoginServiceImpl(get(), get()) }
  factory<CheckLoginService> { CheckLoginServiceImpl(get()) }
}
