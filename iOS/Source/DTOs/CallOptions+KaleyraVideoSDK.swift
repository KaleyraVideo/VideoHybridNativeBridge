// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

@available(iOS 15.0, *)
extension CallOptions {

    var callOptions: KaleyraVideoSDK.CallOptions {
        .init(type: .audioVideo, recording: recordingType?.callRecordingType)
    }
}
