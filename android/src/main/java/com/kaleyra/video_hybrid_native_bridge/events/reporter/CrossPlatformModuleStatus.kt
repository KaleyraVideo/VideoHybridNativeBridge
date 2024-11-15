// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

package com.kaleyra.video_hybrid_native_bridge.events.reporter

import com.kaleyra.video.State
import com.kaleyra.video_hybrid_native_bridge.events.reporter.CrossPlatformModuleStatus.Connecting
import com.kaleyra.video_hybrid_native_bridge.events.reporter.CrossPlatformModuleStatus.Failed
import com.kaleyra.video_hybrid_native_bridge.events.reporter.CrossPlatformModuleStatus.Ready
import com.kaleyra.video_hybrid_native_bridge.events.reporter.CrossPlatformModuleStatus.Stopped

enum class CrossPlatformModuleStatus {
    Ready,
    Connecting,
    Stopped,
    Failed
}

internal fun State.toCrossPlatformModuleStatus(): String? = when (this) {
    is State.Connecting         -> Connecting.name.lowercase()
    is State.Connected          -> Ready.name.lowercase()
    is State.Disconnected.Error -> Failed.name.lowercase()
    is State.Disconnected       -> Stopped.name.lowercase()
    State.Disconnecting -> null
}
