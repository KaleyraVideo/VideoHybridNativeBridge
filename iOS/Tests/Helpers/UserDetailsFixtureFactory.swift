// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import CallKit
import KaleyraVideoSDK

@available(iOS 15.0, *)
protocol UserDetailsFixtureFactory {

    func makeItem(userID: String, name: String?, image: URL?, handle: CXHandle?) -> KaleyraVideoSDK.UserDetails
}

@available(iOS 15.0, *)
extension UserDetailsFixtureFactory {

    func makeItem(userID: String, name: String? = nil, image: URL? = nil, handle: CXHandle? = nil) -> KaleyraVideoSDK.UserDetails {
        .init(userId: userID, name: name, image: image, handle: handle)
    }
}
