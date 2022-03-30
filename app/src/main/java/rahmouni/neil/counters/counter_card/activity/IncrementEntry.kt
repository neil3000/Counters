package rahmouni.neil.counters.counter_card.activity

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rahmouni.neil.counters.R
import rahmouni.neil.counters.ResetType
import rahmouni.neil.counters.database.CountersListViewModel
import rahmouni.neil.counters.database.Increment
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IncrementEntry(
    increment: Increment,
    countersListViewModel: CountersListViewModel,
    resetType: ResetType = ResetType.NEVER
) {
    val localHapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date? = format.parse(increment.timestamp)

    ListItem(
        text = { Text(increment.value.toString()) },
        secondaryText = {
            Text(
                if (date != null) {
                    when (resetType) {
                        ResetType.NEVER -> DateUtils.getRelativeTimeSpanString(date.time).toString()
                        ResetType.DAY -> DateFormat.getTimeFormat(context).format(date)
                        ResetType.WEEK -> DateFormat.format(
                            DateFormat.getBestDateTimePattern(
                                Locale.getDefault(),
                                "EEEE"
                            ), date
                        ).toString()
                            .replaceFirstChar { it.uppercase() } + ", " + DateFormat.getTimeFormat(
                            context
                        ).format(date)
                        ResetType.MONTH -> DateFormat.format(
                            DateFormat.getBestDateTimePattern(
                                Locale.getDefault(),
                                "EEEE d"
                            ), date
                        ).toString()
                            .replaceFirstChar { it.uppercase() } + ", " + DateFormat.getTimeFormat(
                            context
                        ).format(date)
                    }
                } else "Error"
            )
        },
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