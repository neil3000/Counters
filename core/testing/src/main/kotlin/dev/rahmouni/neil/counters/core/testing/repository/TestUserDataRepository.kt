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

package dev.rahmouni.neil.counters.core.testing.repository

import dev.rahmouni.neil.counters.core.data.repository.userData.UserDataRepository
import dev.rahmouni.neil.counters.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

val emptyUserData = UserData(
    hasAccessibilityEmphasizedSwitchesEnabled = false,
    hasAccessibilityIconTooltipsEnabled = true,
    hasMetricsEnabled = true,
    hasCrashlyticsEnabled = true,
    shouldShowLoginScreenOnStartup = false,
    isAppFirstLaunch = false,
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setAccessibilityEmphasizedSwitches(value: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(hasAccessibilityEmphasizedSwitchesEnabled = value))
        }
    }

    override suspend fun setAccessibilityIconTooltips(value: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(hasAccessibilityIconTooltipsEnabled = value))
        }
    }

    override suspend fun setMetricsEnabled(value: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(hasMetricsEnabled = value))
        }
    }

    override suspend fun setCrashlyticsEnabled(value: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(hasCrashlyticsEnabled = value))
        }
    }

    override suspend fun setShouldShowLoginScreenOnStartup(value: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldShowLoginScreenOnStartup = value))
        }
    }

    override suspend fun setNotAppFirstLaunch() {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(isAppFirstLaunch = false))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
