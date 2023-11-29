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

package com.kaleyra.video_sdk.common.usermessages.model

import java.util.UUID

/**
 * Muted message
 * @property admin String? admin user identifier that triggered the mute
 * @property id String muted message identifier
 * @constructor
 */
class MutedMessage(val admin: String?) : UserMessage {

    override val id: String = UUID.randomUUID().toString()

    /**
     * @suppress
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MutedMessage

        if (admin != other.admin) return false
        if (id != other.id) return false

        return true
    }

    /**
     * @suppress
     */
    override fun hashCode(): Int {
        var result = admin?.hashCode() ?: 0
        result = 31 * result + id.hashCode()
        return result
    }
}
