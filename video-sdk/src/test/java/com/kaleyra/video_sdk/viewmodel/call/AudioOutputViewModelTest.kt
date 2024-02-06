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

import com.bandyer.android_audiosession.model.AudioOutputDevice
import com.kaleyra.video.conference.CallParticipant
import com.kaleyra.video.conference.Input
import com.kaleyra.video.conference.Stream
import com.kaleyra.video_common_ui.CallUI
import com.kaleyra.video_common_ui.CollaborationViewModel.Configuration
import com.kaleyra.video_common_ui.ConferenceUI
import com.kaleyra.video_common_ui.connectionservice.CallAudioOutput
import com.kaleyra.video_common_ui.connectionservice.CallAudioOutputDelegate
import com.kaleyra.video_common_ui.connectionservice.CallAudioState
import com.kaleyra.video_extension_audio.extensions.CollaborationAudioExtensions
import com.kaleyra.video_extension_audio.extensions.CollaborationAudioExtensions.audioOutputDevicesList
import com.kaleyra.video_extension_audio.extensions.CollaborationAudioExtensions.currentAudioOutputDevice
import com.kaleyra.video_extension_audio.extensions.CollaborationAudioExtensions.setAudioOutputDevice
import com.kaleyra.video_sdk.MainDispatcherRule
import com.kaleyra.video_sdk.call.audiooutput.model.AudioDeviceUi
import com.kaleyra.video_sdk.call.audiooutput.model.AudioOutputUiState
import com.kaleyra.video_sdk.call.audiooutput.model.BluetoothDeviceState
import com.kaleyra.video_sdk.call.audiooutput.viewmodel.AudioOutputViewModel
import com.kaleyra.video_sdk.call.callactions.model.CallAction
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper.mapToAudioDeviceUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AudioOutputViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AudioOutputViewModel

    private val conferenceMock = mockk<ConferenceUI>()

    private val callMock = mockk<CallUI>(relaxed = true)

    private val audioMock1 = mockk<Input.Audio>(relaxed = true)

    private val audioMock2 = mockk<Input.Audio>(relaxed = true)

    private val streamMock1 = mockk<Stream.Mutable>()

    private val streamMock2 = mockk<Stream.Mutable>()

    private val participantMock1 = mockk<CallParticipant>()

    private val participantMock2 = mockk<CallParticipant>()

    @Before
    fun setUp() {
        viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, null)
        mockkObject(CollaborationAudioExtensions)
        every { conferenceMock.call } returns MutableStateFlow(callMock)
        every { callMock.audioOutputDevicesList } returns MutableStateFlow(listOf(AudioOutputDevice.Loudspeaker(), AudioOutputDevice.WiredHeadset(), AudioOutputDevice.Earpiece(), AudioOutputDevice.Bluetooth("bluetoothId1"), AudioOutputDevice.Bluetooth("bluetoothId2"), AudioOutputDevice.None()))
        every { callMock.participants } returns MutableStateFlow(mockk {
            every { others } returns listOf(participantMock1, participantMock2)
        })
        every { participantMock1.streams } returns MutableStateFlow(listOf(streamMock1))
        every { participantMock2.streams } returns MutableStateFlow(listOf(streamMock2))
        every { streamMock1.audio } returns MutableStateFlow(audioMock1)
        every { streamMock2.audio } returns MutableStateFlow(audioMock2)
        val currentAudioOutputDeviceFlow = MutableSharedFlow<AudioOutputDevice>(replay = 1, extraBufferCapacity = 1)
        every { callMock.currentAudioOutputDevice } returns currentAudioOutputDeviceFlow
        every { callMock.setAudioOutputDevice(any()) } answers { currentAudioOutputDeviceFlow.tryEmit(secondArg()) }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testAudioOutputUiState_playingDeviceIdIsInitialized() = runTest {
        every { callMock.currentAudioOutputDevice } returns MutableStateFlow(AudioOutputDevice.Loudspeaker())
        advanceUntilIdle()
        val new = viewModel.uiState.first().playingDeviceId
        val expected = AudioDeviceUi.LoudSpeaker.id
        Assert.assertEquals(expected, new)
    }

    @Test
    fun testAudioOutputUiState_deviceListUpdated() = runTest {
        every { callMock.audioOutputDevicesList } returns MutableStateFlow(listOf(AudioOutputDevice.Loudspeaker(), AudioOutputDevice.WiredHeadset(), AudioOutputDevice.None()))
        val current = viewModel.uiState.first().audioDeviceList.value
        Assert.assertEquals(listOf<CallAction>(), current)
        advanceUntilIdle()
        val new = viewModel.uiState.first().audioDeviceList.value
        val expected = listOf(AudioDeviceUi.LoudSpeaker, AudioDeviceUi.WiredHeadset, AudioDeviceUi.Muted)
        Assert.assertEquals(expected, new)
    }

    @Test
    fun testSetDevice_playingDeviceIdIsUpdated() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.LoudSpeaker)
        val actual = viewModel.uiState.first().playingDeviceId
        Assert.assertEquals(AudioDeviceUi.LoudSpeaker.id, actual)
    }

    @Test
    fun testSetLoudSpeakerDevice() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.LoudSpeaker)
        verify(exactly = 1) { callMock.setAudioOutputDevice(AudioOutputDevice.Loudspeaker()) }
    }

    @Test
    fun testSetEarpieceDevice() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.EarPiece)
        verify(exactly = 1) { callMock.setAudioOutputDevice(AudioOutputDevice.Earpiece()) }
    }

    @Test
    fun testSetWiredHeadsetDevice() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.WiredHeadset)
        verify(exactly = 1) { callMock.setAudioOutputDevice(AudioOutputDevice.WiredHeadset()) }
    }

    @Test
    fun testSetMutedDevice() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.Muted)
        verify(exactly = 1) { callMock.setAudioOutputDevice(AudioOutputDevice.None()) }
        verify(exactly = 1) { audioMock1.tryDisable() }
        verify(exactly = 1) { audioMock2.tryDisable() }
    }

    @Test
    fun testSetBluetoothDevice() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.Bluetooth(id = "bluetoothId2", connectionState = BluetoothDeviceState.Disconnected, name = null, batteryLevel = null))
        verify(exactly = 1) { callMock.setAudioOutputDevice(AudioOutputDevice.Bluetooth("bluetoothId2")) }
    }

    @Test
    fun `restore participants audio if previous device was none`() = runTest {
        advanceUntilIdle()
        viewModel.setDevice(AudioDeviceUi.Muted)
        viewModel.setDevice(AudioDeviceUi.LoudSpeaker)
        verify(exactly = 1) { audioMock1.tryEnable() }
        verify(exactly = 1) { audioMock2.tryEnable() }
    }

    @Test
    fun testCallAudioOutputDelegate_audioOutputStateUpdated() = runTest {
        val callOutputStateFlow = MutableStateFlow(CallAudioState())
        val callAudioOutputDelegate = object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = callOutputStateFlow
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        }
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        val current = viewModel.uiState.first()
        Assert.assertEquals(AudioOutputUiState(), current)
        callOutputStateFlow.value = CallAudioState(currentOutput = CallAudioOutput.Speaker)
        advanceUntilIdle()
        val new = viewModel.uiState.first()
        val expected = AudioOutputUiState(playingDeviceId = AudioDeviceUi.LoudSpeaker.id)
        Assert.assertEquals(expected, new)
    }

    @Test
    fun callAudioOutputDelegate_setLoudSpeakerDevice_deviceSet() {
        val callAudioOutputDelegate = spyk(object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = MutableStateFlow(CallAudioState())
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        })
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        viewModel.setDevice(AudioDeviceUi.LoudSpeaker)
        verify(exactly = 1) { callAudioOutputDelegate.setAudioOutput(CallAudioOutput.Speaker) }
    }

    @Test
    fun callAudioOutputDelegate_setEarpieceDevice_deviceSet() = runTest {
        val callAudioOutputDelegate = spyk(object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = MutableStateFlow(CallAudioState())
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        })
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        viewModel.setDevice(AudioDeviceUi.EarPiece)
        verify(exactly = 1) { callAudioOutputDelegate.setAudioOutput(CallAudioOutput.Earpiece) }
    }

    @Test
    fun callAudioOutputDelegate_setWiredHeadsetDevice_deviceSet() = runTest {
        val callAudioOutputDelegate = spyk(object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = MutableStateFlow(CallAudioState())
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        })
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        viewModel.setDevice(AudioDeviceUi.WiredHeadset)
        verify(exactly = 1) { callAudioOutputDelegate.setAudioOutput(CallAudioOutput.WiredHeadset) }
    }

    @Test
    fun callAudioOutputDelegate_setMutedDevice_deviceSet() = runTest {
        val callAudioOutputDelegate = spyk(object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = MutableStateFlow(CallAudioState())
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        })
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        viewModel.setDevice(AudioDeviceUi.Muted)
        verify(exactly = 1) { callAudioOutputDelegate.setAudioOutput(CallAudioOutput.Muted) }
    }

    @Test
    fun callAudioOutputDelegate_setBluetoothDevice_deviceSet() = runTest {
        val callAudioOutputDelegate = spyk(object : CallAudioOutputDelegate {
            override val callOutputState: StateFlow<CallAudioState> = MutableStateFlow(CallAudioState(CallAudioOutput.Bluetooth(id = "bluetoothId")))
            override fun setAudioOutput(output: CallAudioOutput) = Unit
        })
        val viewModel = AudioOutputViewModel({ Configuration.Success(conferenceMock, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))}, callAudioOutputDelegate)
        viewModel.setDevice(AudioDeviceUi.Bluetooth(id = "bluetoothId", connectionState = null, name = null, batteryLevel = null))
        verify(exactly = 1) { callAudioOutputDelegate.setAudioOutput(CallAudioOutput.Bluetooth(id = "bluetoothId", any())) }
    }

}