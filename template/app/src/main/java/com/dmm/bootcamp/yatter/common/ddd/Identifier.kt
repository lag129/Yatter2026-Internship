package com.dmm.bootcamp.yatter.common.ddd

abstract class Identifier<T>(val value: T) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Identifier<*>) return false
    return value == other.value
  }

  override fun hashCode(): Int = value?.hashCode() ?: 0
}
