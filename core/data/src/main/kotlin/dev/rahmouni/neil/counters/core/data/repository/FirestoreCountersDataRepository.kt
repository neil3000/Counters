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

package dev.rahmouni.neil.counters.core.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.rahmouni.neil.counters.core.data.model.CounterData
import dev.rahmouni.neil.counters.core.data.model.CounterDataFields
import dev.rahmouni.neil.counters.core.data.model.IncrementDataFields
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

class FirestoreCountersDataRepository @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : CountersDataRepository {

    override val userCounters: Flow<List<CounterData>> =
        userDataRepository.userData.transformLatest { user ->
            emitAll(
                Firebase.firestore
                    .collection("counters")
                    .whereEqualTo(CounterDataFields.ownerUserUid, user.lastUserUid)
                    .dataObjects<CounterData>(),
            )
        }

    override suspend fun createUserCounter(title: String) {
        coroutineScope {
            userDataRepository.userData.stateIn(this).value.let { userData ->
                Firebase.firestore
                    .collection("counters")
                    .add(
                        with(CounterDataFields) {
                            hashMapOf(
                                this.ownerUserUid to userData.lastUserUid,
                                this.title to title,
                                this.currentValue to 0,
                            )
                        },
                    )
            }
        }
    }

    override fun createUserCounterIncrement(counterUid: String, value: Int) {
        Firebase.firestore
            .collection("counters")
            .document(counterUid)
            .let {
                it.update(
                    CounterDataFields.currentValue,
                    FieldValue.increment(value.toLong()),
                )
                it.collection("increments").add(
                    with(IncrementDataFields) {
                        hashMapOf(
                            this.value to value,
                            this.createdAt to Timestamp.now(),
                        )
                    },
                )
            }
    }
}
