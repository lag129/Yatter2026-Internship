package com.dmm.bootcamp.yatter.ui.updateuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import com.dmm.bootcamp.yatter.ui.updateuser.bindingmodel.UpdateUserBindingModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserTemplate(
  bindingModel: UpdateUserBindingModel,
  isLoading: Boolean,
  onChangedDisplayName: (String) -> Unit,
  onClickSelectAvatar: () -> Unit,
  onChangedNote: (String) -> Unit,
  onClickRegister: () -> Unit,
  onClickSkip: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = "プロフィール設定")
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "戻る"
            )
          }
        }
      )
    },
  ) { paddingValues ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(8.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        TextField(
          value = bindingModel.displayName,
          onValueChange = onChangedDisplayName,
          label = { Text("ユーザー名") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.padding(bottom = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("アバター画像を設定: ")
          IconButton(onClick = onClickSelectAvatar) {
            Icon(
              imageVector = Icons.Default.Image,
              contentDescription = "アバター画像を選択"
            )
          }
        }

        if (bindingModel.avatarUri != null) {
          AsyncImage(
            model = bindingModel.avatarUri,
            contentDescription = "アバター画像",
            modifier = Modifier.size(200.dp)
          )
        }

        TextField(
          value = bindingModel.note,
          onValueChange = onChangedNote,
          label = { Text("自己紹介") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = onClickSkip
          ) {
            Text("スキップ")
          }

          Spacer(modifier = Modifier.width(16.dp))

          Button(
            onClick = onClickRegister
          ) {
            Text("登録")
          }
        }
      }

      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

@Preview
@Composable
private fun UpdateUserTemplatePreview() {
  YatterTheme {
    Surface {
      UpdateUserTemplate(
        bindingModel = UpdateUserBindingModel(
          displayName = "displayName",
          avatarUri = null,
          note = "note"
        ),
        isLoading = true,
        onChangedDisplayName = {},
        onClickSelectAvatar = {},
        onChangedNote = {},
        onClickRegister = {},
        onClickSkip = {},
        onBack = {}
      )
    }
  }
}
