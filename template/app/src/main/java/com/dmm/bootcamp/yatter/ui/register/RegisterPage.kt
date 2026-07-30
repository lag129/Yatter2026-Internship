package com.dmm.bootcamp.yatter.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterPage(
  registerViewModel: RegisterViewModel = koinViewModel(),
  onRegistered: () -> Unit,
  onNavigateToLogin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(registerViewModel) {
    registerViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        RegisterNavigationEvent.Registered -> onRegistered()
        RegisterNavigationEvent.NavigatedToLogin -> onNavigateToLogin()
      }
    }
  }

  RegisterTemplate(
    userName = uiState.registerBindingModel.username,
    password = uiState.registerBindingModel.password,
    onChangedUserName = registerViewModel::onChangedUsername,
    onChangedPassword = registerViewModel::onChangedPassword,
    isEnableRegister = uiState.isEnableRegister,
    isLoading = uiState.isLoading,
    onClickRegister = registerViewModel::onClickRegister,
    onClickLogin = registerViewModel::onClickLogin,
    onBack = registerViewModel::onClickLogin
  )
}
