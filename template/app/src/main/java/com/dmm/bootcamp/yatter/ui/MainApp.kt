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
import com.dmm.bootcamp.yatter.ui.bottombar.BottomBarTab
import com.dmm.bootcamp.yatter.ui.detail.DetailPage
import com.dmm.bootcamp.yatter.ui.login.LoginPage
import com.dmm.bootcamp.yatter.ui.navigation.DetailKey
import com.dmm.bootcamp.yatter.ui.navigation.LoginKey
import com.dmm.bootcamp.yatter.ui.navigation.PostKey
import com.dmm.bootcamp.yatter.ui.navigation.ProfileKey
import com.dmm.bootcamp.yatter.ui.navigation.PublicTimelineKey
import com.dmm.bootcamp.yatter.ui.navigation.RegisterKey
import com.dmm.bootcamp.yatter.ui.navigation.UpdateUserKey
import com.dmm.bootcamp.yatter.ui.navigation.YatterNavKey
import com.dmm.bootcamp.yatter.ui.post.PostPage
import com.dmm.bootcamp.yatter.ui.profile.ProfilePage
import com.dmm.bootcamp.yatter.ui.register.RegisterPage
import com.dmm.bootcamp.yatter.ui.timeline.PublicTimelinePage
import com.dmm.bootcamp.yatter.ui.updateuser.UpdateUserPage
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
            onNavigationRegister = { backStack.add(RegisterKey) }
          )
        }

        entry<PublicTimelineKey> {
          PublicTimelinePage(
            onNavigateToPost = { backStack.add(PostKey) },
            onNavigateToDetail = { backStack.add(DetailKey(it)) },
            onNavigateToBottomBar = {
              when (it) {
                BottomBarTab.PUBLIC_TIMELINE -> {}
                BottomBarTab.PROFILE -> {
                  backStack.add(ProfileKey)
                }
              }
            }
          )
        }

        entry<PostKey> {
          PostPage(
            onPosted = { onBack() },
            onBack = onBack
          )
        }

        entry<DetailKey> { key ->
          DetailPage(
            yweetId = key.yweetId,
            onBack = onBack,
          )
        }

        entry<RegisterKey> {
          RegisterPage(
            onRegistered = {
              backStack.clear()
              backStack.add(LoginKey)
            },
            onNavigateToLogin = { onBack() }
          )
        }

        entry<ProfileKey> {
          ProfilePage(
            onNavigateToPost = { backStack.add(PostKey) },
            onNavigateToUpdateUser = { backStack.add(UpdateUserKey) },
            onNavigateToBottomBar = {
              when (it) {
                BottomBarTab.PUBLIC_TIMELINE -> {
                  backStack.add(PublicTimelineKey)
                }

                BottomBarTab.PROFILE -> {}
              }
            }
          )
        }

        entry<UpdateUserKey> {
          UpdateUserPage(
            onRegistered = {
              backStack.clear()
              backStack.add(PublicTimelineKey)
            },
            onSkipped = {
              onBack()
            }
          )
        }
      }
    )
  }
}
