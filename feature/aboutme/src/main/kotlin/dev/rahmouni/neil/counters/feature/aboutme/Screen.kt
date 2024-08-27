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

package dev.rahmouni.neil.counters.feature.aboutme

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.TwoPaneStrategy
import com.google.accompanist.adaptive.VerticalTwoPaneStrategy
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.initialize
import dev.rahmouni.neil.counters.core.designsystem.Rn3PreviewScreen
import dev.rahmouni.neil.counters.core.designsystem.Rn3PreviewUiStates
import dev.rahmouni.neil.counters.core.designsystem.Rn3Theme
import dev.rahmouni.neil.counters.core.designsystem.TopAppBarAction
import dev.rahmouni.neil.counters.core.designsystem.component.Rn3Scaffold
import dev.rahmouni.neil.counters.core.designsystem.component.Rn3SystemBarSpacer
import dev.rahmouni.neil.counters.core.designsystem.component.TopAppBarStyle
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.Rn3PaddingValues
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.Rn3PaddingValuesDirection.TOP
import dev.rahmouni.neil.counters.core.designsystem.paddingValues.padding
import dev.rahmouni.neil.counters.core.feedback.FeedbackContext.FeedbackScreenContext
import dev.rahmouni.neil.counters.core.feedback.navigateToFeedback
import dev.rahmouni.neil.counters.feature.aboutme.model.AboutMeUiState
import dev.rahmouni.neil.counters.feature.aboutme.model.AboutMeUiState.Loading
import dev.rahmouni.neil.counters.feature.aboutme.model.AboutMeUiState.Success
import dev.rahmouni.neil.counters.feature.aboutme.model.AboutMeViewModel
import dev.rahmouni.neil.counters.feature.aboutme.model.data.AboutMeData
import dev.rahmouni.neil.counters.feature.aboutme.model.data.AboutMeDataPreviewParameterProvider
import dev.rahmouni.neil.counters.feature.aboutme.model.data.PreviewParameterData.aboutMeData_default
import dev.rahmouni.neil.counters.feature.aboutme.ui.Biography
import dev.rahmouni.neil.counters.feature.aboutme.ui.LoadingPfp
import dev.rahmouni.neil.counters.feature.aboutme.ui.MainActions
import dev.rahmouni.neil.counters.feature.aboutme.ui.SocialLinks

@Composable
internal fun AboutMeRoute(
    modifier: Modifier = Modifier,
    viewModel: AboutMeViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (!FirebaseApp.getApps(context).any { it.name == "RahNeil_N3" }) {
        val options = FirebaseOptions.Builder()
            .setProjectId("rahneil-n3")
            .setApplicationId("1:818465066811:android:c519829569270bdba7b11e")
            .setApiKey("AIzaSyCILHyvBfAdZ9bo7njs0hqY5dV2z5gxvnE")
            .build()
        Firebase.initialize(context, options, "RahNeil_N3")
    }

    AboutMeScreen(
        modifier,
        uiState,
        onBackIconButtonClicked = navController::popBackStack,
        feedbackTopAppBarAction = FeedbackScreenContext(
            "AccessibilitySettingsScreen",
            "jrKt4Xe58KDipPJsm1iPUijn6BMsNc8g",
        ).toTopAppBarAction(navController::navigateToFeedback),
    )
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun AboutMeScreen(
    modifier: Modifier = Modifier,
    uiState: AboutMeUiState,
    onBackIconButtonClicked: () -> Unit = {},
    feedbackTopAppBarAction: TopAppBarAction? = null,
) {
    Rn3Scaffold(
        modifier = modifier,
        topAppBarTitle = stringResource(R.string.feature_aboutme_aboutMeScreen_scaffold_title),
        onBackIconButtonClicked = onBackIconButtonClicked,
        topAppBarActions = listOfNotNull(feedbackTopAppBarAction),
        topAppBarStyle = TopAppBarStyle.SMALL,
    ) {
        val aboutMeData = if (uiState is Success) uiState.aboutMeData else null

        when {
            currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != COMPACT ->
                TwoPanePanel(
                    it,
                    aboutMeData,
                    HorizontalTwoPaneStrategy(.4f),
                )

            currentWindowAdaptiveInfo().windowPosture.isTabletop ->
                TwoPanePanel(
                    it,
                    aboutMeData,
                    VerticalTwoPaneStrategy(.4f),
                )

            else -> ColumnPanel(it, aboutMeData)
        }
    }
}

@Composable
private fun TwoPanePanel(
    paddingValues: Rn3PaddingValues,
    aboutMeData: AboutMeData?,
    strategy: TwoPaneStrategy,
) {
    val context = LocalContext.current

    var finishedLoadingAnimation by remember { mutableStateOf(aboutMeData != null) }

    TwoPane(
        first = {
            LoadingPfp(
                Modifier
                    .fillMaxHeight()
                    .padding(paddingValues.only(TOP)),
                aboutMeData?.pfp,
                finishedLoadingAnimation,
            ) { finishedLoadingAnimation = true }
        },
        second = {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                AnimatedVisibility(
                    visible = finishedLoadingAnimation,
                    enter = fadeIn() + expandVertically(),
                ) {
                    Column {
                        if (aboutMeData != null) {
                            Biography(aboutMeData.bioShort)
                            MainActions(aboutMeData.portfolioUri)
                            SocialLinks(aboutMeData.socialLinks)
                            Rn3SystemBarSpacer()
                        }
                    }
                }
            }
        },
        strategy = strategy,
        displayFeatures = calculateDisplayFeatures(activity = context as Activity),
        modifier = Modifier.padding(paddingValues),
    )
}

@Composable
private fun ColumnPanel(paddingValues: Rn3PaddingValues, aboutMeData: AboutMeData?) {
    var finishedLoadingAnimation by remember { mutableStateOf(aboutMeData != null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues.add(horizontal = 24.dp)),
        verticalArrangement = Arrangement.Center,
    ) {
        LoadingPfp(
            pfp = aboutMeData?.pfp,
            finishedLoadingAnimation = finishedLoadingAnimation,
        ) { finishedLoadingAnimation = true }

        AnimatedVisibility(visible = !finishedLoadingAnimation) {
            Spacer(modifier = Modifier.height(paddingValues.top))
        }

        AnimatedVisibility(
            visible = finishedLoadingAnimation,
            enter = fadeIn() + expandVertically(),
        ) {
            Column(Modifier.padding(top = 24.dp)) {
                if (aboutMeData != null) {
                    Biography(aboutMeData.bioShort)
                    MainActions(aboutMeData.portfolioUri)
                    SocialLinks(aboutMeData.socialLinks)
                    Rn3SystemBarSpacer()
                }
            }
        }
    }
}

@Rn3PreviewScreen
@Composable
private fun Default() {
    Rn3Theme {
        AboutMeScreen(uiState = Success(aboutMeData_default))
    }
}

@Rn3PreviewUiStates
@Composable
private fun Loading() {
    Rn3Theme {
        AboutMeScreen(uiState = Loading)
    }
}

@Rn3PreviewUiStates
@Composable
private fun UiStates(
    @PreviewParameter(AboutMeDataPreviewParameterProvider::class)
    aboutMeData: AboutMeData,
) {
    Rn3Theme {
        AboutMeScreen(uiState = Success(aboutMeData))
    }
}
