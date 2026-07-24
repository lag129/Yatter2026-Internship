package com.dmm.bootcamp.yatter.common.error

import kotlinx.coroutines.flow.StateFlow

interface ErrorHandler {
  val errorMessage: StateFlow<String?>

  fun handle(errorMessage: String)
}
