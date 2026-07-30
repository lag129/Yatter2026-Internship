package com.dmm.bootcamp.yatter.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dmm.bootcamp.yatter.R
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import com.dmm.bootcamp.yatter.ui.timeline.YweetRow
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.YweetBindingModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTemplate(
  yweet: YweetBindingModel,
  isLoading: Boolean,
  onClickNavIcon: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = stringResource(R.string.detail_title))
        },
        navigationIcon = {
          IconButton(onClick = onClickNavIcon) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.detail_back_icon_button_description)
            )
          }
        }
      )
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      YweetRow(yweetBindingModel = yweet)

      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun DetailTemplatePreview() {
  YatterTheme {
    Surface {
      DetailTemplate(
        yweet = YweetBindingModel(
          id = "id1",
          displayName = "display name1",
          username = "username1",
          avatar = null,
          content = "preview content1",
          attachmentImageList = listOf(),
        ),
        isLoading = true,
        onClickNavIcon = {},
      )
    }
  }
}
