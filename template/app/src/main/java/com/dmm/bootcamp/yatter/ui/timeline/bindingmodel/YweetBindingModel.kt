package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel

import androidx.compose.runtime.Immutable

@Immutable
data class YweetBindingModel(
  val id: String,
  val displayName: String,
  val username: String,
  val avatar: String?,
  val content: String,
  val attachmentImageList: List<ImageBindingModel>,
)
