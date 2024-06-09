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

package dev.rahmouni.neil.counters.core.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dev.rahmouni.neil.counters.core.auth.user.Rn3User
import dev.rahmouni.neil.counters.core.auth.user.Rn3User.AnonymousUser
import dev.rahmouni.neil.counters.core.auth.user.Rn3User.LoggedOutUser
import dev.rahmouni.neil.counters.core.auth.user.Rn3User.SignedInUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val WEB_CLIENT_ID = "743616795299-b7pstjgcvnq3sm77ia43vm66okovpejq.apps.googleusercontent.com"

/**
 * Implementation of `ConfigHelper` that fetches the value from the Firebase Remote Config backend.
 */
internal class FirebaseAuthHelper @Inject constructor() : AuthHelper {

    private var claims = MutableStateFlow<Map<String, Any>>(emptyMap())
    private var finishedLoading = false

    init {
        Firebase.auth.addAuthStateListener { auth ->
            auth.currentUser?.getIdToken(false)?.addOnSuccessListener {
                claims.compareAndSet(claims.value, it.claims)
            }
        }
    }

    override suspend fun quickFirstSignIn(context: Context) {
        try {
            signInWithCredentialManager(context, true)
        } catch (e: Exception) {
            when (e) {
                is NoCredentialException -> {
                    // It's ok to not have a credential already available
                    return
                }

                is GetCredentialCancellationException -> {
                    // It's ok to cancel the credential request
                    return
                }

                is GetCredentialProviderConfigurationException -> {
                    // Play services is not installed (or old version) AND is on an old Android version,
                    // or has a misconfig somewhere in the system stuff (custom ROM that removed the
                    // CredentialManager, ...)
                    return
                }

                else -> throw e
            }
        }
    }

    override suspend fun signIn(context: Context, anonymously: Boolean) {
        if (anonymously) {
            Firebase.auth.signInAnonymously()
        } else {
            signInWithCredentialManager(context, false)
        }
    }

    override suspend fun signOut(context: Context) {
        val task = Firebase.auth.signInAnonymously()
        task.await()
        if (task.isSuccessful && task.result.user != null) {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        }
    }

    override fun getUser(): Rn3User =
        if (finishedLoading) Firebase.auth.currentUser.toRn3User(claims.value) else Rn3User.Loading

    override fun getUserFlow(): Flow<Rn3User> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        awaitClose {
            Firebase.auth.removeAuthStateListener(authStateListener)
        }
    }.combine(claims) { user, claims ->
        finishedLoading = true
        user.toRn3User(claims)
    }

    private fun FirebaseUser?.toRn3User(claims: Map<String, Any>): Rn3User {
        with(this) {
            if (this == null) return LoggedOutUser
            if (isAnonymous) return AnonymousUser(uid)

            return SignedInUser(
                uid = uid,
                displayName = this.displayName.toString(),
                pfpUri = this.photoUrl,
                isAdmin = claims["role"] == "Admin",
            )
        }
    }

    private suspend fun signInWithCredentialManager(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
    ) =
        Firebase.auth.signInWithCredential(
            GoogleAuthProvider.getCredential(
                GoogleIdTokenCredential.createFrom(
                    CredentialManager
                        .create(context)
                        .getCredential(
                            context,
                            GetCredentialRequest.Builder()
                                .setPreferImmediatelyAvailableCredentials(
                                    filterByAuthorizedAccounts,
                                )
                                .addCredentialOption(
                                    GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(
                                            filterByAuthorizedAccounts,
                                        )
                                        .setServerClientId(WEB_CLIENT_ID)
                                        .setAutoSelectEnabled(filterByAuthorizedAccounts)
                                        .build(),
                                ).build(),
                        )
                        .credential
                        .data,
                ).idToken,
                null,
            ),
        )
}
