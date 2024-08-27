/*
 * Copyright (C) 2024 Rahmouni Neïl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.rahmouni.neil.counters.feature.login

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.rahmouni.neil.counters.core.designsystem.LocalNavAnimatedVisibilityScope

const val LOGIN_ROUTE = "login"

fun NavController.navigateToLogin(navOptions: NavOptions? = null) =
    navigate(route = LOGIN_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.loginScreen(
    navController: NavController,
    navigateToNextPage: () -> Unit,
) {
    composable(route = LOGIN_ROUTE) {
        CompositionLocalProvider(value = LocalNavAnimatedVisibilityScope provides this) {
            LoginRoute(
                navController = navController,
                navigateToNextPage = navigateToNextPage,
            )
        }
    }
}
