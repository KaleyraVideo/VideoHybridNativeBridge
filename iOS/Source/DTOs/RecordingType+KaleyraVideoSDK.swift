// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

@available(iOS 15.0, *)
extension RecordingType {

    var callRecordingType: KaleyraVideoSDK.CallOptions.RecordingType? {
        switch self {
            case .automatic:
                return .automatic
            case .manual:
                return .manual
            case .none:
                return nil
        }
    }
}
