package com.dmm.bootcamp.yatter.domain.model

import com.dmm.bootcamp.yatter.common.ddd.Identifier

class Username(value: String) : Identifier<String>(value) {
  fun validate(): Boolean = value.isNotBlank()
}
