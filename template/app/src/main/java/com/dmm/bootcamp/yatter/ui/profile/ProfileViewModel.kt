package com.dmm.bootcamp.yatter.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.repository.UserRepository
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
  private val userRepository: UserRepository
) : ViewModel() {
  private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.empty())
  val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<ProfileNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<ProfileNavigationEvent> = _navigationEvent.receiveAsFlow()

  private suspend fun fetchUser(username: String) {
    val user = userRepository.findByUsername(Username(username), disableCache = true) ?: return
    _uiState.update {
      it.copy(profile = ProfileConverter.convertToBindingModel(user))
    }
  }

  fun onResume(username: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchUser(username)
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}
