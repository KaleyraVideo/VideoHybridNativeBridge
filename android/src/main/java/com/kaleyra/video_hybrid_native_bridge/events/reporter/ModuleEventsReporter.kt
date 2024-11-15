// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

package com.kaleyra.video_hybrid_native_bridge.events.reporter

import com.kaleyra.video_common_ui.KaleyraVideo
import com.kaleyra.video_hybrid_native_bridge.events.Events.CallModuleStatusChanged
import com.kaleyra.video_hybrid_native_bridge.events.Events.ChatModuleStatusChanged
import com.kaleyra.video_hybrid_native_bridge.events.EventsEmitter
import com.kaleyra.video_hybrid_native_bridge.events.EventsReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class ModuleEventsReporter(
    val sdk: KaleyraVideo,
    val eventsEmitter: EventsEmitter,
    val coroutineScope: CoroutineScope
) : EventsReporter {

    private var jobs: MutableSet<Job> = mutableSetOf()

    override fun start() {
        stop()
        jobs += sdk.conference.state
            .mapNotNull { it.toCrossPlatformModuleStatus() }
            .onEach { state -> eventsEmitter.sendEvent(CallModuleStatusChanged, state) }
            .launchIn(coroutineScope)
        jobs += sdk.conversation.state
            .mapNotNull { it.toCrossPlatformModuleStatus()  }
            .onEach { state -> eventsEmitter.sendEvent(ChatModuleStatusChanged, state) }
            .launchIn(coroutineScope)
    }

    override fun stop() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
}
