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

package dev.rahmouni.neil.counters.feature.settings.developer.links.model.data

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.rahmouni.neil.counters.core.data.model.LinkRn3UrlData
import dev.rahmouni.neil.counters.feature.settings.developer.links.model.data.PreviewParameterData.linksData_default

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [DeveloperSettingsLinksData] for Composable previews.
 */
class LinksDataPreviewParameterProvider :
    PreviewParameterProvider<DeveloperSettingsLinksData> {
    override val values: Sequence<DeveloperSettingsLinksData> = sequenceOf(linksData_default)
}

object PreviewParameterData {
    val linksData_default = DeveloperSettingsLinksData(
        links = listOf(
            LinkRn3UrlData(
                path = "install",
                description = "",
                redirectUrl = "https://www.google.com",
                clicks = 120,
            ),
            LinkRn3UrlData(
                path = "changelog",
                description = "Changelog of the app",
                redirectUrl = "https://www.google.com",
                clicks = 28,
            ),
        ),
    )
}
