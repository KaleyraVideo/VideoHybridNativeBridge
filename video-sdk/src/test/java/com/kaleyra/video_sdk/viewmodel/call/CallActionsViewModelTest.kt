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

package com.kaleyra.video_sdk.viewmodel.call

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.kaleyra.video.Company
import com.kaleyra.video.Contact
import com.kaleyra.video.conference.Call
import com.kaleyra.video.conference.CallParticipant
import com.kaleyra.video.conference.CallParticipants
import com.kaleyra.video.conference.Input
import com.kaleyra.video.conference.Inputs
import com.kaleyra.video.conference.Stream
import com.kaleyra.video_common_ui.CallUI
import com.kaleyra.video_common_ui.ConferenceUI
import com.kaleyra.video_common_ui.ConversationUI
import com.kaleyra.video_common_ui.call.CameraStreamConstants.CAMERA_STREAM_ID
import com.kaleyra.video_common_ui.connectionservice.ConnectionServiceUtils
import com.kaleyra.video_common_ui.connectionservice.ConnectionServiceUtils.isConnectionServiceEnabled
import com.kaleyra.video_common_ui.connectionservice.KaleyraCallConnectionService
import com.kaleyra.video_sdk.MainDispatcherRule
import com.kaleyra.video_sdk.call.audiooutput.model.AudioDeviceUi
import com.kaleyra.video_sdk.call.bottomsheetnew.inputmessage.model.CameraMessage
import com.kaleyra.video_sdk.call.bottomsheetnew.inputmessage.model.InputMessage
import com.kaleyra.video_sdk.call.bottomsheetnew.inputmessage.model.MicMessage
import com.kaleyra.video_sdk.call.callactions.viewmodel.CallActionsViewModel
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper.toCurrentAudioDeviceUi
import com.kaleyra.video_sdk.call.mapper.CallActionsMapper
import com.kaleyra.video_sdk.call.mapper.CallActionsMapper.toCallActions
import com.kaleyra.video_sdk.call.mapper.CallStateMapper
import com.kaleyra.video_sdk.call.mapper.CallStateMapper.toCallStateUi
import com.kaleyra.video_sdk.call.mapper.InputMapper
import com.kaleyra.video_sdk.call.mapper.InputMapper.hasUsbCamera
import com.kaleyra.video_sdk.call.mapper.InputMapper.isMyCameraEnabled
import com.kaleyra.video_sdk.call.mapper.InputMapper.isMyMicEnabled
import com.kaleyra.video_sdk.call.mapper.InputMapper.isSharingScreen
import com.kaleyra.video_sdk.call.mapper.ParticipantMapper
import com.kaleyra.video_sdk.call.mapper.ParticipantMapper.isMeParticipantInitialized
import com.kaleyra.video_sdk.call.mapper.VirtualBackgroundMapper
import com.kaleyra.video_sdk.call.mapper.VirtualBackgroundMapper.isVirtualBackgroundEnabled
import com.kaleyra.video_sdk.call.screen.model.CallStateUi
import com.kaleyra.video_sdk.call.screennew.AudioAction
import com.kaleyra.video_sdk.call.screennew.CallActionUI
import com.kaleyra.video_sdk.call.screennew.CameraAction
import com.kaleyra.video_sdk.call.screennew.ChatAction
import com.kaleyra.video_sdk.call.screennew.FileShareAction
import com.kaleyra.video_sdk.call.screennew.FlipCameraAction
import com.kaleyra.video_sdk.call.screennew.HangUpAction
import com.kaleyra.video_sdk.call.screennew.MicAction
import com.kaleyra.video_sdk.call.screennew.ScreenShareAction
import com.kaleyra.video_sdk.call.screennew.VirtualBackgroundAction
import com.kaleyra.video_sdk.call.screennew.WhiteboardAction
import com.kaleyra.video_sdk.call.screenshare.viewmodel.ScreenShareViewModel.Companion.SCREEN_SHARE_STREAM_ID
import com.kaleyra.video_sdk.common.usermessages.model.CameraRestrictionMessage
import com.kaleyra.video_sdk.common.usermessages.provider.CallUserMessagesProvider
import com.kaleyra.video_sdk.ui.mockkSuccessfulConfiguration
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CallActionsViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CallActionsViewModel

    private val conferenceMock = mockk<ConferenceUI>()

    private val callMock = mockk<CallUI>(relaxed = true)

    @Before
    fun setUp() {
        mockkObject(CallActionsMapper)
        mockkObject(InputMapper)
        mockkObject(VirtualBackgroundMapper)
        mockkObject(ParticipantMapper)
        mockkObject(AudioOutputMapper)
        mockkObject(CallStateMapper)

        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf())
        every { callMock.isMyMicEnabled() } returns MutableStateFlow(true)
        every { callMock.isMyCameraEnabled() } returns MutableStateFlow(true)
        every { callMock.hasUsbCamera() } returns MutableStateFlow(false)
        every { callMock.isSharingScreen() } returns MutableStateFlow(false)
        every { callMock.isVirtualBackgroundEnabled() } returns MutableStateFlow(false)
        every { callMock.isMeParticipantInitialized() } returns MutableStateFlow(true)
        every { callMock.state } returns MutableStateFlow(Call.State.Connected)
        every { callMock.toCurrentAudioDeviceUi() } returns MutableStateFlow(AudioDeviceUi.Muted)
        every { callMock.toCallStateUi() } returns MutableStateFlow(CallStateUi.Disconnected)
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioVideo())

        every { conferenceMock.call } returns MutableStateFlow(callMock)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun testCallAnswer() = runTest {
        mockkObject(ConnectionServiceUtils) {
            every { isConnectionServiceEnabled } returns false
            viewModel = spyk(CallActionsViewModel{
                mockkSuccessfulConfiguration(conference = conferenceMock)
            })

            advanceUntilIdle()
            viewModel.accept()
            verify(exactly = 1) { callMock.connect() }
        }
    }

    @Test
    fun testConnectionServiceAnswer() = runTest {
        mockkObject(ConnectionServiceUtils, KaleyraCallConnectionService) {
            every { isConnectionServiceEnabled } returns true
            viewModel = spyk(CallActionsViewModel{
                mockkSuccessfulConfiguration(conference = conferenceMock)
            })

            advanceUntilIdle()
            viewModel.accept()
            advanceUntilIdle()
            coVerify(exactly = 1) { KaleyraCallConnectionService.answer() }
        }
    }

    @Test
    fun testHangUpWhenConnectionServiceIsDisabled() = runTest {
        mockkObject(ConnectionServiceUtils) {
            every { isConnectionServiceEnabled } returns false
            every { callMock.toCallStateUi() } returns MutableStateFlow(CallStateUi.Ringing)

            viewModel = spyk(CallActionsViewModel{
                mockkSuccessfulConfiguration(conference = conferenceMock)
            })

            advanceUntilIdle()
            viewModel.hangUp()

            verify(exactly = 1) { callMock.end() }
        }
    }

    @Test
    fun testHangUpWhenConnectionServiceIsEnabledAndCallIsRinging() = runTest {
        mockkObject(ConnectionServiceUtils, KaleyraCallConnectionService) {
            every { isConnectionServiceEnabled } returns true
            every { callMock.toCallStateUi() } returns MutableStateFlow(CallStateUi.Ringing)

            viewModel = spyk(CallActionsViewModel{
                mockkSuccessfulConfiguration(conference = conferenceMock)
            })

            advanceUntilIdle()
            viewModel.hangUp()
            advanceUntilIdle()
            coVerify(exactly = 1) { KaleyraCallConnectionService.reject() }
        }
    }

    @Test
    fun callStateRinging_isRingingIsTrue() = runTest {
        every { callMock.toCallStateUi() } returns MutableStateFlow(CallStateUi.Ringing)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        val current = viewModel.uiState.first().isRinging
        assertEquals(false, current)

        advanceUntilIdle()

        val new = viewModel.uiState.first().isRinging
        assertEquals(true, new)
    }

    @Test
    fun callStateNotRinging_isRingingIsFalse() = runTest {
        every { callMock.toCallStateUi() } returns MutableStateFlow(mockk<CallStateUi>())

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        val current = viewModel.uiState.first().isRinging
        assertEquals(false, current)

        advanceUntilIdle()

        val new = viewModel.uiState.first().isRinging
        assertEquals(false, new)
    }

    @Test
    fun testCallActionsUiState_actionsUpdated() = runTest {
        val actions = MutableStateFlow(listOf<CallActionUI>())
        every { callMock.toCallActions(any()) } returns actions

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        val current = viewModel.uiState.first().actionList.value
        assertEquals(listOf<CallActionUI>(), current)

        actions.value = listOf(AudioAction(), HangUpAction())
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(), HangUpAction())
        assertEquals(expected, new)
    }

    @Test
    fun testCallActionsUiState_cameraActionKeepsStateAfterCallActionsUpdate() = runTest {
        val actions = MutableStateFlow(listOf(CameraAction(), HangUpAction()))
        every { callMock.toCallActions(any()) } returns actions
        every { callMock.isMyCameraEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isToggled = true), HangUpAction())
        assertEquals(expected, actual)

        // Update call actions
        actions.value = listOf(CameraAction())
        advanceUntilIdle()

        val newActual = viewModel.uiState.first().actionList.value
        val newExpected = listOf(CameraAction(isToggled = true))
        assertEquals(newExpected, newActual)
    }

    @Test
    fun testCallActionsUiState_micActionKeepsStateAfterCallActionsUpdate() = runTest {
        val actions = MutableStateFlow(listOf(MicAction(), HangUpAction()))
        every { callMock.toCallActions(any()) } returns actions
        every { callMock.isMyMicEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = true), HangUpAction())
        assertEquals(expected, actual)

        // Update call actions
        actions.value = listOf(MicAction())
        advanceUntilIdle()

        val newActual = viewModel.uiState.first().actionList.value
        val newExpected = listOf(MicAction(isToggled = true))
        assertEquals(newExpected, newActual)
    }

    @Test
    fun testCallActionsUiState_audioActionKeepsStateAfterCallActionsUpdate() = runTest {
        val actions = MutableStateFlow(listOf(AudioAction(), HangUpAction()))
        every { callMock.toCallActions(any()) } returns actions
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.LoudSpeaker)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.LoudSpeaker), HangUpAction())
        assertEquals(expected, actual)

        // Update call actions
        actions.value = listOf(AudioAction())
        advanceUntilIdle()
        val newActual = viewModel.uiState.first().actionList.value
        val newExpected = listOf(AudioAction(audioDevice = AudioDeviceUi.LoudSpeaker))
        assertEquals(newExpected, newActual)
    }

    @Test
    fun testCallActionsUiState_virtualBackgroundKeepsStateAfterCallActionsUpdate() = runTest {
        val actions = MutableStateFlow(listOf(VirtualBackgroundAction(), HangUpAction()))
        every { callMock.toCallActions(any()) } returns actions
        every { callMock.isVirtualBackgroundEnabled() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual =  viewModel.uiState.first().actionList.value
        val expected = listOf(VirtualBackgroundAction(isToggled = true), HangUpAction())
        assertEquals(expected, actual)

        // Update call actions
        actions.value = listOf(VirtualBackgroundAction())
        advanceUntilIdle()
        val newActual =  viewModel.uiState.first().actionList.value
        val newExpected = listOf(VirtualBackgroundAction(isToggled = true))
        assertEquals(newExpected, newActual)
    }

    @Test
    fun cameraEnabled_cameraActionStateNotToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isToggled = true)))
        every { callMock.isMyCameraEnabled() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun cameraDisabled_cameraActionStateToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isToggled = false)))
        every { callMock.isMyCameraEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isToggled = true))
        assertEquals(expected, new)
    }

    @Test
    fun localParticipantInitialized_cameraActionStateEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isEnabled = false)))
        every { callMock.isMeParticipantInitialized() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isEnabled = true))
        assertEquals(expected, new)
    }

    @Test
    fun localParticipantNotInitialized_cameraActionStateDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isEnabled = true)))
        every { callMock.isMeParticipantInitialized() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isEnabled = false))
        assertEquals(expected, new)
    }

    @Test
    fun micEnabled_micActionStateNotToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isToggled = true)))
        every { callMock.isMyMicEnabled() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun micDisabled_micActionStateToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isToggled = false)))
        every { callMock.isMyMicEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = true))
        assertEquals(expected, new)
    }

    @Test
    fun isMyMicEnabledNotEmitting_preferredTypeAudioVideo_uiStateMicNotToggled() = runTest {
        every { callMock.isMyMicEnabled() } returns flowOf()
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioVideo())
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isToggled = false)))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun isMyMicEnabledNotEmitting_preferredTypeAudioUpgradable_uiStateMicNotToggled() = runTest {
        every { callMock.isMyMicEnabled() } returns flowOf()
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioUpgradable())
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isToggled = false)))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun isMyMicEnabledNotEmitting_preferredTypeAudioOnly_uiStateMicNotToggled() = runTest {
        every { callMock.isMyMicEnabled() } returns flowOf()
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioOnly())
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isToggled = false)))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun isMyCameraEnabledNotEmitting_preferredTypeAudioVideo_uiStateCameraNotToggled() = runTest {
        every { callMock.isMyCameraEnabled() } returns flowOf()
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioVideo())
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isToggled = false)))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun isMyCameraEnabledNotEmitting_preferredTypeAudioUpgradable_uiStateCameraNotToggled() = runTest {
        every { callMock.isMyCameraEnabled() } returns flowOf()
        every { callMock.preferredType } returns MutableStateFlow(Call.PreferredType.audioUpgradable())
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(CameraAction(isToggled = false)))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(CameraAction(isToggled = true))
        assertEquals(expected, new)
    }

    @Test
    fun localParticipantInitialized_micActionStateEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isEnabled = false)))
        every { callMock.isMeParticipantInitialized() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isEnabled = true))
        assertEquals(expected, new)
    }

    @Test
    fun localParticipantNotInitialized_micActionStateDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(MicAction(isEnabled = true)))
        every { callMock.isMeParticipantInitialized() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(MicAction(isEnabled = false))
        assertEquals(expected, new)
    }

    @Test
    fun audioDeviceLoudSpeaker_audioActionDeviceUpdateToLoudSpeaker() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(AudioAction()))
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.LoudSpeaker)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.LoudSpeaker))
        assertEquals(expected, actual)
    }

    @Test
    fun audioDeviceEarpiece_audioActionDeviceUpdateToEarPiece() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(AudioAction()))
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.EarPiece)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.EarPiece))
        assertEquals(expected, actual)
    }

    @Test
    fun audioDeviceWiredHeadset_audioActionDeviceUpdateToWiredHeadset() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(AudioAction()))
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.WiredHeadset)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.WiredHeadset))
        assertEquals(expected, actual)
    }

    @Test
    fun audioDeviceMuted_audioActionDeviceUpdateToMuted() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(AudioAction(audioDevice = AudioDeviceUi.WiredHeadset)))
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.Muted)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.Muted))
        assertEquals(expected, actual)
    }

    @Test
    fun audioDeviceBluetooth_audioActionDeviceUpdateToBluetooth() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(AudioAction()))
        every { callMock.toCurrentAudioDeviceUi() } returns flowOf(AudioDeviceUi.Bluetooth("id", null, null, null))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(AudioAction(audioDevice = AudioDeviceUi.Bluetooth("id", null, null, null)))
        assertEquals(expected, actual)
    }

    @Test
    fun virtualBackgroundEnabled_virtualBackgroundActionToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(VirtualBackgroundAction(isToggled = false)))
        every { callMock.isVirtualBackgroundEnabled() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(VirtualBackgroundAction(isToggled = true))
        assertEquals(expected, new)
    }

    @Test
    fun virtualBackgroundDisabled_virtualBackgroundActionNotToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(VirtualBackgroundAction(isToggled = true)))
        every { callMock.isVirtualBackgroundEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val new = viewModel.uiState.first().actionList.value
        val expected = listOf(VirtualBackgroundAction(isToggled = false))
        assertEquals(expected, new)
    }

    @Test
    fun callStateEnded_allActionsDisabled() = runTest {
        val callState = MutableStateFlow<Call.State>(Call.State.Connected)
        val actions = listOf(
            HangUpAction(),
            FlipCameraAction(),
            AudioAction(),
            ChatAction(),
            FileShareAction(),
            WhiteboardAction(),
            VirtualBackgroundAction(),
            MicAction(),
            CameraAction(),
            ScreenShareAction()
        )
        every { callMock.toCallActions(any()) } returns MutableStateFlow(actions)
        every { callMock.state } returns callState

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val current = viewModel.uiState.first().actionList.value
        assertEquals(actions, current)

        callState.value = Call.State.Disconnected.Ended
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(
            HangUpAction(isEnabled = false),
            FlipCameraAction(isEnabled = false),
            AudioAction(isEnabled = false),
            ChatAction(isEnabled = false),
            FileShareAction(isEnabled = false),
            WhiteboardAction(isEnabled = false),
            VirtualBackgroundAction(isEnabled = false),
            MicAction(isEnabled = false),
            CameraAction(isEnabled = false),
            ScreenShareAction(isEnabled = false)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun callStateDisconnecting_actionsSetToEmpty() = runTest {
        val callState = MutableStateFlow<Call.State>(Call.State.Connected)
        val actions = listOf(
            HangUpAction(),
            FlipCameraAction(),
            AudioAction(),
            ChatAction(),
            FileShareAction(),
            WhiteboardAction(),
            VirtualBackgroundAction(),
            MicAction(),
            CameraAction(),
            ScreenShareAction()
        )
        every { callMock.toCallActions(any()) } returns MutableStateFlow(actions)
        every { callMock.state } returns callState

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })

        advanceUntilIdle()
        val current = viewModel.uiState.first().actionList.value
        assertEquals(actions, current)

        callState.value = Call.State.Disconnecting
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(
            HangUpAction(isEnabled = false),
            FlipCameraAction(isEnabled = false),
            AudioAction(isEnabled = false),
            ChatAction(isEnabled = false),
            FileShareAction(isEnabled = false),
            WhiteboardAction(isEnabled = false),
            VirtualBackgroundAction(isEnabled = false),
            MicAction(isEnabled = false),
            CameraAction(isEnabled = false),
            ScreenShareAction(isEnabled = false)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun callIsNotConnected_fileShareActionDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(FileShareAction(isEnabled = true)))
        every { callMock.state } returns MutableStateFlow(mockk(relaxed = true))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(FileShareAction(isEnabled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun callIsNotConnected_screenShareActionDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(ScreenShareAction(isEnabled = true)))
        every { callMock.state } returns MutableStateFlow(mockk(relaxed = true))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(ScreenShareAction(isEnabled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun callIsNotConnected_whiteboardActionDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(WhiteboardAction(isEnabled = true)))
        every { callMock.state } returns MutableStateFlow(mockk(relaxed = true))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(WhiteboardAction(isEnabled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun callIsConnected_fileShareActionEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(FileShareAction(isEnabled = false)))
        every { callMock.state } returns MutableStateFlow(Call.State.Connected)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(FileShareAction(isEnabled = true))
        assertEquals(expected, actual)
    }

    @Test
    fun callIsConnected_screenShareActionEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(ScreenShareAction(isEnabled = false)))
        every { callMock.state } returns MutableStateFlow(Call.State.Connected)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(ScreenShareAction(isEnabled = true))
        assertEquals(expected, actual)
    }

    @Test
    fun callIsConnected_whiteboardActionEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(WhiteboardAction(isEnabled = false)))
        every { callMock.state } returns MutableStateFlow(Call.State.Connected)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(WhiteboardAction(isEnabled = true))
        assertEquals(expected, actual)
    }

    @Test
    fun screenSharingEnabled_screenShareActionToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(ScreenShareAction(isToggled = false)))
        every { callMock.isSharingScreen() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(ScreenShareAction(isToggled = true))
        assertEquals(expected, actual)
    }

    @Test
    fun screenSharingDisabled_screenShareActionNotToggled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(ScreenShareAction(isToggled = true)))
        every { callMock.isSharingScreen() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(ScreenShareAction(isToggled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun usbCameraConnected_switchCameraDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(FlipCameraAction(isEnabled = true)))
        every { callMock.hasUsbCamera() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(FlipCameraAction(isEnabled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun usbCameraNotConnectedAndCameraEnable_switchCameraEnabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(FlipCameraAction(isEnabled = false)))
        every { callMock.hasUsbCamera() } returns flowOf(false)
        every { callMock.isMyCameraEnabled() } returns flowOf(true)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(FlipCameraAction(isEnabled = true))
        assertEquals(expected, actual)
    }

    @Test
    fun usbCameraNotConnectedAndCameraDisable_switchCameraDisabled() = runTest {
        every { callMock.toCallActions(any()) } returns MutableStateFlow(listOf(FlipCameraAction(isEnabled = true)))
        every { callMock.hasUsbCamera() } returns flowOf(false)
        every { callMock.isMyCameraEnabled() } returns flowOf(false)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val actual = viewModel.uiState.first().actionList.value
        val expected = listOf(FlipCameraAction(isEnabled = false))
        assertEquals(expected, actual)
    }

    @Test
    fun testToggleMicOn() = runTest {
        val activity = mockk<FragmentActivity>()
        val inputs = mockk<Inputs>(relaxed = true)
        val audio = mockk<Input.Audio>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
            every { tryEnable() } returns true
        }
        every { callMock.inputs } returns inputs
        coEvery { inputs.request(any(), any()) } returns Inputs.RequestResult.Success(audio)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        var inputMessage: InputMessage? = null
        backgroundScope.launch { inputMessage = viewModel.inputMessage.first() }
        viewModel.toggleMic(activity)
        runCurrent()

        coVerify(exactly = 1) { inputs.request(activity, Inputs.Type.Microphone) }
        verify(exactly = 1) { audio.tryEnable() }
        assertEquals(MicMessage.Enabled, inputMessage)
    }

    @Test
    fun testToggleMicOff() = runTest {
        val activity = mockk<FragmentActivity>()
        val inputs = mockk<Inputs>(relaxed = true)
        val audio = mockk<Input.Audio>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
            every { tryDisable() } returns true
        }
        every { callMock.inputs } returns inputs
        coEvery { inputs.request(any(), any()) } returns Inputs.RequestResult.Success(audio)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        var inputMessage: InputMessage? = null
        backgroundScope.launch { inputMessage = viewModel.inputMessage.first() }
        viewModel.toggleMic(activity)
        runCurrent()

        coVerify(exactly = 1) { inputs.request(activity, Inputs.Type.Microphone) }
        verify(exactly = 1) { audio.tryDisable() }
        assertEquals(MicMessage.Disabled, inputMessage)
    }

    @Test
    fun testToggleCameraOn() = runTest {
        val activity = mockk<FragmentActivity>()
        val cameraRestriction = mockk<Contact.Restrictions.Restriction.Camera> {
            every { usage } returns false
        }
        val contactRestrictions = mockk<Contact.Restrictions> {
            every { camera } returns MutableStateFlow(cameraRestriction)
        }
        val cameraVideo = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
            every { tryEnable() } returns true
        }
        val cameraStream = mockk<Stream.Mutable> {
            every { id } returns CAMERA_STREAM_ID
            every { video } returns MutableStateFlow(cameraVideo)
        }
        val meParticipant = mockk<CallParticipant.Me> {
            every { restrictions } returns contactRestrictions
            every { streams } returns MutableStateFlow(listOf(cameraStream))
        }
        val participants = mockk<CallParticipants> {
            every { me } returns meParticipant
        }
        every { callMock.participants } returns MutableStateFlow(participants)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(setOf(cameraVideo))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        var inputMessage: InputMessage? = null
        backgroundScope.launch { inputMessage = viewModel.inputMessage.first() }
        viewModel.toggleCamera(activity)
        runCurrent()

        verify(exactly = 1) { cameraVideo.tryEnable() }
        assertEquals(CameraMessage.Enabled, inputMessage)
    }

    @Test
    fun testToggleCameraOnWhenCameraStreamInputIsNoMoreInAvailableInputs() = runTest {
        val activity = mockk<FragmentActivity>()
        val cameraRestriction = mockk<Contact.Restrictions.Restriction.Camera> {
            every { usage } returns false
        }
        val contactRestrictions = mockk<Contact.Restrictions> {
            every { camera } returns MutableStateFlow(cameraRestriction)
        }
        val cameraVideo = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
        }
        val usbVideo = mockk<Input.Video.Camera.Usb>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
        }
        val cameraStream = mockk<Stream.Mutable> {
            every { id } returns CAMERA_STREAM_ID
            every { video } returns MutableStateFlow(cameraVideo)
        }
        val meParticipant = mockk<CallParticipant.Me> {
            every { restrictions } returns contactRestrictions
            every { streams } returns MutableStateFlow(listOf(cameraStream))
        }
        val participants = mockk<CallParticipants> {
            every { me } returns meParticipant
        }
        val inputs = mockk<Inputs> {
            every { availableInputs } returns MutableStateFlow(setOf())
            coEvery { request(activity, Inputs.Type.Camera.External) } returns Inputs.RequestResult.Success(usbVideo)
            coEvery { request(activity, Inputs.Type.Camera.Internal) } returns Inputs.RequestResult.Success(cameraVideo)
        }
        every { callMock.participants } returns MutableStateFlow(participants)
        every { callMock.inputs } returns inputs

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        viewModel.toggleCamera(activity)
        advanceUntilIdle()

        coVerify(ordering = Ordering.ORDERED) {
            inputs.request(activity, Inputs.Type.Camera.External)
            inputs.request(activity, Inputs.Type.Camera.Internal)
        }
        verify(exactly = 1) { cameraVideo.tryEnable() }
        verify(exactly = 1) { usbVideo.tryEnable() }
    }

    @Test
    fun testToggleCameraOnWhenCameraStreamIsNull() = runTest {
        val activity = mockk<FragmentActivity>()
        val cameraRestriction = mockk<Contact.Restrictions.Restriction.Camera> {
            every { usage } returns false
        }
        val contactRestrictions = mockk<Contact.Restrictions> {
            every { camera } returns MutableStateFlow(cameraRestriction)
        }
        val cameraVideo = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
        }
        val usbVideo = mockk<Input.Video.Camera.Usb>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
        }
        val cameraStream = mockk<Stream.Mutable> {
            every { id } returns CAMERA_STREAM_ID
            every { video } returns MutableStateFlow(null)
        }
        val meParticipant = mockk<CallParticipant.Me> {
            every { restrictions } returns contactRestrictions
            every { streams } returns MutableStateFlow(listOf(cameraStream))
        }
        val participants = mockk<CallParticipants> {
            every { me } returns meParticipant
        }
        val inputs = mockk<Inputs> {
            every { availableInputs } returns MutableStateFlow(setOf(cameraVideo))
            coEvery { request(activity, Inputs.Type.Camera.External) } returns Inputs.RequestResult.Success(usbVideo)
            coEvery { request(activity, Inputs.Type.Camera.Internal) } returns Inputs.RequestResult.Success(cameraVideo)
        }
        every { callMock.participants } returns MutableStateFlow(participants)
        every { callMock.inputs } returns inputs

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        viewModel.toggleCamera(activity)
        advanceUntilIdle()

        coVerify(ordering = Ordering.ORDERED) {
            inputs.request(activity, Inputs.Type.Camera.External)
            inputs.request(activity, Inputs.Type.Camera.Internal)
        }
        verify(exactly = 1) { cameraVideo.tryEnable() }
        verify(exactly = 1) { usbVideo.tryEnable() }
    }

    @Test
    fun testToggleCameraOff() = runTest {
        val activity = mockk<FragmentActivity>()
        val cameraRestriction = mockk<Contact.Restrictions.Restriction.Camera> {
            every { usage } returns false
        }
        val contactRestrictions = mockk<Contact.Restrictions> {
            every { camera } returns MutableStateFlow(cameraRestriction)
        }
        val cameraVideo = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
            every { tryDisable() } returns true
        }
        val cameraStream = mockk<Stream.Mutable> {
            every { id } returns CAMERA_STREAM_ID
            every { video } returns MutableStateFlow(cameraVideo)
        }
        val meParticipant = mockk<CallParticipant.Me> {
            every { restrictions } returns contactRestrictions
            every { streams } returns MutableStateFlow(listOf(cameraStream))
        }
        val participants = mockk<CallParticipants> {
            every { me } returns meParticipant
        }
        every { callMock.participants } returns MutableStateFlow(participants)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(setOf(cameraVideo))

        var inputMessage: InputMessage? = null
        backgroundScope.launch { inputMessage = viewModel.inputMessage.first() }
        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()
        viewModel.toggleCamera(activity)
        runCurrent()

        verify(exactly = 1) { cameraVideo.tryDisable() }
        assertEquals(CameraMessage.Disabled, inputMessage)
    }

    @Test
    fun testToggleCameraWithCameraRestriction() = runTest {
        mockkObject(CallUserMessagesProvider)
        val activity = mockk<FragmentActivity>()
        val cameraRestriction = mockk<Contact.Restrictions.Restriction.Camera> {
            every { usage } returns true
        }
        val contactRestrictions = mockk<Contact.Restrictions> {
            every { camera } returns MutableStateFlow(cameraRestriction)
        }
        val cameraVideo = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
        }
        val cameraStream = mockk<Stream.Mutable> {
            every { id } returns CAMERA_STREAM_ID
            every { video } returns MutableStateFlow(cameraVideo)
        }
        val meParticipant = mockk<CallParticipant.Me> {
            every { restrictions } returns contactRestrictions
            every { streams } returns MutableStateFlow(listOf(cameraStream))
        }
        val participants = mockk<CallParticipants> {
            every { me } returns meParticipant
        }
        every { callMock.participants } returns MutableStateFlow(participants)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(setOf(cameraVideo))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        viewModel.toggleCamera(activity)
        runCurrent()
        verify(exactly = 1) {
            CallUserMessagesProvider.sendUserMessage(withArg {
                assertEquals(it::class, CameraRestrictionMessage::class)
            })
        }
    }

    @Test
    fun testHangUpWhenConnectionServiceIsEnabledAndCallIsNotRinging() = runTest {
        mockkObject(ConnectionServiceUtils, KaleyraCallConnectionService) {
            every { isConnectionServiceEnabled } returns true

            viewModel = spyk(CallActionsViewModel{
                mockkSuccessfulConfiguration(conference = conferenceMock)
            })
            advanceUntilIdle()

            viewModel.hangUp()
            runCurrent()
            coVerify(exactly = 1) { KaleyraCallConnectionService.hangUp() }
        }
    }

    @Test
    fun testSwitchCameraToFrontLens() = runTest {
        val rearLens = mockk<Input.Video.Camera.Internal.Lens>(relaxed = true) {
            every { isRear } returns true
        }
        val frontLens = mockk<Input.Video.Camera.Internal.Lens>(relaxed = true) {
            every { isRear } returns false
        }
        val video = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { lenses } returns listOf(rearLens, frontLens)
            every { currentLens } returns MutableStateFlow(rearLens)
        }
        every { callMock.inputs.availableInputs } returns MutableStateFlow(setOf(video, mockk<Input.Video.Camera.Usb>()))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        viewModel.switchCamera()
        verify(exactly = 1) { video.setLens(frontLens) }
    }

    @Test
    fun testSwitchCameraToRearLens() = runTest {
        val rearLens = mockk<Input.Video.Camera.Internal.Lens>(relaxed = true) {
            every { isRear } returns true
        }
        val frontLens = mockk<Input.Video.Camera.Internal.Lens>(relaxed = true) {
            every { isRear } returns false
        }
        val video = mockk<Input.Video.Camera.Internal>(relaxed = true) {
            every { lenses } returns listOf(rearLens, frontLens)
            every { currentLens } returns MutableStateFlow(frontLens)
        }
        every { callMock.inputs.availableInputs } returns MutableStateFlow(setOf(video, mockk<Input.Video.Camera.Usb>()))

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        viewModel.switchCamera()
        verify(exactly = 1) { video.setLens(rearLens) }
    }

    @Test
    fun oneToOne_showChatSuccessful() = runTest {
        val conversationMock = mockk<ConversationUI>(relaxed = true)
        val companyMock = mockk<Company>(relaxed = true) {
            every { id } returns MutableStateFlow("companyId")
        }
        val otherParticipantMock = mockk<CallParticipant>(relaxed = true) {
            every { userId } returns "userId1"
        }
        val callParticipantsMock = mockk<CallParticipants>(relaxed = true) {
            every { others } returns listOf(otherParticipantMock)
        }
        val contextMock = mockk<Context>()

        every { callMock.participants } returns MutableStateFlow(callParticipantsMock)
        every { conversationMock.chat(any(), any()) } returns Result.success(mockk())

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock, conversation = conversationMock, company = companyMock)
        })
        advanceUntilIdle()

        viewModel.showChat(contextMock)

        advanceUntilIdle()
        val expectedUserId = otherParticipantMock.userId
        verify(exactly = 1) {
            conversationMock.chat(
                context = contextMock,
                userId = withArg { assertEquals(it, expectedUserId) }
            )
        }
    }

    @Test
    fun oneToOneWithCompanyParticipant_showChatSuccessful() = runTest {
        val conversationMock = mockk<ConversationUI>(relaxed = true)
        val companyMock = mockk<Company>(relaxed = true) {
            every { id } returns MutableStateFlow("companyId")
        }
        val otherParticipantMock = mockk<CallParticipant>(relaxed = true) {
            every { userId } returns "userId1"
        }
        val companyParticipant = mockk<CallParticipant>(relaxed = true) {
            every { userId } returns "companyId"
        }
        val callParticipantsMock = mockk<CallParticipants>(relaxed = true) {
            every { others } returns listOf(otherParticipantMock, companyParticipant)
        }
        val contextMock = mockk<Context>()

        every { callMock.participants } returns MutableStateFlow(callParticipantsMock)
        every { conversationMock.chat(any(), any()) } returns Result.success(mockk())

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock, conversation = conversationMock, company = companyMock)
        })
        advanceUntilIdle()

        viewModel.showChat(contextMock)

        advanceUntilIdle()
        val expectedUserId = otherParticipantMock.userId
        verify(exactly = 1) {
            conversationMock.chat(
                context = contextMock,
                userId = withArg { assertEquals(it, expectedUserId) }
            )
        }
    }

    @Test
    fun groupCall_showChatFails() = runTest {
        val conversationMock = mockk<ConversationUI>(relaxed = true)
        val companyMock = mockk<Company>(relaxed = true) {
            every { id } returns MutableStateFlow("companyId")
        }
        val otherParticipantMock = mockk<CallParticipant>(relaxed = true) {
            every { userId } returns "userId1"
        }
        val otherParticipantMock2 = mockk<CallParticipant>(relaxed = true) {
            every { userId } returns "userId2"
        }
        val callParticipantsMock = mockk<CallParticipants>(relaxed = true) {
            every { others } returns listOf(otherParticipantMock, otherParticipantMock2)
        }
        val contextMock = mockk<Context>()

        every { callMock.participants } returns MutableStateFlow(callParticipantsMock)
        every { conversationMock.chat(any(), any()) } returns Result.success(mockk())

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock, conversation = conversationMock, company = companyMock)
        })
        advanceUntilIdle()

        viewModel.showChat(contextMock)

        advanceUntilIdle()
        verify(exactly = 0) {
            conversationMock.chat(any(), any())
        }
    }

    // TODO de-comment this when mtm will be available again
