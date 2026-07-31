package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel

import kotlinx.serialization.Serializable

@Serializable
data class ImageBindingModel(
  val id: String,
  val type: String,
  val url: String,
  val description: String?,
)
