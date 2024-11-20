// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import Hamcrest
@testable import KaleyraVideoHybridNativeBridge

final class UserInterfacePresenter_MainQueueRelayTests: UnitTestCase {

    private var spy: UserInterfacePresenterSpy!
    private var sut: UserInterfacePresenter!

    // MARK: - Lifecycle

    override func setUpWithError() throws {
        try super.setUpWithError()

        spy = makeUserInterfacePresenterSpy()
        sut = MainQueueRelay(spy)
    }

    override func tearDownWithError() throws {
        sut = nil
        spy = nil

        try super.tearDownWithError()
    }

    // MARK: - Configure

    func testConfigurationShouldGetAndSetConfigurationOnDecoratee() {
        let config = makeUserInterfacePresenterConfiguration(showsFeedbackWhenCallEnds: false)

        sut.configuration = config
        assertThat(spy.configuration, equalTo(config))

        spy.configuration = .default
        assertThat(sut.configuration, equalTo(.default))
    }

    // MARK: - Setup

    func testSetupShouldForwardInvocationToDecorateeOnCurrentQueue() {
        let exp = expectation(description: "Performing work async on background queue")

        spy.onSetup = {
            exp.fulfill()
            assertThat(Thread.isMainThread, isFalse())
        }

        executeOnBgThread {
            self.sut.setup()
        }

        wait(for: [exp], timeout: 10.0)
    }

    // MARK: - Present Call

    func testPresentCallWithOptionsShouldForwardInvocationToDecoratee() {
        let options = makeCreateCallOptions()

        sut.presentCall(options)

        assertThat(spy.presentCallWithOptionsInvocations, equalTo([options]))
    }

    func testPresentCallWithOptionsShouldForwardInvocationToDecorateeOnMainQueue() {
        let exp = expectation(description: "Performing work async on background queue")

        spy.onPresentCallWithOptions = {
            exp.fulfill()

            assertThat(Thread.isMainThread, isTrue())
        }

        executeOnBgThread {
            self.sut.presentCall(self.makeCreateCallOptions())
        }

        wait(for: [exp], timeout: 10.0)
    }

    func testPresentCallWithURLShouldForwardInvocationToDecoratee() {
        let url = makeAnyURL()

        sut.presentCall(url)

        assertThat(spy.presentCallWithURLInvocations, equalTo([url]))
    }

    func testPresentCallWithURLShouldForwardInvocationToDecorateeOnMainQueue() {
        let exp = expectation(description: "Performing work async on background queue")

        spy.onPresentCallWithURL = {
            exp.fulfill()

            assertThat(Thread.isMainThread, isTrue())
        }

        executeOnBgThread {
            self.sut.presentCall(self.makeAnyURL())
        }

        wait(for: [exp], timeout: 10.0)
    }

    // MARK: - Present Chat

    func testPresentChatShouldForwardInvocationToDecoratee() {
        sut.presentChat(with: "user_id")

        assertThat(spy.presentChatInvocations, equalTo(["user_id"]))
    }

    func testPresentChatShouldForwardInvocationToDecorateeOnMainQueue() {
        let exp = expectation(description: "Performing work async on background queue")

        spy.onPresentChat = {
            exp.fulfill()

            assertThat(Thread.isMainThread, isTrue())
        }

        executeOnBgThread {
            self.sut.presentChat(with: "user_id")
        }

        wait(for: [exp], timeout: 10.0)
    }

    // MARK: - Helpers

    private func makeUserInterfacePresenterSpy() -> UserInterfacePresenterSpy {
        .init()
    }

    private func makeUserInterfacePresenterConfiguration(showsFeedbackWhenCallEnds: Bool = false,
                                                         chatAudioButtonConf: UserInterfacePresenterConfiguration.ChatAudioButtonConfiguration = .disabled,
                                                         chatVideoButtonConf: UserInterfacePresenterConfiguration.ChatVideoButtonConfiguration = .disabled) -> UserInterfacePresenterConfiguration {
        .init(showsFeedbackWhenCallEnds: showsFeedbackWhenCallEnds,
              chatAudioButtonConf: chatAudioButtonConf,
              chatVideoButtonConf: chatVideoButtonConf)
    }

    private func makeCreateCallOptions() -> CreateCallOptions {
        .init(callees: ["user_id"], callType: .audio, recordingType: .manual)
    }

    private func makeAnyURL() -> URL {
        .init(string: "https://www.kaleyra.com")!
    }

    private func executeOnBgThread(_ work: @escaping () -> Void) {
        DispatchQueue.global(qos: .background).async(execute: work)
    }
}
