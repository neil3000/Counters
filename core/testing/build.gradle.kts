/*
 * Copyright (C) 2024 Rahmouni Neïl & Aurélien Coppée
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

plugins {
    alias(libs.plugins.rn3.android.library)
    alias(libs.plugins.rn3.android.library.compose)
    alias(libs.plugins.rn3.android.hilt)
}

android {
    namespace = "dev.rahmouni.neil.counters.core.testing"
}

dependencies {
    api(kotlin("test"))
    api(libs.androidx.compose.ui.test)
    api(projects.core.analytics)
    api(projects.core.data)
    api(projects.core.model)

    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.datetime)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(libs.libphonenumber)
}
