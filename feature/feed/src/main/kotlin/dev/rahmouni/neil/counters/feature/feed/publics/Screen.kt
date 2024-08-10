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

package dev.rahmouni.neil.counters.feature.feed.publics

import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.rahmouni.neil.counters.core.designsystem.BottomBarItem
import dev.rahmouni.neil.counters.core.designsystem.R.color
import dev.rahmouni.neil.counters.core.designsystem.TopAppBarAction
import dev.rahmouni.neil.counters.core.designsystem.component.Rn3Scaffold
import dev.rahmouni.neil.counters.core.designsystem.component.TopAppBarStyle.HOME
import dev.rahmouni.neil.counters.core.designsystem.component.getHaptic
import dev.rahmouni.neil.counters.core.designsystem.icons.HumanGreetingProximity
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.Rn3PaddingValues
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.padding
import dev.rahmouni.neil.counters.core.feedback.FeedbackContext.FeedbackScreenContext
import dev.rahmouni.neil.counters.core.feedback.navigateToFeedback
import dev.rahmouni.neil.counters.core.ui.TrackScreenViewEvent
import dev.rahmouni.neil.counters.core.user.Rn3User.AnonymousUser
import dev.rahmouni.neil.counters.core.user.Rn3User.SignedInUser
import dev.rahmouni.neil.counters.feature.feed.R.string
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedUiState.Loading
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedUiState.Success
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedViewModel
import dev.rahmouni.neil.counters.feature.feed.publics.model.data.PublicFeedData
import dev.rahmouni.neil.counters.feature.feed.ui.Publication

@Composable
internal fun PublicFeedRoute(
    modifier: Modifier = Modifier,
    viewModel: PublicFeedViewModel = hiltViewModel(),
    navController: NavController,
    navigateToSettings: () -> Unit,
    navigateToConnect: () -> Unit,
    navigateToFriends: () -> Unit,
    navigateToPublication: () -> Unit,
    navigateToEvents: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        Loading -> {}
        is Success -> PublicFeedScreen(
            modifier = modifier,
            data = (uiState as Success).publicFeedData,
            feedbackTopAppBarAction = FeedbackScreenContext(
                localName = "LocalFeedScreen",
                localID = "PkS4cSDUBdi2IvRegPIEe46xgk8Bf7h8",
            ).toTopAppBarAction(navController::navigateToFeedback),
            onSettingsTopAppBarActionClicked = navigateToSettings,
            onConnectBottomBarItemClicked = navigateToConnect,
            onFriendsBottomBarItemClicked = navigateToFriends,
            onAddBottomBarItemClicked = navigateToPublication,
            onEventsBottomBarItemClicked = navigateToEvents,
        )
    }

    TrackScreenViewEvent(screenName = "PublicFeed")
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun PublicFeedScreen(
    modifier: Modifier = Modifier,
    data: PublicFeedData,
    feedbackTopAppBarAction: TopAppBarAction? = null,
    onSettingsTopAppBarActionClicked: () -> Unit = {},
    onConnectBottomBarItemClicked: () -> Unit = {},
    onFriendsBottomBarItemClicked: () -> Unit = {},
    onAddBottomBarItemClicked: () -> Unit = {},
    onEventsBottomBarItemClicked: () -> Unit = {},
) {
    val haptic = getHaptic()
    val context = LocalContext.current

    val add = when (data.user) {
        is SignedInUser -> BottomBarItem(
            icon = Filled.Add,
            label = stringResource(string.feature_feed_bottomBar_add),
            onClick = onAddBottomBarItemClicked,
            unselectedIconColor = Color(
                ContextCompat.getColor(
                    context,
                    color.core_designsystem_color,
                ),
            ),
            fullSize = true,
        )

        else -> BottomBarItem(
            icon = Filled.Add,
            label = stringResource(string.feature_feed_bottomBar_add),
            onClick = {
                haptic.click()

                Toast
                    .makeText(
                        context,
                        context.getString(string.feature_feed_bottomBar_add_disabled),
                        Toast.LENGTH_SHORT,
                    )
                    .show()
            },
            unselectedIconColor = MaterialTheme.colorScheme.secondaryContainer,
            fullSize = true,
        )
    }

    Rn3Scaffold(
        modifier = modifier,
        topAppBarTitle = stringResource(string.feature_feed_topAppBarTitle_public),
        topAppBarTitleAlignment = CenterHorizontally,
        onBackIconButtonClicked = null,
        topAppBarActions = listOfNotNull(
            TopAppBarAction(
                icon = Outlined.Settings,
                title = stringResource(string.feature_feed_topAppBarActions_settings),
                onClick = onSettingsTopAppBarActionClicked,
            ),
            feedbackTopAppBarAction,
        ),
        bottomBarItems = listOf(
            BottomBarItem(
                icon = Outlined.HumanGreetingProximity,
                label = stringResource(string.feature_feed_bottomBar_connect),
                onClick = onConnectBottomBarItemClicked,
            ),
            BottomBarItem(
                icon = Filled.People,
                label = stringResource(string.feature_feed_bottomBar_friends),
                onClick = onFriendsBottomBarItemClicked,
            ),
            add,
            BottomBarItem(
                icon = Filled.Public,
                label = stringResource(string.feature_feed_bottomBar_public),
                onClick = {},
                selected = true,
            ),
            BottomBarItem(
                icon = Filled.Event,
                label = stringResource(string.feature_feed_bottomBar_events),
                onClick = onEventsBottomBarItemClicked,
            ),
        ),
        topAppBarStyle = HOME,
    ) {
        PublicFeedPanel(
            paddingValues = it,
            data = data,
        )
    }
}

@Composable
private fun PublicFeedPanel(
    paddingValues: Rn3PaddingValues,
    data: PublicFeedData,
) {
    val phoneUtil = PhoneNumberUtil.getInstance()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(paddingValues),
    ) {
        data.posts.forEach { post ->
            Publication(
                post = post,
                friend = data.friends.find { it.uid == post.userId },
                enabled = if (data.user is SignedInUser || data.user is AnonymousUser) {
                    phoneUtil.isValidNumber(data.phone)
                } else {
                    false
                },
            )
        }
    }
}