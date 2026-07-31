package com.dmm.bootcamp.yatter.ui.bottombar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
  onClick: (BottomBarTab) -> Unit,
  bottomBarViewModel: BottomBarViewModel = koinViewModel()
) {
  LaunchedEffect(bottomBarViewModel) {
    bottomBarViewModel.navigationEvent.collect { navigationEvent ->
      when (navigationEvent) {
        BottomBarTab.PUBLIC_TIMELINE -> bottomBarViewModel.onClickPublicTimeline()
        BottomBarTab.PROFILE -> bottomBarViewModel.onClickProfile()
      }
    }
  }

  val startDestination = BottomBarTab.PUBLIC_TIMELINE
  var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

  NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
    BottomBarTab.entries.forEachIndexed { index, destination ->
      NavigationBarItem(
        selected = selectedDestination == index,
        onClick = {
          selectedDestination = index
          onClick(destination)
        },
        icon = {
          Icon(
            destination.icon,
            contentDescription = destination.route
          )
        }
      )
    }
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
