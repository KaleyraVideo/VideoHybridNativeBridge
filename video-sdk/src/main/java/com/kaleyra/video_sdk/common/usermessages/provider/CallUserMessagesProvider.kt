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

@file:OptIn(FlowPreview::class)

package com.kaleyra.video_sdk.common.usermessages.provider

import com.kaleyra.video.conference.Call
import com.kaleyra.video_common_ui.CallUI
import com.kaleyra.video_common_ui.mapper.StreamMapper.amIWaitingOthers
import com.kaleyra.video_common_ui.mapper.StreamMapper.doOthersHaveStreams
import com.kaleyra.video_sdk.call.mapper.CallStateMapper.toCallStateUi
import com.kaleyra.video_sdk.call.mapper.InputMapper.toAudioConnectionFailureMessage
import com.kaleyra.video_sdk.call.mapper.InputMapper.toMutedMessage
import com.kaleyra.video_sdk.call.mapper.InputMapper.toUsbCameraMessage
import com.kaleyra.video_sdk.call.mapper.RecordingMapper.toRecordingMessage
import com.kaleyra.video_sdk.call.screen.model.CallStateUi
import com.kaleyra.video_sdk.common.usermessages.model.AlertMessage
import com.kaleyra.video_sdk.common.usermessages.model.RecordingMessage
import com.kaleyra.video_sdk.common.usermessages.model.UsbCameraMessage
import com.kaleyra.video_sdk.common.usermessages.model.UserMessage
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.concurrent.Executors

/**
 * Call User Messages Provider
 */
object CallUserMessagesProvider {

    private const val AM_I_LEFT_ALONE_DEBOUNCE_MILLIS = 5000L
    private const val AM_I_WAITING_FOR_OTHERS_DEBOUNCE_MILLIS = 3000L

    private var coroutineScope: CoroutineScope? = null

    private val userMessageChannel = Channel<UserMessage>(Channel.BUFFERED)

    private val _alertMessages: MutableStateFlow<Set<AlertMessage>> = MutableStateFlow(emptySet())

    /**
     * User messages flow
     */
    val userMessage: Flow<UserMessage> = userMessageChannel.receiveAsFlow()

    /**
     * Alert messages flow
     */
    val alertMessages: StateFlow<Set<AlertMessage>> = _alertMessages

    /**
     * Starts the call User Message Provider
     * @param call Flow<CallUI> the call flow
     * @param scope CoroutineScope optional coroutine scope in which to execute the observing
     */
    fun start(call: CallUI, scope: CoroutineScope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) + CoroutineName("CallUserMessagesProvider")) {
        if (coroutineScope != null) dispose()
        coroutineScope = scope

        userMessageChannel.sendRecordingEvents(call, scope)
        userMessageChannel.sendMutedEvents(call, scope)
        userMessageChannel.sendUsbCameraEvents(call, scope)
        userMessageChannel.sendFailedAudioOutputEvents(call, scope)

        _alertMessages.sendAutomaticRecordingAlertEvents(call, scope)
        _alertMessages.sendAmIAloneEvents(call, scope)
        _alertMessages.sendWaitingForOtherParticipantsEvents(call, scope)
    }

    /**
     * Send user message
     * @param userMessage UserMessage the user message to be sent
     */
    fun sendUserMessage(userMessage: UserMessage) {
        coroutineScope?.launch {
            userMessageChannel.send(userMessage)
        }
    }

    /**
     * Dispose User Message Provider
     */
    fun dispose() {
        _alertMessages.value = setOf()
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun Channel<UserMessage>.sendRecordingEvents(call: CallUI, scope: CoroutineScope) {
        call.toRecordingMessage()
            .dropWhile {
                it is RecordingMessage.Stopped
            }.onEach {
                send(it)
            }.launchIn(scope)
    }

    private fun Channel<UserMessage>.sendMutedEvents(call: CallUI, scope: CoroutineScope) {
        call.toMutedMessage().onEach { send(it) }.launchIn(scope)
    }

    private fun Channel<UserMessage>.sendUsbCameraEvents(call: CallUI, scope: CoroutineScope) {
        call.toUsbCameraMessage().dropWhile { it is UsbCameraMessage.Disconnected }.onEach { send(it) }.launchIn(scope)
    }

    private fun Channel<UserMessage>.sendFailedAudioOutputEvents(call: CallUI, scope: CoroutineScope) {
        call.toAudioConnectionFailureMessage().onEach { send(it) }.launchIn(scope)
    }

    private fun MutableStateFlow<Set<AlertMessage>>.sendAmIAloneEvents(call: CallUI, scope: CoroutineScope) {
        call.toCallStateUi()
            .takeWhile { it !is CallStateUi.Disconnecting && it !is CallStateUi.Disconnected.Ended }
            .filterNot { it is CallStateUi.Ringing || it is CallStateUi.RingingRemotely || it is CallStateUi.Dialing }
            .combine(call.doOthersHaveStreams()) { _, doOthersHaveStreams -> doOthersHaveStreams }
            .dropWhile { !it }
            .debounce { doOthersHaveStreams -> if (!doOthersHaveStreams) AM_I_LEFT_ALONE_DEBOUNCE_MILLIS else 0L }
            .onEach { doOthersHaveStreams ->
                val mutableList = value.toMutableSet()
                val newList = if (!doOthersHaveStreams) mutableList.plus(AlertMessage.LeftAloneMessage) else mutableList.minus(AlertMessage.LeftAloneMessage)
                value = newList
            }
            .onCompletion {
                value = value.toMutableSet().minus(AlertMessage.LeftAloneMessage)
            }
            .launchIn(scope)
    }

    private fun MutableStateFlow<Set<AlertMessage>>.sendWaitingForOtherParticipantsEvents(call: CallUI, scope: CoroutineScope) {
        call.state
            .filter { it == Call.State.Connected }
            .onEach {

                call
                    .amIWaitingOthers()
                    .combine(call.doOthersHaveStreams()) { amIWaitingOthers, doOthersHaveStreams ->
                        amIWaitingOthers to doOthersHaveStreams
                    }
                    .takeWhile { (_, doOthersHaveStreams) ->
                        !doOthersHaveStreams
                    }
                    .debounce { (amIWaitingOthers, _) ->
                        if (amIWaitingOthers) AM_I_WAITING_FOR_OTHERS_DEBOUNCE_MILLIS else 0L }
                    .onEach {
                        val mutableList = value.toMutableSet()
                        val newList = if (it.first) mutableList.plus(AlertMessage.WaitingForOtherParticipantsMessage) else mutableList.minus(AlertMessage.WaitingForOtherParticipantsMessage)
                        value = newList
                    }
                    .onCompletion {
                        value = value.toMutableSet().minus(AlertMessage.WaitingForOtherParticipantsMessage)
                    }
                    .launchIn(scope)

            }.launchIn(scope)
    }

    private fun MutableStateFlow<Set<AlertMessage>>.sendAutomaticRecordingAlertEvents(call: CallUI, scope: CoroutineScope) {
        call.recording.combine(call.toCallStateUi()) { recording, callStateUi ->
            recording to callStateUi
        }.filter { it.first.type is Call.Recording.Type.OnConnect }.onEach {
            val callStateUi = it.second

            val mutableList = value.toMutableSet()
            val newList = if (callStateUi is CallStateUi.Connecting) mutableList.plus(AlertMessage.AutomaticRecordingMessage) else mutableList.minus(AlertMessage.AutomaticRecordingMessage)
            value = newList
        }.launchIn(scope)
    }
}
