package com.dmm.bootcamp.yatter.ui.myprofile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dmm.bootcamp.yatter.R
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBar
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarTab
import com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel.MyProfileBindingModel
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme

@SuppressLint("LocalContextResourcesRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileTemplate(
  profileBindingModel: MyProfileBindingModel,
  isLoading: Boolean,
  onClickUpdateUser: () -> Unit,
  onClickBottomBar: (BottomBarTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val placeholder = ResourcesCompat.getDrawable(
    context.resources,
    R.drawable.avatar_placeholder,
    null,
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = "ユーザ")
        }
      )
    },
    bottomBar = {
      BottomBar(
        onClick = onClickBottomBar,
      )
    },
  ) { paddingValues ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(profileBindingModel.avatar)
            .placeholder(placeholder)
            .error(placeholder)
            .fallback(placeholder)
            .setHeader("User-Agent", "Mozilla/5.0")
            .build(),
          contentDescription = "アバター画像",
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.White),
        )

        Text(
          text = buildAnnotatedString {
            profileBindingModel.displayName?.let {
              append(it)
              append(" ")
            }
            append("＠${profileBindingModel.username}")
          },
          color = Color.DarkGray,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
          text = buildAnnotatedString {
            if (!profileBindingModel.note.isNullOrEmpty()) {
              append(profileBindingModel.note)
            } else {
              withStyle(
                style = SpanStyle(
                  color = Color.DarkGray,
                  fontStyle = FontStyle.Italic,
                  fontWeight = FontWeight.Light
                )
              ) {
                append("自己紹介はまだありません")
              }
            }
          },
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
          text = buildAnnotatedString {
            append("フォロー中")
            append(" : ")
            append(profileBindingModel.followingCount.toString())
          }
        )

        Text(
          text = buildAnnotatedString {
            append("フォロワー")
            append(" : ")
            append(profileBindingModel.followerCount.toString())
          }
        )

        TextButton(
          onClick = onClickUpdateUser
        ) {
          Text("編集")
        }
      }

      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun MyProfileTemplatePreview() {
  YatterTheme {
    Surface {
      MyProfileTemplate(
        profileBindingModel = MyProfileBindingModel(
          id = "",
          username = "",
          displayName = "",
          note = "",
          avatar = "",
          header = "",
          followingCount = 100,
          followerCount = 100,
        ),
        isLoading = true,
        onClickUpdateUser = {},
        onClickBottomBar = {}
      )
    }
  }
}
