package rahmouni.neil.counters.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlusOne
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rahmouni.neil.counters.IncrementType
import rahmouni.neil.counters.IncrementValueType

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class
)
@Composable
fun IncrementValueOption(
    incrementType: IncrementType,
    incrementValueType: IncrementValueType,
    incrementValue: Int,
    hasMinus: Boolean,
    inModal: Boolean = false,
    onSave: (IncrementValueType, Int) -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var dialogIncrementValueType by rememberSaveable { mutableStateOf(IncrementValueType.VALUE) }
    var dialogIncrementValue by rememberSaveable { mutableStateOf("1") }
    var isDialogIncrementValueError by rememberSaveable { mutableStateOf(false) }

    val localHapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun validateDialogIncrementValue(text: String): Boolean {
        isDialogIncrementValueError = text.toIntOrNull() == null || text.toInt() <= 0
        return !isDialogIncrementValueError
    }

    val titleStr = if (incrementType == IncrementType.VALUE) { //TODO i18n
        if (hasMinus) "Increase/decrease by" else "Increase by"
    } else {
        "Default value when asking"
    }

    fun confirm() {
        localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

        if (validateDialogIncrementValue(dialogIncrementValue)) {
            keyboardController?.hide()

            onSave(dialogIncrementValueType, dialogIncrementValue.toInt())

            openDialog = false
        }
    }

    androidx.compose.material.Surface {
        ListItem(
            text = { androidx.compose.material.Text(titleStr) },
            secondaryText = {
                androidx.compose.material.Text(
                    incrementValueType.description.replace("%s", incrementValue.toString())
                )
            },
            icon = if (!inModal) {
                { Icon(Icons.Outlined.PlusOne, null) }
            } else null,
            modifier = Modifier
                .clickable(
                    onClick = {
                        localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        dialogIncrementValueType = incrementValueType
                        dialogIncrementValue = incrementValue.toString()
                        isDialogIncrementValueError = false
                        openDialog = true
                    }
                )
                .padding(if (inModal) 8.dp else 0.dp)
        )
    }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
            },
            title = {
                Text(titleStr)
            },
            text = {
                Column {
                    IncrementValueType.values().forEach {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = dialogIncrementValueType == it,
                                    onClick = {
                                        localHapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )

                                        dialogIncrementValueType = it
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),

                            ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(56.dp)
                            ) {
                                RadioButton(
                                    selected = dialogIncrementValueType == it,
                                    onClick = null // null recommended for accessibility with screenreaders
                                )
                                Text(
                                    text = it.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            if (it.hasValue) {
                                OutlinedTextField(
                                    value = dialogIncrementValue,
                                    enabled = dialogIncrementValueType == it,
                                    onValueChange = { str ->
                                        isDialogIncrementValueError = false
                                        dialogIncrementValue = str
                                    },
                                    singleLine = true,
                                    isError = isDialogIncrementValueError,
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    keyboardActions = KeyboardActions { confirm() })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isDialogIncrementValueError,
                    onClick = { confirm() }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        openDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}