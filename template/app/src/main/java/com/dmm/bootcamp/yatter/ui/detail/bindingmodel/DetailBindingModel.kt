package com.dmm.bootcamp.yatter.ui.detail.bindingmodel

import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.ImageBindingModel

data class DetailBindingModel(
  val id: String,
  val displayName: String,
  val username: String,
  val avatar: String?,
  val content: String,
  val attachmentImageList: List<ImageBindingModel>,
)
