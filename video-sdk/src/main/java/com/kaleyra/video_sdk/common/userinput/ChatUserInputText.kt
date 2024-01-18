/*
 * Copyright 2023 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaleyra.video_sdk.common.userinput

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.chat.input.TextFieldTag
import com.kaleyra.video_sdk.theme.KaleyraM3Theme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ChatUserInputText(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    maxLines: Int = Int.MAX_VALUE,
    onDirectionLeft: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .then(modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        OutlinedTextField(
            value = textFieldValue,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            onValueChange = { onTextChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TextFieldTag)
                .onPreviewKeyEvent {
                    if (it.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (it.key) {
                        Key.Tab -> {
                            focusManager.moveFocus(FocusDirection.Next); true
                        }

                        Key.DirectionUp -> {
                            focusManager.moveFocus(FocusDirection.Up); true
                        }

                        Key.DirectionDown -> {
                            focusManager.moveFocus(FocusDirection.Down); true
                        }

                        Key.DirectionRight -> {
                            focusManager.moveFocus(FocusDirection.Right); true
                        }

                        Key.DirectionLeft -> {
                            onDirectionLeft.invoke(); true
                        }

                        else -> false
                    }
                },
            interactionSource = interactionSource,
            maxLines = maxLines,
            placeholder = { Text(text = stringResource(id = R.string.kaleyra_edit_text_input_placeholder)) }
        )
    }
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
internal fun ChatUserInputPreview() = KaleyraM3Theme {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        var textState by remember { mutableStateOf(TextFieldValue()) }
        ChatUserInputText(textFieldValue = textState, onDirectionLeft = {}, onTextChanged = {})
    }
}
