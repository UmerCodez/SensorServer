/*
 *     This file is a part of SensorServer (https://www.github.com/UmerCodez/SensorServer)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     SensorServer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SensorServer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SensorServer. If not, see <https://www.gnu.org/licenses/>.
 *
 */
package github.umer0586.sensorserver.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextPref(
    modifier: Modifier = Modifier,
    value: String,
    password: Boolean = false,
    title: @Composable () -> Unit,
    isError: ((String) -> Boolean)? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onUpdateClick: ((String) -> Unit)? = null
) {

    var showModal by rememberSaveable { mutableStateOf(false) }

    ListItem(
        modifier = modifier,
        headlineContent = title,
        supportingContent = {
            if (password) {
                Text("*".repeat(value.length))
            } else {
                Text(value)
            }
        },
        trailingContent = {
            IconButton(
                onClick = { showModal = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }
        }
    )

    if(showModal){
        ModalBottomSheet(
            modifier = modifier.fillMaxWidth(),
            onDismissRequest = { showModal = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var value by rememberSaveable { mutableStateOf(value) }

                title()

                val isError = isError?.invoke(value) ?: false

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = value,
                    isError = isError,
                    onValueChange = { value = it},
                    keyboardOptions = if (password) KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ) else keyboardOptions,
                    visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                    shape = RoundedCornerShape(30.dp),
                    supportingText = {
                        if (isError && errorText != null) {
                            Text(errorText)
                        }
                    },
                    singleLine = true,

                    )

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        onUpdateClick?.invoke(value)
                        showModal = false
                    },
                    enabled = !isError
                ) { Text("Update") }
            }
        }
    }

}

@Preview
@Composable
fun EdittextPrefPreview(){
    SensorServerTheme {
        EditTextPref(
            value = "192.168.19.1",
            title = { Text("Broker Address") },
            onUpdateClick = {  }
        )
    }
}