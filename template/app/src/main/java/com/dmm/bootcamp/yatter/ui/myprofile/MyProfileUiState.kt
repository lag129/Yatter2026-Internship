package com.dmm.bootcamp.yatter.ui.myprofile

import com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel.MyProfileBindingModel

data class MyProfileUiState(
  val profile: MyProfileBindingModel,
  val isLoading: Boolean
) {
  companion object {
    fun empty(): MyProfileUiState = MyProfileUiState(
      profile = MyProfileBindingModel(
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
