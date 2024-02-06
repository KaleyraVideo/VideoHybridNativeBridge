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

package com.kaleyra.video_sdk.call.callactionsm3.model

import androidx.compose.runtime.Immutable
import com.kaleyra.video_sdk.call.callactions.model.CallAction
import com.kaleyra.video_sdk.call.callactions.model.CallActionsUiState
import com.kaleyra.video_sdk.common.uistate.UiState
import com.kaleyra.video_sdk.common.immutablecollections.ImmutableList

@Immutable
data class CallActionsM3UiState(
    val primaryActionList: ImmutableList<CallAction> = ImmutableList(emptyList()),
    val secondaryActionList: ImmutableList<CallAction> = ImmutableList(emptyList()),
    val message: String? = null
) : UiState {


//    fun copy(primaryActionList: ImmutableList<CallAction> = ImmutableList(emptyList()),
//             secondaryActionList: ImmutableList<CallAction> = ImmutableList(emptyList()),
//             message: String? = null): CallActionsM3UiState = CallActionsM3UiState(primaryActionList, secondaryActionList, message)
}