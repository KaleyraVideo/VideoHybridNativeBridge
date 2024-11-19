// Copyright © 2018-2024 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

extension ToolsConfiguration {

    func makeToolsConfig() -> KaleyraVideoSDK.ConferenceSettings.Tools {
        var config = KaleyraVideoSDK.ConferenceSettings.Tools.default
        config.chat = makeChatConfiguration()
        config.broadcastScreenSharing = makeBroadcastScreenSharingConfiguration()
        config.inAppScreenSharing = makeInAppScreenSharingConfiguration()
        config.fileshare = makeFileShareConfiguration()
        config.whiteboard = makeWhiteboardConfiguration()
        return config
    }

    private func makeChatConfiguration() -> KaleyraVideoSDK.ConferenceSettings.Tools.Chat {
        guard chat != nil else { return .disabled }
        return .enabled
    }

    private func makeBroadcastScreenSharingConfiguration() -> KaleyraVideoSDK.ConferenceSettings.Tools.BroadcastScreenSharing {
        guard let screenShare,
                screenShare.wholeDevice ?? false,
                let configURL = Bundle.main.url(forResource: "KaleyraVideoConfig", withExtension: "plist") else { return .disabled }

        let reader = BroadcastConfigurationPlistReader()
        guard let broadcastConfig = try? reader.read(fileURL: configURL),
                let appGroupIdentifier = try? AppGroupIdentifier(broadcastConfig.appGroupID) else { return .disabled }

        return .enabled(appGroupIdentifier: appGroupIdentifier, extensionBundleIdentifier: broadcastConfig.extensionBundleID)

    }

    private func makeInAppScreenSharingConfiguration() -> KaleyraVideoSDK.ConferenceSettings.Tools.InAppScreenSharing {
        guard let screenShare, screenShare.inApp ?? false else { return .disabled }
        return .enabled
    }

    private func makeFileShareConfiguration() -> KaleyraVideoSDK.ConferenceSettings.Tools.Fileshare {
        guard fileShare ?? false else { return .disabled }
        return .enabled
    }

    private func makeWhiteboardConfiguration() -> KaleyraVideoSDK.ConferenceSettings.Tools.Whiteboard {
        guard whiteboard ?? false else { return .disabled }
        return .enabled
    }
}
