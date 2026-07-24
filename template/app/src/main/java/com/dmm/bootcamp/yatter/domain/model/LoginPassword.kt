package com.dmm.bootcamp.yatter.domain.model

data class LoginPassword(override val value: String) : Password {
  override fun validate(): Boolean = value.isNotEmpty()
}
