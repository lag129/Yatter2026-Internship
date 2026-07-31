package com.dmm.bootcamp.yatter.ui.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.service.GetLoginUserService
import com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel.converter.MyProfileConverter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MyProfileNavigationEvent {
  data object NavigateToPost : MyProfileNavigationEvent
  data object NavigateToUpdateUser : MyProfileNavigationEvent
}

class MyProfileViewModel(
  private val getLoginUserService: GetLoginUserService
) : ViewModel() {
  private val _uiState: MutableStateFlow<MyProfileUiState> =
    MutableStateFlow(MyProfileUiState.empty())
  val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<MyProfileNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<MyProfileNavigationEvent> = _navigationEvent.receiveAsFlow()

  private suspend fun fetchLoginUser() {
    val me = getLoginUserService.execute() ?: return
    _uiState.update {
      it.copy(profile = MyProfileConverter.convertToBindingModel(me))
    }
  }

  fun onResume() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchLoginUser()
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickUpdateUser() {
    viewModelScope.launch {
      _navigationEvent.send(MyProfileNavigationEvent.NavigateToUpdateUser)
    }
  }
}
