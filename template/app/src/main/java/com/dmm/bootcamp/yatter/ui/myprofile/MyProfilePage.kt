package com.dmm.bootcamp.yatter.ui.myprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarTab
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyProfilePage(
  onNavigateToPost: () -> Unit,
  onNavigateToUpdateUser: () -> Unit,
  onNavigateToBottomBar: (BottomBarTab) -> Unit,
  profileViewModel: MyProfileViewModel = koinViewModel()
) {
  val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    profileViewModel.onResume()
  }

  LaunchedEffect(profileViewModel) {
    profileViewModel.navigationEvent.collect { event ->
      when (event) {
        MyProfileNavigationEvent.NavigateToPost -> onNavigateToPost()
        MyProfileNavigationEvent.NavigateToUpdateUser -> onNavigateToUpdateUser()
      }
    }
  }

  MyProfileTemplate(
    profileBindingModel = uiState.profile,
    isLoading = uiState.isLoading,
    onClickUpdateUser = profileViewModel::onClickUpdateUser,
    onClickBottomBar = onNavigateToBottomBar
  )
}
