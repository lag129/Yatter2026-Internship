package com.dmm.bootcamp.yatter.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.model.LoginPassword
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCase
import com.dmm.bootcamp.yatter.usecase.login.LoginUseCaseResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoginNavigationEvent {
  data object LoggedIn : LoginNavigationEvent
  data object NavigatedToRegister : LoginNavigationEvent
}

class LoginViewModel(
  private val loginUseCase: LoginUseCase,
) : ViewModel() {

  private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.empty())
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<LoginNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<LoginNavigationEvent> = _navigationEvent.receiveAsFlow()

  fun onChangedUsername(username: String) {
    val snapshotBindingModel = uiState.value.loginBindingModel
    _uiState.update {
      it.copy(
        validUsername = Username(username).validate(),
        loginBindingModel = snapshotBindingModel.copy(username = username),
      )
    }
  }

  fun onChangedPassword(password: String) {
    val snapshotBindingModel = uiState.value.loginBindingModel
    _uiState.update {
      it.copy(
        validPassword = LoginPassword(password).validate(),
        loginBindingModel = snapshotBindingModel.copy(password = password),
      )
    }
  }

  fun onClickLogin() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }

      val snapBindingModel = uiState.value.loginBindingModel

      val result = loginUseCase.execute(
        Username(snapBindingModel.username),
        LoginPassword(snapBindingModel.password),
      )

      when (result) {
        is LoginUseCaseResult.Success -> {
          _navigationEvent.send(LoginNavigationEvent.LoggedIn)
          Log.d("Yatter", "Login Success")
        }

        is LoginUseCaseResult.Failure -> {
          Log.e("Yatter", "Login Failed")
        }
      }

      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickRegister() {
    viewModelScope.launch {
      _navigationEvent.send(LoginNavigationEvent.NavigatedToRegister)
    }
  }
}
