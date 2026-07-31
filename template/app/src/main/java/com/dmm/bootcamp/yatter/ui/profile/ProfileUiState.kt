package com.dmm.bootcamp.yatter.ui.profile

import com.dmm.bootcamp.yatter.ui.profile.bindingmodel.ProfileBindingModel

data class ProfileUiState(
  val profile: ProfileBindingModel,
  val isLoading: Boolean
) {
  companion object {
    fun empty(): ProfileUiState = ProfileUiState(
      profile = ProfileBindingModel(
        id = "",
        username = "",
        displayName = null,
        note = null,
        avatar = null,
        header = null,
        followingCount = 0,
        followerCount = 0,
      ),
      isLoading = false
    )
  }
}
