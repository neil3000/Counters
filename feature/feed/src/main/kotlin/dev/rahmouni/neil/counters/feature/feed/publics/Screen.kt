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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.rahmouni.neil.counters.core.designsystem.BottomBarItem
import dev.rahmouni.neil.counters.core.designsystem.TopAppBarAction
import dev.rahmouni.neil.counters.core.designsystem.component.Rn3Scaffold
import dev.rahmouni.neil.counters.core.designsystem.component.TopAppBarStyle.HOME
import dev.rahmouni.neil.counters.core.designsystem.component.getHaptic
import dev.rahmouni.neil.counters.core.designsystem.component.tile.Rn3TileHorizontalDivider
import dev.rahmouni.neil.counters.core.designsystem.component.tile.Rn3TileHorizontalDividerDefaults
import dev.rahmouni.neil.counters.core.designsystem.icons.HumanGreetingProximity
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.Rn3PaddingValues
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.padding
import dev.rahmouni.neil.counters.core.designsystem.rebased.FeedType
import dev.rahmouni.neil.counters.core.designsystem.rebased.Friend
import dev.rahmouni.neil.counters.core.designsystem.rebased.Post
import dev.rahmouni.neil.counters.core.designsystem.rebased.PostType
import dev.rahmouni.neil.counters.core.designsystem.rebased.SharingScope
import dev.rahmouni.neil.counters.core.feedback.FeedbackContext.FeedbackScreenContext
import dev.rahmouni.neil.counters.core.feedback.navigateToFeedback
import dev.rahmouni.neil.counters.core.ui.TrackScreenViewEvent
import dev.rahmouni.neil.counters.core.user.Rn3User.SignedInUser
import dev.rahmouni.neil.counters.feature.feed.R.string
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedUiState.Loading
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedUiState.Success
import dev.rahmouni.neil.counters.feature.feed.publics.model.PublicFeedViewModel
import dev.rahmouni.neil.counters.feature.feed.publics.model.data.PublicFeedData
import dev.rahmouni.neil.counters.feature.feed.ui.Publication
import java.time.LocalDateTime

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
            modifier,
            (uiState as Success).publicFeedData,
            feedbackTopAppBarAction = FeedbackScreenContext(
                "LocalFeedScreen",
                "PkS4cSDUBdi2IvRegPIEe46xgk8Bf7h8",
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
    val haptics = getHaptic()
    val context = LocalContext.current

    val add = when (data.user) {
        is SignedInUser -> BottomBarItem(
            icon = Filled.Add,
            label = stringResource(string.feature_feed_bottomBar_add),
            onClick = onAddBottomBarItemClicked,
            unselectedIconColor = Color(color = 0xFFE8175D),
            fullSize = true,
        )

        else -> BottomBarItem(
            icon = Filled.Add,
            label = stringResource(string.feature_feed_bottomBar_add),
            onClick = {
                haptics.click()

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
                Outlined.Settings,
                stringResource(string.feature_feed_topAppBarActions_settings),
                onSettingsTopAppBarActionClicked,
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
            it,
        )
    }
}

@Composable
private fun PublicFeedPanel(
    paddingValues: Rn3PaddingValues,
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(paddingValues),
    ) {
        listOf(
            Post(
                id = "test",
                userId = "test",
                sharingScope = SharingScope.STREET,
                location = "Street King Charles",
                timestamp = LocalDateTime.now(),
                content = "The Street King Charles was under construction, but now it's all clear! The renovation has finished, making this spot more accessible and enjoyable. Explore the new look of this iconic street and join me on this adventure.",
                postType = PostType.TEXT,
                feed = FeedType.PUBLIC,
            ),
            Post(
                id = "test",
                userId = "test",
                sharingScope = SharingScope.CITY,
                location = "London",
                timestamp = LocalDateTime.now().minusMinutes(30),
                content = "I'm selling my road bike in excellent condition! It's perfect for anyone looking to explore the city or commute efficiently. Details: Brand - Trek, Model - Emonda, Year - 2020, Color - Black. Contact me if interested!",
                postType = PostType.BUTTONS,
                additionalInfos = listOf("I'm interested"),
                feed = FeedType.PUBLIC,
            ),
            Post(
                id = "test",
                userId = "test",
                sharingScope = SharingScope.COUNTRY,
                location = "UK",
                timestamp = LocalDateTime.now().minusHours(3),
                content = "Exciting news for nature enthusiasts! England welcomes its newest national park, providing vast spaces for hiking, wildlife exploration, and stunning scenery. Discover the endless trails and the beauty of our protected lands. Join us in celebrating this great addition to our national heritage.",
                postType = PostType.TEXT,
                feed = FeedType.PUBLIC,
            ),
            Post(
                id = "test",
                userId = "4",
                sharingScope = SharingScope.BUILDING,
                location = "221B Baker Street",
                timestamp = LocalDateTime.now().minusDays(1),
                content = "Seems like there's an impromptu concert every night next door! The music and noise levels from my neighbors have become a real challenge.",
                postType = PostType.TEXT,
                feed = FeedType.PUBLIC,
            ),
            Post(
                id = "test",
                userId = "test",
                sharingScope = SharingScope.DISTRICT,
                location = "Chelsea",
                timestamp = LocalDateTime.now().minusDays(5),
                content = "Is anyone else experiencing a power outage in Chelsea?",
                postType = PostType.POLL,
                additionalInfos = listOf("Yes", "No"),
                feed = FeedType.PUBLIC,
            ),
        ).forEach { post ->
            Publication(post, UserRepository.friends)
            Rn3TileHorizontalDivider(
                paddingValues = Rn3TileHorizontalDividerDefaults.paddingValues.copy(
                    top = 0.dp,
                    bottom = 0.dp,
                ),
            )
        }
    }
}

object UserRepository {
    val friends = listOf(
        Friend(
            userId = "1",
            name = "Alice Smith",
            email = "alice.smith@example.com",
            phone = "123-456-7890",
        ),
        Friend(
            userId = "2",
            name = "Bob Johnson",
            email = "bob.johnson@example.com",
            phone = "234-567-8901",
        ),
        Friend(
            userId = "3",
            name = "Carol Williams",
            email = "carol.williams@example.com",
            phone = "345-678-9012",
        ),
        Friend(
            userId = "4",
            name = "David Brown",
            email = "david.brown@example.com",
            phone = "456-789-0123",
        ),
        Friend(
            userId = "5",
            name = "Eva Davis",
            email = "eva.davis@example.com",
            phone = "567-890-1234",
        ),
    )
}