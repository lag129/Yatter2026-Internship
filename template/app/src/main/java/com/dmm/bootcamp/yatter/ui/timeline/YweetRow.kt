package com.dmm.bootcamp.yatter.ui.timeline

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dmm.bootcamp.yatter.R
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.ImageBindingModel
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.YweetBindingModel

@SuppressLint("LocalContextResourcesRead")
@Composable
fun YweetRow(
  yweetBindingModel: YweetBindingModel,
  onClickYweet: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  val placeholder = ResourcesCompat.getDrawable(
    context.resources,
    R.drawable.avatar_placeholder,
    null,
  )

  Row(
    modifier = modifier
      .clickable { onClickYweet.invoke(yweetBindingModel.id) }
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    AsyncImage(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(Color.White),
      model = ImageRequest.Builder(context)
        .data(yweetBindingModel.avatar)
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .setHeader("User-Agent", "Mozilla/5.0")
        .build(),
      contentDescription = stringResource(id = R.string.public_timeline_avatar_content_description),
      contentScale = ContentScale.Crop,
    )

    Column(
      verticalArrangement =
        Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = buildAnnotatedString {
          append(yweetBindingModel.displayName)
          withStyle(
            style = SpanStyle(
              color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
            ),
          ) {
            append(" @${yweetBindingModel.username}")
          }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
      )

      Text(text = yweetBindingModel.content)

      LazyRow {
        items(yweetBindingModel.attachmentImageList) { attachmentImage ->
          AsyncImage(
            model = attachmentImage.url,
            contentDescription = attachmentImage.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .height(128.dp)
              .clip(RoundedCornerShape(10.dp))
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
      }
    }
  }
}

@Preview
@Composable
private fun YweetRowPreview() {
  YatterTheme {
    Surface {
      YweetRow(
        yweetBindingModel = YweetBindingModel(
          id = "id",
          displayName = "mito",
          username = "mitohato14",
          avatar = "https://avatars.githubusercontent.com/u/19385268?v=4",
          content = "preview content",
          attachmentImageList = listOf(
            ImageBindingModel(
              id = "id",
              type = "image",
              url = "https://avatars.githubusercontent.com/u/39693306?v=4",
              description = "icon",
            ),
          ),
        ),
        onClickYweet = {},
      )
    }
  }
}
