package com.dmm.bootcamp.yatter.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.service.GetLoginUserService
import com.dmm.bootcamp.yatter.ui.profile.bindingmodel.converter.ProfileConverter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileNavigationEvent {
  data object NavigateToPost : ProfileNavigationEvent
}

class ProfileViewModel(
  private val getLoginUserService: GetLoginUserService
) : ViewModel() {
  private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.empty())
  val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<ProfileNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<ProfileNavigationEvent> = _navigationEvent.receiveAsFlow()

  private suspend fun fetchLoginUser() {
    val me = getLoginUserService.execute()
    Log.d("Yatter", me.toString())

    if (me == null) {
      Log.e("Yatter", "null")
      return
    }

    Log.d("Yatter", "Process")
    _uiState.update {
      it.copy(profile = ProfileConverter.convertToBindingModel(me))
    }
  }

  fun onResume() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchLoginUser()
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickPost() {
    viewModelScope.launch {
      _navigationEvent.send(ProfileNavigationEvent.NavigateToPost)
    }
  }
}
