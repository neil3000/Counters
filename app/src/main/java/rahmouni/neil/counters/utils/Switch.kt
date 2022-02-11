package rahmouni.neil.counters.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import rahmouni.neil.counters.ui.theme.CountersTheme

@Composable
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary
    ),
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    androidx.compose.material.Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = colors,
    )
}

@Preview(showBackground = true)
@Composable
fun SwitchPreview() {
    CountersTheme {
        Switch(false) {}
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchPreviewChecked() {
    CountersTheme {
        Switch(true) {}
    }
}