package com.dmm.bootcamp.yatter.ui.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublicTimelinePage(
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
  val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    publicTimelineViewModel.onResume()
  }

  PublicTimelineTemplate(
    yweetList = uiState.yweetList,
    isLoading = uiState.isLoading,
    isRefreshing = uiState.isRefreshing,
    onRefresh = publicTimelineViewModel::onRefresh,
  )
}
