package com.dmm.bootcamp.yatter.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class DetailKey(
  val yweetId: String,
) : YatterNavKey {
}
