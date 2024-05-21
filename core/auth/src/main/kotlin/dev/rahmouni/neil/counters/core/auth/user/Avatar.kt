package dev.rahmouni.neil.counters.core.auth.user

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.NoAccounts
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.rahmouni.neil.counters.core.auth.user.Rn3User.LoggedOutUser
import dev.rahmouni.neil.counters.core.auth.user.Rn3User.SignedInUser

@Composable
fun Rn3User.Avatar() {
    when (this@Avatar) {
        is SignedInUser -> SubcomposeAsyncImage(
            model = this@Avatar.pfpUri,
            contentDescription = null,
            loading = { FallbackPfp() },
            error = { FallbackPfp() },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
        )

        LoggedOutUser -> Icon(Icons.Outlined.NoAccounts, null)
    }
}

@Composable
private fun FallbackPfp() {
    Icon(Icons.Outlined.AccountCircle, null)
}