package com.dmm.bootcamp.yatter.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** アプリ内の全画面キーの共通親。`rememberNavBackStack` の多型シリアライズ用。 */
@Serializable
sealed interface YatterNavKey : NavKey
