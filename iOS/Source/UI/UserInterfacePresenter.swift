// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation

protocol UserInterfacePresenter: AnyObject {

    var configuration: UserInterfacePresenterConfiguration { get set }

    func setup()

    func presentCall(_ options: CreateCallOptions)
    func presentCall(_ url: URL)
    func presentChat(with userID: String)
}
