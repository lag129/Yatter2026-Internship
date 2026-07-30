package com.dmm.bootcamp.yatter.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.model.LoginPassword
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.usecase.register.RegisterUserUseCase
import com.dmm.bootcamp.yatter.usecase.register.RegisterUserUseCaseResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RegisterNavigationEvent {
  data object Registered : RegisterNavigationEvent
  data object NavigatedToLogin : RegisterNavigationEvent
}

class RegisterViewModel(
  private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel() {

  private val _uiState: MutableStateFlow<RegisterUiState> =
    MutableStateFlow(RegisterUiState.empty())
  val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<RegisterNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<RegisterNavigationEvent> = _navigationEvent.receiveAsFlow()

  fun onChangedUsername(username: String) {
    val snapshotBindingModel = uiState.value.registerBindingModel
    _uiState.update {
      it.copy(
        validUsername = Username(username).validate(),
        registerBindingModel = snapshotBindingModel.copy(username = username),
      )
    }
  }

  fun onChangedPassword(password: String) {
    val snapshotBindingModel = uiState.value.registerBindingModel
    _uiState.update {
      it.copy(
        validPassword = LoginPassword(password).validate(),
        registerBindingModel = snapshotBindingModel.copy(password = password),
      )
    }
  }

  fun onClickRegister() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }

      val snapBindingModel = uiState.value.registerBindingModel

      val result = registerUserUseCase.execute(
        snapBindingModel.username,
        snapBindingModel.password,
      )

      when (result) {
        is RegisterUserUseCaseResult.Success -> {
          _navigationEvent.send(RegisterNavigationEvent.Registered)
          Log.d("Yatter", "Register Success")
        }

        is RegisterUserUseCaseResult.Failure -> {
          Log.e("Yatter", "Register Failed")
        }
      }

      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickLogin() {
    viewModelScope.launch {
      _navigationEvent.send(RegisterNavigationEvent.NavigatedToLogin)
    }
  }

  fun onBack() {
    viewModelScope.launch {
      _navigationEvent.send(RegisterNavigationEvent.NavigatedToLogin)
    }
  }
}
