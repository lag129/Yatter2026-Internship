package com.dmm.bootcamp.yatter.ui.updateuser

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.usecase.updateuser.UpdateUserUseCase
import com.dmm.bootcamp.yatter.usecase.updateuser.UpdateUserUseCaseResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed interface UpdateUserNavigationEvent {
  data object Updated : UpdateUserNavigationEvent
  data object Skipped : UpdateUserNavigationEvent
}

class UpdateUserViewModel(
  private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {
  private val _uiState: MutableStateFlow<UpdateUserUiState> =
    MutableStateFlow(UpdateUserUiState.empty())
  val uiState: StateFlow<UpdateUserUiState> = _uiState.asStateFlow()

  private val _navigationEvent = Channel<UpdateUserNavigationEvent>(Channel.BUFFERED)
  val navigationEvent: Flow<UpdateUserNavigationEvent> = _navigationEvent.receiveAsFlow()

  fun onChangedDisplayName(displayName: String) {
    viewModelScope.launch {
      _uiState.update {
        it.copy(
          bindingModel = it.bindingModel.copy(
            displayName = displayName
          )
        )
      }
    }
  }

  fun onSelectAvatar(avatar: Uri) {
    viewModelScope.launch {
      _uiState.update {
        it.copy(
          bindingModel = it.bindingModel.copy(
            avatarUri = avatar
          )
        )
      }
    }
  }

  fun onClickRegister(context: Context) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }

      val snapBindingModel = uiState.value.bindingModel
      val avatarFile = snapBindingModel.avatarUri?.let {
        uriToFile(context, snapBindingModel.avatarUri)
      }

      val result = updateUserUseCase.execute(snapBindingModel.displayName, avatarFile)

      when (result) {
        UpdateUserUseCaseResult.Success -> {
          _navigationEvent.send(UpdateUserNavigationEvent.Updated)
        }

        UpdateUserUseCaseResult.Failure.NotLoggedIn -> {
          Log.e("Yatter", "Not Logged in")
        }

        is UpdateUserUseCaseResult.Failure -> {
          Log.e("Yatter", "Update Failed")
        }
      }

      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun onClickSkip() {
    viewModelScope.launch {
      _navigationEvent.send(UpdateUserNavigationEvent.Updated)
    }
  }

  private fun uriToFile(context: Context, uri: Uri): File? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
      val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
        context.contentResolver.getType(uri)
      ) ?: "jpg"
      val file = File.createTempFile("image", ".$extension")
      file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
      }
      file
    }
  }
}
