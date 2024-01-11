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

package com.kaleyra.video_common_ui.call

import android.content.Context
import com.kaleyra.video.conference.Call
import com.kaleyra.video.conference.CallParticipant
import com.kaleyra.video.conference.CallParticipants
import com.kaleyra.video.conference.Input
import com.kaleyra.video.conference.Stream
import com.kaleyra.video.conference.VideoStreamView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Streams Video View Delegate
 */
 class StreamsVideoViewDelegate(private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)){

    /**
     * Sets the streams video view
     *
     * @param context Context context used for the view creation
     * @param call Call The call on which set the video view
     */
    fun setStreamsVideoView(
        context: Context,
        call: Call
    ) {
        call.participants
            .map { it.list }
            .mapParticipantsToVideos()
            .transform { videos -> videos.forEach { emit(it) } }
            .filterIsInstance<Input.Video>()
            .onEach { video ->
                withContext(Dispatchers.Main) {
                    if (video.view.value != null) return@withContext
                    video.view.value = VideoStreamView(context.applicationContext)
                }
            }
            .launchIn(coroutineScope)
    }

    private fun Flow<List<CallParticipant>>.mapParticipantsToVideos(): Flow<List<Input.Video?>> {
        return this.flatMapLatest { participants ->
            val participantVideos = mutableMapOf<String, List<Input.Video?>>()
            participants.map { participant ->
                participant.streams
                    .mapStreamsToVideos()
                    .map { Pair(participant.userId, it) }
            }
                .merge()
                .transform { (userId, videos) ->
                    participantVideos[userId] = videos
                    val values = participantVideos.values.toList()
                    if (values.size == participants.size) {
                        emit(values.flatten())
                    }
                }
        }
    }

    private fun Flow<List<Stream>>.mapStreamsToVideos(): Flow<List<Input.Video?>> {
        return this.flatMapLatest { streams ->
            val streamVideos = mutableMapOf<String, Input.Video?>()
            if (streams.isEmpty()) flowOf(listOf())
            else streams
                .map { stream ->
                    stream.video
                        .map { Pair(stream.id, it) }
                }
                .merge()
                .transform { (streamId, video) ->
                    streamVideos[streamId] = video
                    val values = streamVideos.values.toList()
                    if (values.size == streams.size) {
                        emit(values)
                    }
                }
        }
    }
}
