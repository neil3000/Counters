package rahmouni.neil.counters.counter_card

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rahmouni.neil.counters.R
import rahmouni.neil.counters.database.CountersListViewModel
import rahmouni.neil.counters.database.Increment

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IncrementEntry(increment: Increment, countersListViewModel: CountersListViewModel) {
    val localHapticFeedback = LocalHapticFeedback.current

    val scope = rememberCoroutineScope()

    Surface {
        ListItem(
            text = { Text(increment.value.toString()) },
            secondaryText = { Text(increment.timestamp) },
            trailing = {
                IconButton(onClick = {
                    localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                    scope.launch {
                        countersListViewModel.deleteIncrement(increment)
                    }
                }) {
                    Icon(
                        Icons.Outlined.Delete,
                        stringResource(R.string.action_deleteEntry),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}