package com.dmm.bootcamp.yatter.di

import com.dmm.bootcamp.yatter.ui.MainViewModel
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarViewModel
import com.dmm.bootcamp.yatter.ui.detail.DetailViewModel
import com.dmm.bootcamp.yatter.ui.login.LoginViewModel
import com.dmm.bootcamp.yatter.ui.post.PostViewModel
import com.dmm.bootcamp.yatter.ui.profile.ProfileViewModel
import com.dmm.bootcamp.yatter.ui.register.RegisterViewModel
import com.dmm.bootcamp.yatter.ui.timeline.PublicTimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
  viewModel { MainViewModel(get()) }
  viewModel { PublicTimelineViewModel(get()) }
  viewModel { PostViewModel(get(), get()) }
  viewModel { RegisterViewModel(get()) }
  viewModel { LoginViewModel(get()) }
  viewModel { DetailViewModel(get()) }
  viewModel { BottomBarViewModel() }
  viewModel { ProfileViewModel(get()) }
}
