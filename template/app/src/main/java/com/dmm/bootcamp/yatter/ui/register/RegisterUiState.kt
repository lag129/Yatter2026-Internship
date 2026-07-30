package com.dmm.bootcamp.yatter.ui.register

import com.dmm.bootcamp.yatter.ui.register.bindingmodel.RegisterBindingModel

data class RegisterUiState(
  val registerBindingModel: RegisterBindingModel,
  val isLoading: Boolean,
  val validUsername: Boolean,
  val validPassword: Boolean,
) {
  val isEnableRegister: Boolean = validUsername && validPassword

  companion object {
    fun empty(): RegisterUiState = RegisterUiState(
      registerBindingModel = RegisterBindingModel(
        username = "",
        password = "",
      ),
      isLoading = false,
      validUsername = false,
      validPassword = false,
    )
  }
}
