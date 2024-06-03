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

package dev.rahmouni.neil.counters.feature.dashboard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rahmouni.neil.counters.core.data.repository.CountersDataRepository
import dev.rahmouni.neil.counters.feature.dashboard.model.data.DashboardData
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val countersDataRepository: CountersDataRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        countersDataRepository.userCounters.map { counters ->
            DashboardUiState(
                dashboardData = DashboardData(
                    counters = counters,
                ),
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = DashboardUiState(
                DashboardData(
                    counters = emptyList(),
                ),
            ),
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
        )

    fun createUserCounter(counterDataFields: Map<String, Any>) {
        viewModelScope.launch {
            countersDataRepository.createUserCounter(counterDataFields)
        }
    }

    fun incrementUserCounter(id: String) {
        countersDataRepository.createUserCounterIncrement(id, 1)
    }
}
