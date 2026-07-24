package com.dmm.bootcamp.yatter.domain.model

data class RegisterPassword(override val value: String) : Password {
  companion object {
    private const val SYMBOLS = "/*!@#$%^&*()\"{}_[]|\\?/<>,."
    private const val MIN_LENGTH = 8
  }

  override fun validate(): Boolean = value.isNotEmpty() &&
    hasUpperCase() &&
    hasLowerCase() &&
    hasSymbol() &&
    hasMinLength()

  fun hasUpperCase(): Boolean = value.toCharArray().any { it.isUpperCase() }

  fun hasLowerCase(): Boolean = value.toCharArray().any { it.isLowerCase() }

  fun hasSymbol(): Boolean = value.toCharArray().any { SYMBOLS.contains(it) }

  fun hasMinLength(): Boolean = value.length >= MIN_LENGTH
}
