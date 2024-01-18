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

package com.kaleyra.video_sdk.chat.appbar.model

import androidx.compose.runtime.Immutable

/**
 * Chat Participant State
 */
@Immutable
sealed class ChatParticipantState {

    /**
     * Online Chat Participant State
     */
    data object Online : ChatParticipantState()

    /**
     * Offline Chat Participant State
     * @property timestamp Long?
     * @constructor
     */
    data class Offline(val timestamp: Long? = null) : ChatParticipantState()

    /**
     * Typing Chat Participant State
     */
    data object Typing : ChatParticipantState()

    /**
     * Unknown Chat Participant State
     */
    data object Unknown : ChatParticipantState()
}
