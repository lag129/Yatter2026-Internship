package com.dmm.bootcamp.yatter.ui.post

import com.dmm.bootcamp.yatter.ui.post.bindingmodel.PostBindingModel

data class PostUiState(
  val bindingModel: PostBindingModel,
  val isLoading: Boolean,
) {
  companion object {
    fun empty(): PostUiState = PostUiState(
      bindingModel = PostBindingModel(
        avatarUrl = null,
        yweetText = "",
        attachmentImageUris = emptyList()
      ),
      isLoading = false,
    )
  }

  val canPost: Boolean
    get() = bindingModel.yweetText.isNotBlank() || bindingModel.attachmentImageUris.isNotEmpty()
}