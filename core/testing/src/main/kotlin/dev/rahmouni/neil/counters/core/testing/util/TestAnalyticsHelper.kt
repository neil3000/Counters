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

package dev.rahmouni.neil.counters.core.testing.util

import dev.rahmouni.neil.counters.core.analytics.AnalyticsEvent
import dev.rahmouni.neil.counters.core.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    override var appInstallationID: String = "Not available in Tests"

    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    override fun clearMetrics() {}

    fun hasLogged(event: AnalyticsEvent) = event in events
}
