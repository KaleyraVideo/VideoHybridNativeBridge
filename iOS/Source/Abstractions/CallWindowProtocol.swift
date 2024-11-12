// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import KaleyraVideoSDK

@available(iOS 15.0, *)
protocol CallWindowProtocol: AnyObject {

    var isHidden: Bool { get set }

    func makeKeyAndVisible()
    func set(rootViewController controller: UIViewController?, animated: Bool, completion: ((Bool) -> Void)?)
}

@available(iOS 15.0, *)
extension CallWindow: CallWindowProtocol {}
