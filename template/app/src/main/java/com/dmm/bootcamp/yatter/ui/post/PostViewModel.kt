package com.dmm.bootcamp.yatter.ui.post

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.service.GetLoginUserService
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCase
import com.dmm.bootcamp.yatter.usecase.post.PostYweetUseCaseResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PostNavigationEvent {
  data object Posted : PostNavigationEvent
  data object Back : PostNavigationEvent
}

class PostViewModel(
  private val postYweetUseCase: PostYweetUseCase,
  private val getLoginUserService: GetLoginUserService
) : ViewModel() {
  private val _uiState: MutableStateFlow<PostUiState> = MutableStateFlow(PostUiState.empty())
  val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<PostNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<PostNavigationEvent> = _navigationEvent.receiveAsFlow()

  fun onCreate() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }

      val me = getLoginUserService.execute()

      val snapshotBindingModel = uiState.value.bindingModel
      _uiState.update {
        it.copy(
          bindingModel = snapshotBindingModel.copy(avatarUrl = me?.avatar?.toString()),
          isLoading = false
        )
      }
    }
  }

  fun onChangedYweetText(yweetText: String) {
    _uiState.update {
      it.copy(
        bindingModel = uiState.value.bindingModel.copy(yweetText = yweetText)
      )
    }
  }

  fun onClickPost(context: Context) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      val result = postYweetUseCase.execute(
        content = uiState.value.bindingModel.yweetText,
        attachmentList = emptyList(),
      )

      when (result) {
        PostYweetUseCaseResult.Success -> {
          _navigationEvent.send(PostNavigationEvent.Posted)
        }

        is PostYweetUseCaseResult.Failure -> {
          Log.e("Yatter", "Post Yweet Failed")
        }
      }
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickNavIcon() {
    viewModelScope.launch {
      _navigationEvent.send(PostNavigationEvent.Back)
    }
  }
}
