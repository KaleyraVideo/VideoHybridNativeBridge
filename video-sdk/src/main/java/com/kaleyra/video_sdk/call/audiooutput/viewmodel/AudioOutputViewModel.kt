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

package com.kaleyra.video_sdk.call.audiooutput.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaleyra.video_common_ui.connectionservice.ConnectionServiceUtils
import com.kaleyra.video_extension_audio.extensions.CollaborationAudioExtensions.setAudioOutputDevice
import com.kaleyra.video_sdk.call.audiooutput.model.AudioDeviceUi
import com.kaleyra.video_sdk.call.audiooutput.model.AudioOutputUiState
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper.mapToAudioOutputDevice
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper.toAvailableAudioDevicesUi
import com.kaleyra.video_sdk.call.mapper.AudioOutputMapper.toCurrentAudioDeviceUi
import com.kaleyra.video_sdk.call.viewmodel.BaseViewModel
import com.kaleyra.video_sdk.common.immutablecollections.ImmutableList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class AudioOutputViewModel(configure: suspend () -> Configuration) : BaseViewModel<AudioOutputUiState>(configure) {

    override fun initialState() = AudioOutputUiState()

    val isConnectionServiceEnabled: Boolean
        get() = ConnectionServiceUtils.isConnectionServiceEnabled

    init {
        call
            .toAvailableAudioDevicesUi()
            .onEach { audioDevices -> _uiState.update { it.copy(audioDeviceList = ImmutableList(audioDevices)) } }
            .launchIn(viewModelScope)

        call
            .toCurrentAudioDeviceUi()
            .filterNotNull()
            .onEach { currentOutputDevice -> _uiState.update { it.copy(playingDeviceId = currentOutputDevice.id) } }
            .launchIn(viewModelScope)
    }

    fun setDevice(device: AudioDeviceUi) {
        val call = call.getValue()
        val outputDevice = call?.let { device.mapToAudioOutputDevice(it) } ?: return
        call.setAudioOutputDevice(outputDevice, isConnectionServiceEnabled = ConnectionServiceUtils.isConnectionServiceEnabled)
        _uiState.update { it.copy(playingDeviceId = device.id) }
    }

    companion object {
        fun provideFactory(configure: suspend () -> Configuration) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AudioOutputViewModel(configure) as T
            }
        }
    }

}