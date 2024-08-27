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

package dev.rahmouni.neil.counters.feature.settings.main.model.data

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.rahmouni.neil.counters.core.user.Rn3User.AnonymousUser
import dev.rahmouni.neil.counters.core.user.Rn3User.SignedInUser
import dev.rahmouni.neil.counters.feature.settings.main.model.data.InAppUpdateState.NoUpdateAvailable
import dev.rahmouni.neil.counters.feature.settings.main.model.data.InAppUpdateState.UpdateAvailable
import dev.rahmouni.neil.counters.feature.settings.main.model.data.PreviewParameterData.settingsData_default
import dev.rahmouni.neil.counters.feature.settings.main.model.data.PreviewParameterData.settingsData_mutations

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [SettingsData] for Composable previews.
 */
class SettingsDataPreviewParameterProvider :
    PreviewParameterProvider<SettingsData> {
    override val values: Sequence<SettingsData> =
        sequenceOf(settingsData_default).plus(settingsData_mutations)
}

object PreviewParameterData {
    val settingsData_default = SettingsData(
        user = SignedInUser(
            uid = "androidPreviewID",
            displayName = "Android Preview",
            pfpUri = null,
            isAdmin = false,
            email = "androidPreview@rahmouni.dev",
        ),
        devSettingsEnabled = false,
        inAppUpdateData = NoUpdateAvailable,
    )
    val settingsData_mutations = with(settingsData_default) {
        sequenceOf(
            copy(
                user = AnonymousUser(
                    uid = "androidPreviewID",
                ),
            ),
            copy(devSettingsEnabled = true),
            copy(inAppUpdateData = UpdateAvailable(null, null)),
        )
    }
}
