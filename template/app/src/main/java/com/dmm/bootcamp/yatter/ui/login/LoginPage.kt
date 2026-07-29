package com.dmm.bootcamp.yatter.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginPage(
  onLoggedIn: () -> Unit,
  onNavigationRegister: () -> Unit,
  loginViewModel: LoginViewModel = koinViewModel(),
) {
  val uiState: LoginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(loginViewModel) {
    loginViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        LoginNavigationEvent.LoggedIn -> onLoggedIn()
        LoginNavigationEvent.NavigatedToRegister -> onNavigationRegister()
      }
    }
  }

  LoginTemplate(
    userName = uiState.loginBindingModel.username,
    onChangedUserName = loginViewModel::onChangedUsername,
    password = uiState.loginBindingModel.password,
    onChangedPassword = loginViewModel::onChangedPassword,
    isEnableLogin = uiState.isEnableLogin,
    isLoading = uiState.isLoading,
    onClickLogin = loginViewModel::onClickLogin,
    onClickRegister = loginViewModel::onClickRegister,
  )
}
