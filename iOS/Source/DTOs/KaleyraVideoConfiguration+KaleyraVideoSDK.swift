// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import UIKit
import KaleyraVideoSDK
import PushKit

extension KaleyraVideoConfiguration {

    private enum ConfigurationError: Error {
        case unsupportedEnvironment
        case unsupportedRegion
    }

    var isCallkitEnabled: Bool {
        iosConfig?.callkit?.enabled ?? true
    }

    var voipStrategy: VoipHandlingStrategy {
        iosConfig?.voipHandlingStrategy ?? .automatic
    }

    func makeKaleyraVideoSDKConfig() throws -> KaleyraVideoSDK.Config {
        guard let kaleyraEnv = environment.kaleyraEnvironment else {
            throw ConfigurationError.unsupportedEnvironment
        }

        guard let kaleyraRegion = region.kaleyraRegion else {
            throw ConfigurationError.unsupportedRegion
        }

        var config = KaleyraVideoSDK.Config(appID: appID, region: kaleyraRegion, environment: kaleyraEnv)
        config.callKit = iosConfig?.makeCallKitConfiguration() ?? .enabled(.default)
        config.voip = iosConfig?.makeVoIPConfiguration() ?? .manual
        return config
    }
}

private extension IosConfiguration {

    func makeCallKitConfiguration() -> KaleyraVideoSDK.Config.CallKitIntegration {

        guard let callkitConfig = callkit else {
            return .enabled(.default)
        }

        if callkitConfig.enabled ?? true {
            let image: UIImage? = if let iconName = callkitConfig.appIconName {
                UIImage(named: iconName)
            } else {
                nil
            }
            return .enabled(.init(icon: image, ringtoneSound: callkitConfig.ringtoneSoundName))
        } else {
            return .disabled
        }
    }

    func makeVoIPConfiguration() -> KaleyraVideoSDK.Config.VoIP {
        if voipHandlingStrategy == nil || voipHandlingStrategy == .automatic {
            return .automatic(listenForNotificationsInForeground: false)
        } else {
            return .manual
        }
    }
}
