package rahmouni.neil.counters.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    title: String,
    secondary: String?,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(24.dp).testTag("HEADER_TITLE")
            )
            if (secondary != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        secondary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderExperiment(
    title: String,
    secondary: String?,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(24.dp).testTag("HEADER_TITLE")
            )
            if (secondary != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        secondary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 16.dp
                        )
                    )
                }
            }
        }
    }
}