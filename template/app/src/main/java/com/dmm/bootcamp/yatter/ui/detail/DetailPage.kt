package com.dmm.bootcamp.yatter.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.bootcamp.yatter.domain.model.YweetId
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailPage(
  yweetId: String,
  onBack: () -> Unit,
  detailViewModel: DetailViewModel = koinViewModel(),
) {
  val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    detailViewModel.onResume(YweetId(yweetId))
  }

  LaunchedEffect(detailViewModel) {
    detailViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        DetailTimelineNavigationEvent.Back -> onBack()
      }
    }
  }

  DetailTemplate(
    yweet = uiState.yweet,
    isLoading = uiState.isLoading,
    onBack = detailViewModel::onBack,
  )
}
