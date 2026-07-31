package com.dmm.bootcamp.yatter.ui.updateuser

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdateUserPage(
  onRegistered: () -> Unit,
  onSkipped: () -> Unit,
  updateUserViewModel: UpdateUserViewModel = koinViewModel(),
) {
  val uiState by updateUserViewModel.uiState.collectAsStateWithLifecycle()

  val context = LocalContext.current

  LaunchedEffect(updateUserViewModel) {
    updateUserViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        UpdateUserNavigationEvent.Updated -> onRegistered()
        UpdateUserNavigationEvent.Skipped -> onSkipped()
      }
    }
  }

  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    uri ?: return@rememberLauncherForActivityResult
    updateUserViewModel.onSelectAvatar(uri)
  }

  UpdateUserTemplate(
    bindingModel = uiState.bindingModel,
    isLoading = uiState.isLoading,
    onChangedDisplayName = updateUserViewModel::onChangedDisplayName,
    onClickRegister = { updateUserViewModel.onClickRegister(context) },
    onClickSelectAvatar = { imagePickerLauncher.launch("image/*") },
    onClickSkip = updateUserViewModel::onClickSkip,
    onBack = updateUserViewModel::onClickSkip
  )
}
