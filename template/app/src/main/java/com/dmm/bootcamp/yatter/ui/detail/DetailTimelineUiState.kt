package com.dmm.bootcamp.yatter.ui.detail

import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.YweetBindingModel

data class DetailTimelineUiState(
  val yweet: YweetBindingModel,
  val isLoading: Boolean,
) {
  companion object {
    fun empty(): DetailTimelineUiState = DetailTimelineUiState(
      yweet = YweetBindingModel(
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