//    @Test
//    fun testShowGroupChat() = runTest {
//        val contextMock = mockk<Context>()
//        every { conversationMock.chat(any(), any(), any()) } returns Result.success(mockk())
//        every { callParticipantsMock.others } returns listOf(otherParticipantMock, otherParticipantMock2)
//        advanceUntilIdle()
//        viewModel.showChat(contextMock)
//        advanceUntilIdle()
//        val expectedCallServerId = callMock.serverId.replayCache.first()
//        val expectedUserIds = listOf(otherParticipantMock.userId, otherParticipantMock2.userId)
//        verify(exactly = 1) {
//            conversationMock.chat(
//                context = contextMock,
//                userIds = withArg { assertEquals(it, expectedUserIds) },
//                chatId = withArg { assertEquals(it, expectedCallServerId) }
//            )
//        }
//    }

    // TODO de-comment this when mtm will be available again
//    @Test
//    fun testShowGroupChatWithCompanyIdParticipant() = runTest {
//        val contextMock = mockk<Context>()
//        val companyParticipant = mockk<CallParticipant> {
//            every { userId } returns "companyId"
//        }
//        every { conversationMock.chat(any(), any(), any()) } returns Result.success(mockk())
//        every { callParticipantsMock.others } returns listOf(otherParticipantMock, otherParticipantMock2, companyParticipant)
//        advanceUntilIdle()
//        viewModel.showChat(contextMock)
//        advanceUntilIdle()
//        val expectedCallServerId = callMock.serverId.replayCache.first()
//        val expectedUserIds = listOf(otherParticipantMock.userId, otherParticipantMock2.userId)
//        verify(exactly = 1) {
//            conversationMock.chat(
//                context = contextMock,
//                userIds = withArg { assertEquals(it, expectedUserIds) },
//                chatId = expectedCallServerId
//            )
//        }
//    }

    @Test
    fun noScreenShareInputAvailable_stopScreenShareFail() = runTest {
        val cameraInput = mockk<Input.Video.Camera.Internal> {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
        }
        val usbInput = mockk<Input.Video.Camera.Usb> {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
        }
        val availableInputs = setOf(cameraInput, usbInput)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(availableInputs)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val isStopped = viewModel.tryStopScreenShare()
        assertEquals(false, isStopped)
    }

    @Test
    fun noScreenShareInputActive_stopScreenShareFail() = runTest {
        val screenShareInput = mockk<Input.Video.Application> {
            every { enabled } returns MutableStateFlow(Input.Enabled(false, false))
            every { tryDisable() } returns true
        }
        val usbInput = mockk<Input.Video.Camera.Usb> {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
        }
        val myStreamMock = mockk<Stream.Mutable>(relaxed = true) {
            every { id } returns SCREEN_SHARE_STREAM_ID
        }
        val meMock = mockk<CallParticipant.Me>(relaxed = true) {
            every { streams } returns MutableStateFlow(listOf(myStreamMock))
        }
        val participants = mockk<CallParticipants>(relaxed = true) {
            every { me } returns meMock
        }
        val availableInputs = setOf(screenShareInput, usbInput)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(availableInputs)
        every { callMock.participants } returns MutableStateFlow(participants)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val isStopped = viewModel.tryStopScreenShare()
        verify(exactly = 0) { screenShareInput.tryDisable() }
        assertEquals(false, isStopped)
    }

    @Test
    fun noScreenShareStream_stopScreenShareFail() = runTest {
        val screenShareInput = mockk<Input.Video.Application> {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
            every { tryDisable() } returns true
        }
        val usbInput = mockk<Input.Video.Camera.Usb> {
            every { enabled } returns MutableStateFlow(Input.Enabled(true, true))
        }
        val myStreamMock = mockk<Stream.Mutable>(relaxed = true) {
            every { id } returns "streamId"
        }
        val meMock = mockk<CallParticipant.Me>(relaxed = true) {
            every { streams } returns MutableStateFlow(listOf(myStreamMock))
        }
        val participants = mockk<CallParticipants>(relaxed = true) {
            every { me } returns meMock
        }
        val availableInputs = setOf(screenShareInput, usbInput)
        every { callMock.inputs.availableInputs } returns MutableStateFlow(availableInputs)
        every { callMock.participants } returns MutableStateFlow(participants)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val isStopped = viewModel.tryStopScreenShare()
        assertEquals(false, isStopped)
    }

    @Test
    fun deviceScreenShareActive_stopScreenShareSuccess() {
        val screenShareVideoMock = spyk<Input.Video.Screen>()
        testTryStopScreenShare(screenShareVideoMock)
        verify(exactly = 1) { screenShareVideoMock.dispose() }
    }

    @Test
    fun appScreenShareActive_stopScreenShareSuccess() {
        val screenShareVideoMock = spyk<Input.Video.Application>()
        testTryStopScreenShare(screenShareVideoMock)
        verify(exactly = 1) { screenShareVideoMock.tryDisable() }
    }

    private fun testTryStopScreenShare(screenShareVideoMock: Input.Video) = runTest {
        every { screenShareVideoMock.enabled } returns MutableStateFlow(Input.Enabled(true, true))
        every { screenShareVideoMock.tryDisable() } returns true
        val myStreamMock = mockk<Stream.Mutable>(relaxed = true) {
            every { id } returns SCREEN_SHARE_STREAM_ID
        }
        val meMock = mockk<CallParticipant.Me>(relaxed = true) {
            every { streams } returns MutableStateFlow(listOf(myStreamMock))
        }
        val participants = mockk<CallParticipants>(relaxed = true) {
            every { me } returns meMock
        }
        val availableInputs = setOf(screenShareVideoMock, mockk<Input.Video.Screen>(), mockk<Input.Video.Application>(), mockk<Input.Video.Camera>())
        every { callMock.inputs.availableInputs } returns MutableStateFlow(availableInputs)
        every { callMock.participants } returns MutableStateFlow(participants)

        viewModel = spyk(CallActionsViewModel{
            mockkSuccessfulConfiguration(conference = conferenceMock)
        })
        advanceUntilIdle()

        val isStopped = viewModel.tryStopScreenShare()
        verify(exactly = 1) { meMock.removeStream(myStreamMock) }
        assertEquals(true, isStopped)
    }
}