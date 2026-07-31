package com.dmm.bootcamp.yatter.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.model.YweetId
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.ui.detail.bindingmodel.converter.DetailConverter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetailTimelineNavigationEvent {
  data object Back : DetailTimelineNavigationEvent
}

class DetailViewModel(
  private val yweetRepository: YweetRepository,
) : ViewModel() {
  private val _uiState: MutableStateFlow<DetailTimelineUiState> =
    MutableStateFlow(DetailTimelineUiState.empty())
  val uiState: StateFlow<DetailTimelineUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<DetailTimelineNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<DetailTimelineNavigationEvent> = _navigationEvent.receiveAsFlow()

  private suspend fun fetchYweetById(yweetId: YweetId) {
    val yweet = yweetRepository.findById(yweetId) ?: return
    _uiState.update {
      it.copy(yweet = DetailConverter.convertToBindingModel(yweet))
    }
  }

  fun onResume(yweetId: YweetId) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      fetchYweetById(yweetId)
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onBack() {
    viewModelScope.launch {
      _navigationEvent.send(DetailTimelineNavigationEvent.Back)
    }
  }
}
