package com.dmm.bootcamp.yatter.ui.detail.bindingmodel

import androidx.compose.runtime.Immutable
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.ImageBindingModel
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DetailBindingModel(
  val id: String,
  val displayName: String,
  val username: String,
  val avatar: String?,
  val content: String,
  val attachmentImageList: List<ImageBindingModel>,
)
