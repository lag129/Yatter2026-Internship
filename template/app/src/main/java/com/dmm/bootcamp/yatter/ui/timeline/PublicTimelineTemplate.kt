package com.dmm.bootcamp.yatter.ui.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.YweetBindingModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PublicTimelineTemplate(
  yweetList: List<YweetBindingModel>,
  isLoading: Boolean,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
) {
  val pullToRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = "タイムライン")
        },
      )
    },
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullToRefreshState)
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
      ) {
        items(yweetList) { item ->
          YweetRow(yweetBindingModel = item)
        }
      }

      PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullToRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )

      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

@Preview
@Composable
private fun PublicTimelineTemplatePreview() {
  YatterTheme {
    Surface {
      PublicTimelineTemplate(
        yweetList = listOf(
          YweetBindingModel(
            id = "id1",
            displayName = "display name1",
            username = "username1",
            avatar = null,
            content = "preview content1",
            attachmentImageList = listOf(),
          ),
          YweetBindingModel(
            id = "id2",
            displayName = "display name2",
            username = "username2",
            avatar = null,
            content = "preview content2",
            attachmentImageList = listOf(),
          ),
        ),
        isLoading = true,
        isRefreshing = false,
        onRefresh = {},
      )
    }
  }
}
