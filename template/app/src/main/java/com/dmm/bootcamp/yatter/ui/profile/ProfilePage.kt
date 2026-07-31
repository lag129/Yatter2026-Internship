package com.dmm.bootcamp.yatter.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarTab
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfilePage(
  onNavigateToPost: () -> Unit,
  onNavigateToUpdateUser: () -> Unit,
  onNavigateToBottomBar: (BottomBarTab) -> Unit,
  profileViewModel: ProfileViewModel = koinViewModel()
) {
  val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    profileViewModel.onResume()
  }

  LaunchedEffect(profileViewModel) {
    profileViewModel.navigationEvent.collect { event ->
      when (event) {
        ProfileNavigationEvent.NavigateToPost -> onNavigateToPost()
        ProfileNavigationEvent.NavigateToUpdateUser -> onNavigateToUpdateUser()
      }
    }
  }

  ProfileTemplate(
    profileBindingModel = uiState.profile,
    isLoading = uiState.isLoading,
    onClickUpdateUser = profileViewModel::onClickUpdateUser,
    onClickBottomBar = onNavigateToBottomBar
  )
}
