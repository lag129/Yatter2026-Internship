package com.dmm.bootcamp.yatter.ui.bottombar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
  onClick: (BottomBarTab) -> Unit,
  bottomBarViewModel: BottomBarViewModel = koinViewModel(),
  modifier: Modifier = Modifier
) {
  LaunchedEffect(bottomBarViewModel) {
    bottomBarViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        BottomBarTab.PUBLIC_TIMELINE -> bottomBarViewModel.onClickPublicTimeline()
        BottomBarTab.PROFILE -> bottomBarViewModel.onClickProfile()
      }
    }
  }

  BottomAppBar(
    actions = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
      ) {
        BottomBarIcon(
          imageVector = Icons.Filled.Home,
          description = "PublicTimeline",
          onClick = { onClick(BottomBarTab.PUBLIC_TIMELINE) }
        )
        BottomBarIcon(
          imageVector = Icons.Filled.Person,
          description = "PublicTimeline",
          onClick = { onClick(BottomBarTab.PROFILE) }
        )
      }
    },
    modifier = modifier.fillMaxWidth(),
  )
}

@Composable
fun BottomBarIcon(
  imageVector: ImageVector,
  description: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clickable { onClick() }
      .clip(CircleShape)
      .padding(12.dp),
  ) {
    Icon(
      imageVector = imageVector,
      contentDescription = description
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarPreview() {
  YatterTheme {
    Surface {
      BottomBar(
        onClick = {},
      )
    }
  }
}
