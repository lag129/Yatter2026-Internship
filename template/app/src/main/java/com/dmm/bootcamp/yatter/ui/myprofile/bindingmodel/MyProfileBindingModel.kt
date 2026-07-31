package com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel

import androidx.compose.runtime.Immutable

@Immutable
data class MyProfileBindingModel(
  val id: String,
  val username: String,
  val displayName: String?,
  val note: String?,
  val avatar: String?,
  val header: String?,
  val followingCount: Int,
  val followerCount: Int,
)
