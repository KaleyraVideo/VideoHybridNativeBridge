// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import XCTest
import Hamcrest
import KaleyraVideoSDK
@testable import KaleyraVideoHybridNativeBridge

@available(iOS 15.0, *)
class ChatClientEventsReporterTests: UnitTestCase {

    private let statusChangedEvent = "chatModuleStatusChanged"

    private var client: ConversationStub!
    private var emitter: EventEmitterSpy!
    private var sut: ChatClientEventsReporter!

    override func setUpWithError() throws {
        try super.setUpWithError()

        client = .init()
        emitter = .init()
        sut = .init(conversation: client, emitter: emitter)
    }

    override func tearDownWithError() throws {
        sut = nil
        emitter = nil
        client = nil

        try super.tearDownWithError()
    }

    // MARK: - Tests

    func testOnClientConnectingShouldSendEvent() throws {
        sut.start()

        client.state = .connecting

        let event = try unwrap(emitter.sentEvents.first)
        assertThat(event.event, equalTo(statusChangedEvent))
        assertThat(event.args, presentAnd(instanceOfAnd(equalTo("connecting"))))
    }

    func testOnClientConnectedShouldSendEvent() throws {
        sut.start()

        client.state = .connected

        let event = try unwrap(emitter.sentEvents.first)
        assertThat(event.event, equalTo(statusChangedEvent))
        assertThat(event.args, presentAnd(instanceOfAnd(equalTo("ready"))))
    }

    func testOnClientDisconnectedShouldSendEvent() throws {
        sut.start()

        client.state = .connected
        client.state = .disconnected(error: nil)

        let event = try unwrap(emitter.sentEvents.last)
        assertThat(event.event, equalTo(statusChangedEvent))
        assertThat(event.args, presentAnd(instanceOfAnd(equalTo("stopped"))))
    }

    func testOnClientReconnectingShouldSendEvent() throws {
        sut.start()

        client.state = .connected
        client.state = .reconnecting

        let event = try unwrap(emitter.sentEvents.last)
        assertThat(event.event, equalTo(statusChangedEvent))
        assertThat(event.args, presentAnd(instanceOfAnd(equalTo("reconnecting"))))
    }

    func testOnClientFailedShouldSendTwoEvents() throws {
        sut.start()

        let error = anyNSError()
        client.simulateFailure(error: error)

        assertThat(emitter.sentEvents, hasCount(2))
        let firstEvent = emitter.sentEvents[0]
        assertThat(firstEvent.event, equalTo("chatError"))
        assertThat(firstEvent.args, presentAnd(instanceOfAnd(equalTo(error.localizedDescription))))
        let secondEvent = emitter.sentEvents[1]
        assertThat(secondEvent.event, equalTo(statusChangedEvent))
        assertThat(secondEvent.args, presentAnd(instanceOfAnd(equalTo("failed"))))
    }

    func testStopShouldStopListeningForClientEvents() {
        sut.start()

        sut.stop()
        client.state = .connected

        assertThat(emitter.sentEvents, empty())
    }

}
