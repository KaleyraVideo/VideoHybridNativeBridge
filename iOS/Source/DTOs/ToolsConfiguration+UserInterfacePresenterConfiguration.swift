// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation

extension ToolsConfiguration {

    func uiPresenterConfiguration() -> UserInterfacePresenterConfiguration {
        .init(showsFeedbackWhenCallEnds: feedback ?? false,
              chatAudioButtonConf: chatAudioButtonConf,
              chatVideoButtonConf: chatVideoButtonConf)
    }

    private var chatAudioButtonConf: UserInterfacePresenterConfiguration.ChatAudioButtonConfiguration {
        guard let audioOptions = chat?.audioCallOption else {
            return .disabled
        }
        return .enabled(audioOptions)
    }

    private var chatVideoButtonConf: UserInterfacePresenterConfiguration.ChatVideoButtonConfiguration {
        guard let videoOptions = chat?.videoCallOption else {
            return .disabled
        }
        return .enabled(videoOptions)
    }
}

