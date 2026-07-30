package com.dmm.bootcamp.yatter.ui.post.bindingmodel

import android.net.Uri

data class PostBindingModel(
  val avatarUrl: String?,
  val yweetText: String,
  val attachmentImageUris: List<Uri> = emptyList(),
)
