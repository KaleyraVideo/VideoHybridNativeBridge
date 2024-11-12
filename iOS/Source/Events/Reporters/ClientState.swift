// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

@available(iOS 15.0, *)
enum ClientState: String {
    case stopped = "stopped"
    case connecting = "connecting"
    case ready = "ready"
    case paused = "paused"
    case reconnecting = "reconnecting"
    case failed = "failed"
}

@available(iOS 15.0, *)
extension ClientState {

    init?(state: KaleyraVideoSDK.ClientState) {
        switch state {
            case .disconnected(error: let error):
                if error != nil {
                    self = .failed
                } else {
                    self = .stopped
                }
            case .connecting:
                self = .connecting
            case .connected:
                self = .ready
            case .reconnecting:
                self = .reconnecting
        }
    }
}
