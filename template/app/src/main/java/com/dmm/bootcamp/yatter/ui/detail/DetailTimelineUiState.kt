package com.dmm.bootcamp.yatter.ui.detail

import com.dmm.bootcamp.yatter.ui.detail.bindingmodel.DetailBindingModel

data class DetailTimelineUiState(
  val yweet: DetailBindingModel,
  val isLoading: Boolean,
) {
  companion object {
    fun empty(): DetailTimelineUiState = DetailTimelineUiState(
      yweet = DetailBindingModel(
        id = "",
        displayName = "",
        username = "",
        avatar = "",
        content = "",
        attachmentImageList = emptyList(),
      ),
      isLoading = false,
    )
  }
}
