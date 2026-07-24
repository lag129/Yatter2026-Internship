package com.dmm.bootcamp.yatter.domain.model

interface Password {
  val value: String
  fun validate(): Boolean
}
