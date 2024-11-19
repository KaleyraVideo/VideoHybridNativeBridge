// Copyright © 2018-2024 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import Hamcrest
@testable import KaleyraVideoHybridNativeBridge

final class ToolsConfiguration_KaleyraVideoSDKTests: UnitTestCase {

    func testChatToolConfiguration() {
        let noChatConf = makeSUT(isChatEnabled: false)
        let chatConf = makeSUT(isChatEnabled: true)

        let kaleyraSDKNoChatConf = noChatConf.makeToolsConfig()
        let kaleyraSDKChatConf = chatConf.makeToolsConfig()

        assertThat(kaleyraSDKNoChatConf.chat.isEnabled, isFalse())
        assertThat(kaleyraSDKChatConf.chat.isEnabled, isTrue())
    }

    func testFileShareToolConfiguration() {
        let fileshareDisabledConf = makeSUT(isFileShareEnabled: false)
        let fileshareNilConf = makeSUT(isFileShareEnabled: nil)
        let fileshareEnabledConf = makeSUT(isFileShareEnabled: true)

        let kaleyraSDKFileshareDisabledConf = fileshareDisabledConf.makeToolsConfig()
        let kaleyraSDKFileshareNilConf = fileshareNilConf.makeToolsConfig()
        let kaleyraSDKFileshareEnalbedConf = fileshareEnabledConf.makeToolsConfig()

        assertThat(kaleyraSDKFileshareDisabledConf.fileshare.isEnabled, isFalse())
        assertThat(kaleyraSDKFileshareNilConf.fileshare.isEnabled, isFalse())
        assertThat(kaleyraSDKFileshareEnalbedConf.fileshare.isEnabled, isTrue())
    }

    func testWhiteboardToolConfiguration() {
        let whiteboardDisabledConf = makeSUT(isWhiteboardEnabled: false)
        let whiteboardNilConf = makeSUT(isWhiteboardEnabled: nil)
        let whiteboardEnabledConf = makeSUT(isWhiteboardEnabled: true)

        let kaleyraSDKWhiteboardDisabledConf = whiteboardDisabledConf.makeToolsConfig()
        let kaleyraSDKWhiteboardNilConf = whiteboardNilConf.makeToolsConfig()
        let kaleyraSDKWiteboardEnabledConf = whiteboardEnabledConf.makeToolsConfig()

        assertThat(kaleyraSDKWhiteboardDisabledConf.whiteboard.isEnabled, isFalse())
        assertThat(kaleyraSDKWhiteboardNilConf.whiteboard.isEnabled, isFalse())
        assertThat(kaleyraSDKWiteboardEnabledConf.whiteboard.isEnabled, isTrue())
        assertThat(kaleyraSDKWiteboardEnabledConf.whiteboard.isEnabled, isTrue())
    }

    func testScreenShareConfiguration() {
        let disabledScreenShareConf = makeSUT(screenShareConf: .disabled)
        let disabledInAppAndDeviceScreenShareConf = makeSUT(screenShareConf: .enabled(inApp: false, wholeDevice: false))
        let nilInAppAndDeviceScreenShareConf = makeSUT(screenShareConf: .enabled(inApp: nil, wholeDevice: nil))
        let enabledInAppAndDisabledDeviceScreenShareConf = makeSUT(screenShareConf: .enabled(inApp: true, wholeDevice: false))
        let disabledInAppAndEnabledDeviceScreenShareConf = makeSUT(screenShareConf: .enabled(inApp: false, wholeDevice: true))
        let enabledAllScreenShareConf = makeSUT(screenShareConf: .enabled(inApp: true, wholeDevice: true))

        let kaleyraSDKDisabledScreenShareConf = disabledScreenShareConf.makeToolsConfig()
        let kaleyraSDKDisabledInAppAndDeviceScreenShareConf = disabledInAppAndDeviceScreenShareConf.makeToolsConfig()
        let kaleyraSDKNilInAppAndDeviceScreenShareConf = nilInAppAndDeviceScreenShareConf.makeToolsConfig()
        let kaleyraSDKEnabledInAppAndDisabledDeviceScreenShareConf = enabledInAppAndDisabledDeviceScreenShareConf.makeToolsConfig()
        let kaleyraSDKDisabledInAppAndEnabledDeviceScreenShareConf = disabledInAppAndEnabledDeviceScreenShareConf.makeToolsConfig()
        let kaleyraSDKEnabledAllScreenShareConf = enabledAllScreenShareConf.makeToolsConfig()

        assertThat(kaleyraSDKDisabledScreenShareConf.inAppScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKDisabledScreenShareConf.broadcastScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKDisabledInAppAndDeviceScreenShareConf.inAppScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKDisabledInAppAndDeviceScreenShareConf.broadcastScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKNilInAppAndDeviceScreenShareConf.inAppScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKNilInAppAndDeviceScreenShareConf.broadcastScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKEnabledInAppAndDisabledDeviceScreenShareConf.inAppScreenSharing.isEnabled, isTrue())
        assertThat(kaleyraSDKEnabledInAppAndDisabledDeviceScreenShareConf.broadcastScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKDisabledInAppAndEnabledDeviceScreenShareConf.inAppScreenSharing.isEnabled, isFalse())
        assertThat(kaleyraSDKEnabledAllScreenShareConf.inAppScreenSharing.isEnabled, isTrue())
    }

    // MARK: - Helpers

    private func makeSUT(isChatEnabled: Bool = false,
                         isFileShareEnabled: Bool? = nil,
                         screenShareConf: ScreenShare = .disabled,
                         isWhiteboardEnabled: Bool? = nil) -> ToolsConfiguration {
        .init(chat: isChatEnabled ? .init(audioCallOption: .init(recordingType: nil, type: .audio),
                                          videoCallOption: .init(recordingType: nil)) : nil,
              feedback: nil,
              fileShare: isFileShareEnabled,
              screenShare: screenShareConf.toScreenShareConfiguration(),
              whiteboard: isWhiteboardEnabled)
    }

    // MARK: - Doubles

    fileprivate enum ScreenShare {

        case disabled
        case enabled(inApp: Bool?, wholeDevice: Bool?)
    }
}

private extension ToolsConfiguration_KaleyraVideoSDKTests.ScreenShare {

    func toScreenShareConfiguration() -> ScreenShareToolConfiguration? {
        switch self {
            case .disabled:
                return nil
            case .enabled(let inApp, let wholeDevice):
                return .init(inApp: inApp, wholeDevice: wholeDevice)
        }
    }
}
