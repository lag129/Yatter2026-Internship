package com.dmm.bootcamp.yatter.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ProfileKey(
  val username: String
) : YatterNavKey {
}
