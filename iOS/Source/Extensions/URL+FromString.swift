// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation

@available(iOS 12.0, *)
extension URL {

    static func fromString(_ string: String) throws -> URL {
        guard let url = URL(string: string) else {
            throw VideoHybridNativeBridgeError.unableToInitializeURLError(urlString: string)
        }
        return url
    }
}
