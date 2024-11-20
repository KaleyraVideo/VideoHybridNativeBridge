// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation

extension MainQueueRelay: UserInterfacePresenter where Decoratee: UserInterfacePresenter {

    var configuration: UserInterfacePresenterConfiguration {
        get {
            decoratee.configuration
        }
        set {
            decoratee.configuration = newValue
        }
    }
    
    func setup() {
        decoratee.setup()
    }

    func presentCall(_ options: CreateCallOptions) {
        dispatch { [weak self] in
            self?.decoratee.presentCall(options)
        }
    }

    func presentCall(_ url: URL) {
        dispatch { [weak self] in
            self?.decoratee.presentCall(url)
        }
    }

    func presentChat(with userID: String) {
        dispatch { [weak self] in
            self?.decoratee.presentChat(with: userID)
        }
    }
}
