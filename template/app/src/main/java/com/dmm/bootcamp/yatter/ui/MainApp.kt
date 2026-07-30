package com.dmm.bootcamp.yatter.ui

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dmm.bootcamp.yatter.ui.login.LoginPage
import com.dmm.bootcamp.yatter.ui.navigation.LoginKey
import com.dmm.bootcamp.yatter.ui.navigation.PostKey
import com.dmm.bootcamp.yatter.ui.navigation.PublicTimelineKey
import com.dmm.bootcamp.yatter.ui.navigation.YatterNavKey
import com.dmm.bootcamp.yatter.ui.post.PostPage
import com.dmm.bootcamp.yatter.ui.timeline.PublicTimelinePage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel = koinViewModel()) {

  val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()
  LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) { mainViewModel.onCreate() }

  val startNavKey: YatterNavKey = when (isLoggedIn) {
    true -> PublicTimelineKey
    false -> LoginKey
    null -> return
  }

  key(startNavKey) {
    val backStack = rememberNavBackStack(startNavKey)
    val onBack: () -> Unit = {
      if (backStack.size > 1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
          backStack.removeLast()
        } else {
          backStack.removeAt(backStack.lastIndex)
        }
      }
    }

    NavDisplay(
      backStack = backStack,
      onBack = onBack,
      entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
      ),
      entryProvider = entryProvider {
        entry<LoginKey> {
          LoginPage(
            onLoggedIn = {
              backStack.clear()
              backStack.add(PublicTimelineKey)
            },
            onNavigationRegister = {}
          )
        }

        entry<PublicTimelineKey> {
          PublicTimelinePage(
            onNavigateToPost = { backStack.add(PostKey) },
          )
        }

        entry<PostKey> {
          PostPage(
            onPosted = { onBack() },
            onBack = onBack
          )
        }
      }
    )
  }
}
