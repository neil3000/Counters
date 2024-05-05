/*
 * Copyright 2024 Rahmouni Neïl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.rahmouni.neil.counters.feature.settings.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.rahmouni.neil.counters.feature.settings.accessibility.navigateToAccessibilitySettings
import dev.rahmouni.neil.counters.feature.settings.dataAndPrivacy.navigateToDataAndPrivacySettings
import dev.rahmouni.neil.counters.feature.settings.developer.navigateToDeveloperSettings

const val SETTINGS_ROUTE = "settings"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(navController: NavController, navigateToAboutMe: () -> Unit) {
    composable(route = SETTINGS_ROUTE) {
        // TODO add contribute redirection
        SettingsRoute(
            onBackIconButtonClicked = navController::popBackStack,
            onClickDataAndPrivacyTile = navController::navigateToDataAndPrivacySettings,
            onClickAccessibilityTile = navController::navigateToAccessibilitySettings,
            onClickContributeTile = {},
            onClickAboutMeTile = navigateToAboutMe,
            onClickDeveloperSettings = navController::navigateToDeveloperSettings,
        )
    }
}
