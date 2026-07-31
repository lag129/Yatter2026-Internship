package com.dmm.bootcamp.yatter.ui.updateuser

import com.dmm.bootcamp.yatter.ui.updateuser.bindingmodel.UpdateUserBindingModel

data class UpdateUserUiState(
  val bindingModel: UpdateUserBindingModel,
  val isLoading: Boolean,
) {
  companion object {
    fun empty(): UpdateUserUiState {
      return UpdateUserUiState(
        bindingModel = UpdateUserBindingModel(
          displayName = "",
          avatarUri = null,
          note = ""
        ),
        isLoading = false
      )
    }
  }
}
