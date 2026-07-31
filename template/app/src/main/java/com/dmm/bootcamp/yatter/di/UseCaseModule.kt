package com.dmm.bootcamp.yatter.di

import com.dmm.bootcamp.yatter.usecase.impl.login.LoginUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.impl.post.PostYweetUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.impl.register.RegisterUserUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.impl.updateuser.UpdateUserUseCaseImpl
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCase
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCase
import com.dmm.bootcamp.yatter.usecase.register.RegisterUserUseCase
import com.dmm.bootcamp.yatter.usecase.updateuser.UpdateUserUseCase
import org.koin.dsl.module

internal val useCaseModule = module {
  factory<PostYweetUseCase> { PostYweetUseCaseImpl(get(), get()) }
  factory<RegisterUserUseCase> { RegisterUserUseCaseImpl(get(), get(), get()) }
  factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
  factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(get()) }
}
