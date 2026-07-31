package com.dmm.bootcamp.yatter.ui.profile.bindingmodel

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileBindingModel(
  val id: String,
  val username: String,
  val displayName: String?,
  val note: String?,
  val avatar: String?,
  val header: String?,
  val followingCount: Int,
  val followerCount: Int,
)
