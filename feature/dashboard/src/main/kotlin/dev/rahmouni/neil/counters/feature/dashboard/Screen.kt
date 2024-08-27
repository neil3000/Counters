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

package dev.rahmouni.neil.counters.feature.dashboard

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.rahmouni.neil.counters.core.data.model.CounterRawData
import dev.rahmouni.neil.counters.core.data.model.IncrementRawData
import dev.rahmouni.neil.counters.core.designsystem.Rn3PreviewScreen
import dev.rahmouni.neil.counters.core.designsystem.Rn3PreviewUiStates
import dev.rahmouni.neil.counters.core.designsystem.Rn3Theme
import dev.rahmouni.neil.counters.core.designsystem.TopAppBarAction
import dev.rahmouni.neil.counters.core.designsystem.component.Rn3Scaffold
import dev.rahmouni.neil.counters.core.designsystem.component.TopAppBarStyle.HOME
import dev.rahmouni.neil.counters.core.designsystem.component.getHaptic
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.Rn3PaddingValues
import dev.rahmouni.neil.counters.core.feedback.FeedbackContext.FeedbackScreenContext
import dev.rahmouni.neil.counters.core.feedback.navigateToFeedback
import dev.rahmouni.neil.counters.core.ui.TrackScreenViewEvent
import dev.rahmouni.neil.counters.feature.dashboard.model.DashboardUiState
import dev.rahmouni.neil.counters.feature.dashboard.model.DashboardViewModel
import dev.rahmouni.neil.counters.feature.dashboard.model.data.DashboardData
import dev.rahmouni.neil.counters.feature.dashboard.model.data.DashboardDataPreviewParameterProvider
import dev.rahmouni.neil.counters.feature.dashboard.model.data.PreviewParameterData
import dev.rahmouni.neil.counters.feature.dashboard.ui.DashboardCard
import dev.rahmouni.neil.counters.feature.dashboard.ui.newCounterModal

@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController,
    navigateToSettings: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardScreen(
        modifier = modifier,
        uiState = uiState,
        feedbackTopAppBarAction = FeedbackScreenContext(
            "DashboardScreen",
            "PkS4cSDUBdi2IvRegPIEe46xgk8Bf7h8",
        ).toTopAppBarAction(navController::navigateToFeedback),
        onSettingsTopAppBarActionClicked = navigateToSettings,
        onCreateCounter = viewModel::createCounter,
        onIncrementCounter = viewModel::incrementCounter,
    )

    TrackScreenViewEvent(screenName = "Dashboard")
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun DashboardScreen(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    feedbackTopAppBarAction: TopAppBarAction? = null,
    onSettingsTopAppBarActionClicked: () -> Unit = {},
    onCreateCounter: (CounterRawData) -> Unit = {},
    onIncrementCounter: (String, IncrementRawData) -> Unit = { _, _ -> },
) {
    val haptics = getHaptic()

    val gridState = rememberLazyGridState()
    val expandedFab by remember { derivedStateOf { gridState.firstVisibleItemIndex == 0 } }

    val openNewCounterModal = newCounterModal(onCreateCounter = onCreateCounter)

    Rn3Scaffold(
        modifier = modifier,
        topAppBarTitle = stringResource(R.string.feature_dashboard_dashboardScreen_topAppBarTitle),
        onBackIconButtonClicked = null,
        topAppBarActions = listOfNotNull(
            TopAppBarAction(
                Icons.Outlined.Settings,
                stringResource(R.string.feature_dashboard_dashboardScreen_topAppBarActions_settings),
                onSettingsTopAppBarActionClicked,
            ),
            feedbackTopAppBarAction,
        ),
        topAppBarStyle = HOME,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = it,
                text = { Text(stringResource(R.string.feature_dashboard_fab_newCounter)) },
                icon = { Icon(Icons.Outlined.Add, null) },
                onClick = {
                    haptics.click()
                    openNewCounterModal()
                },
                expanded = expandedFab,
            )
        },
    ) {
        DashboardPanel(it, uiState.dashboardData, gridState, onIncrementCounter)
    }
}

@Composable
private fun DashboardPanel(
    paddingValues: Rn3PaddingValues,
    data: DashboardData,
    lazyGridState: LazyGridState,
    onIncrementCounter: (counterUid: String, incrementRawData: IncrementRawData) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 165.dp),
        state = lazyGridState,
        contentPadding = paddingValues.add(
            start = 4.dp,
            end = 4.dp,
            bottom = 80.dp,
        ).toComposePaddingValues(),
    ) {
        items(data.counters, key = { it.uid }) {
            with(it) {
                DashboardCard(
                    Modifier
                        .padding(4.dp)
                        .animateItem(),
                ) { value ->
                    onIncrementCounter(uid, IncrementRawData(value = value))
                }
            }
        }
    }
}

@Rn3PreviewScreen
@Composable
private fun Default() {
    Rn3Theme {
        DashboardScreen(
            uiState = DashboardUiState(
                dashboardData = PreviewParameterData.dashboardData_default,
            ),
        )
    }
}

@Rn3PreviewUiStates
@Composable
private fun UiStates(
    @PreviewParameter(DashboardDataPreviewParameterProvider::class)
    dashboardData: DashboardData,
) {
    Rn3Theme {
        DashboardScreen(uiState = DashboardUiState(dashboardData))
    }
}
