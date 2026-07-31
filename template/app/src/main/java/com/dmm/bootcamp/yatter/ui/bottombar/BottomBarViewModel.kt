package com.dmm.bootcamp.yatter.ui.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

enum class BottomBarTab(
  val route: String,
  val icon: ImageVector,
) {
  PUBLIC_TIMELINE("public_timeline", Icons.Filled.Home),
  PROFILE("profile", Icons.Filled.Person),
}

class BottomBarViewModel() : ViewModel() {

  private val _navigationEvent = Channel<BottomBarTab>(Channel.BUFFERED)
  val navigationEvent: Flow<BottomBarTab> = _navigationEvent.receiveAsFlow()

  fun onClickPublicTimeline() {
    viewModelScope.launch {
      _navigationEvent.send(BottomBarTab.PUBLIC_TIMELINE)
    }
  }

  fun onClickProfile() {
    viewModelScope.launch {
      _navigationEvent.send(BottomBarTab.PROFILE)
    }
  }
}
