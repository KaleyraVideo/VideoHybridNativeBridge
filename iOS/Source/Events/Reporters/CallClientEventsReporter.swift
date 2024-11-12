// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import Combine
import KaleyraVideoSDK

@available(iOS 15.0, *)
class CallClientEventsReporter: NSObject {

    private(set) var lastVoIPToken: String?
    private let conference: Conference
    private let emitter: EventEmitter
    private var cancellables = Set<AnyCancellable>()

    var isRunning: Bool {
        cancellables.isEmpty
    }

    init(conference: Conference, emitter: EventEmitter) {
        self.conference = conference
        self.emitter = emitter
        super.init()
    }

    func start() {
        guard !isRunning else { return }

        conference.statePublisher.dropFirst().receive(on: DispatchQueue.main).sink { [weak self] _ in
            self?.notifyNewClientState()
        }.store(in: &cancellables)

        conference.voipCredentialsPublisher.receive(on: DispatchQueue.main).sink { [weak self] credentials in
            self?.notifyNewCredentials(credentials)
        }.store(in: &cancellables)
    }

    func stop() {
        guard isRunning else { return }

        cancellables.removeAll()
    }

    // MARK: - Call client observer

    private func notifyNewClientState() {
        notifyErrorIfNeeded()

        guard let clientState = ClientState(state: conference.state) else { return }
        emitter.sendEvent(Events.chatModuleStatusChanged, args: clientState.rawValue)
    }

    private func notifyErrorIfNeeded() {
        guard case KaleyraVideoSDK.ClientState.disconnected(error: let error) = conference.state, let error else { return }
        emitter.sendEvent(Events.chatError, args: error.localizedDescription)
    }

    // MARK: - VoIP Credentials

    private func notifyNewCredentials(_ credentials: VoIPCredentials?) {
        guard let credentials else {
            emitter.sendEvent(.iOSVoipPushTokenInvalidated, args: nil)
            lastVoIPToken = nil
            return
        }

        emitter.sendEvent(.iOSVoipPushTokenUpdated, args: credentials.tokenAsString)
        lastVoIPToken = credentials.tokenAsString
    }
}
