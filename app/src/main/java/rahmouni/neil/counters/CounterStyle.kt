package rahmouni.neil.counters

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class CounterStyle {
    DEFAULT, SECONDARY, PRIMARY, TERTIARY;

    @Composable
    fun getBackGroundColor(): Color {
        if (this == PRIMARY) return MaterialTheme.colorScheme.primaryContainer
        if (this == SECONDARY) return MaterialTheme.colorScheme.secondaryContainer
        if (this == TERTIARY) return MaterialTheme.colorScheme.tertiaryContainer
        return MaterialTheme.colorScheme.surfaceVariant
    }
}