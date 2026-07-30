package com.dmm.bootcamp.yatter.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.converter.YweetConverter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PublicTimelineNavigationEvent {
  data object NavigateToPost : PublicTimelineNavigationEvent
  data object NavigateToDetail : PublicTimelineNavigationEvent
}

class PublicTimelineViewModel(
  private val yweetRepository: YweetRepository,
) : ViewModel() {
  private val _uiState: MutableStateFlow<PublicTimelineUiState> =
    MutableStateFlow(PublicTimelineUiState.empty())
  val uiState: StateFlow<PublicTimelineUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<PublicTimelineNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<PublicTimelineNavigationEvent> = _navigationEvent.receiveAsFlow()

  private val _yweetId = MutableStateFlow<String?>(null)
  val yweetId: StateFlow<String?> = _yweetId.asStateFlow()

  private suspend fun fetchPublicTimeline() {
    val yweetList = yweetRepository.findAllPublic()
    _uiState.update {
      it.copy(yweetList = YweetConverter.convertToBindingModel(yweetList))
    }
  }

  fun onResume() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchPublicTimeline()
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onRefresh() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchPublicTimeline()
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickPost() {
    viewModelScope.launch {
      _navigationEvent.send(PublicTimelineNavigationEvent.NavigateToPost)
    }
  }

  fun onClickYweet(yweetId: String) {
    viewModelScope.launch {
      _yweetId.value = yweetId
      _navigationEvent.send(PublicTimelineNavigationEvent.NavigateToDetail)
    }
  }
}
