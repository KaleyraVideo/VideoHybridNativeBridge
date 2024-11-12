// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

@available(iOS 15.0, *)
extension UserDetails {

    var bandyerDetails: KaleyraVideoSDK.UserDetails {
        .init(userId: userID, 
              name: nickName,
              image: URL(string: profileImageURL ?? ""),
              handle: .init(type: .generic, value: nickName ?? userID))
    }
}
