package com.dmm.bootcamp.yatter.ui.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarTab
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublicTimelinePage(
  onNavigateToPost: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToProfile: (String) -> Unit,
  onNavigateToBottomBar: (BottomBarTab) -> Unit,
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
  val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    publicTimelineViewModel.onResume()
  }

  LaunchedEffect(publicTimelineViewModel) {
    publicTimelineViewModel.navigationEvent.collect { event ->
      when (event) {
        PublicTimelineNavigationEvent.NavigateToPost -> onNavigateToPost()
        PublicTimelineNavigationEvent.NavigateToDetail -> onNavigateToDetail(
          publicTimelineViewModel.yweetId.value ?: return@collect
        )
      }
    }
  }

  PublicTimelineTemplate(
    yweetList = uiState.yweetList,
    isLoading = uiState.isLoading,
    isRefreshing = uiState.isRefreshing,
    onRefresh = publicTimelineViewModel::onRefresh,
    onClickPost = publicTimelineViewModel::onClickPost,
    onClickYweet = publicTimelineViewModel::onClickYweet,
    onClickBottomBar = onNavigateToBottomBar,
    onClickProfile = onNavigateToProfile
  )
}
