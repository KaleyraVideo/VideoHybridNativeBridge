// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

import Foundation
import Hamcrest
import PushKit
@testable import KaleyraVideoHybridNativeBridge

final class KaleyraVideoConfiguration_KaleyraVideoSDKTests: UnitTestCase {

    func testBandyerPluginConfigurationDefaultIsCallkitEnabled() {
        let sut = makeKaleyraVideoPluginConfiguration()

        assertThat(sut.isCallkitEnabled, isTrue())
    }

    func testBandyerPluginConfigurationIsCallkitEnabled() {
        let disabled = makeKaleyraVideoPluginConfiguration(callkit: CallKitConfiguration(appIconName: nil, enabled: false, ringtoneSoundName: nil))
        let enabled = makeKaleyraVideoPluginConfiguration(callkit: CallKitConfiguration(appIconName: nil, enabled: true, ringtoneSoundName: nil))

        assertThat(disabled.isCallkitEnabled, isFalse())
        assertThat(enabled.isCallkitEnabled, isTrue())
    }

    func testBandyerPluginConfigurationDefaultVoipStrategy() {
        let sut = makeKaleyraVideoPluginConfiguration()

        assertThat(sut.voipStrategy, equalTo(.automatic))
    }

    func testBandyerPluginConfigurationVoipStrategy() {
        let automatic = makeKaleyraVideoPluginConfiguration(voipHandlingStrategy: .automatic)
        let disabled = makeKaleyraVideoPluginConfiguration(voipHandlingStrategy: .disabled)

        assertThat(automatic.voipStrategy, equalTo(.automatic))
        assertThat(disabled.voipStrategy, equalTo(.disabled))
    }

    func testIosConfigurationMakeVoipConfigurationShouldBeAutomaticByDefault() throws {
        let conf = makeKaleyraVideoPluginConfiguration()

        let bandyerConf = try conf.makeKaleyraVideoSDKConfig()

        assertThat(bandyerConf.voip.isAutomatic, isTrue())
    }

    func testIosConfigurationMakeVoipConfigurationWithDisabledStrategy() throws {
        let conf = makeKaleyraVideoPluginConfiguration(voipHandlingStrategy: .disabled)

        let bandyerConf = try conf.makeKaleyraVideoSDKConfig()

        assertThat(bandyerConf.voip.isAutomatic, isFalse())
    }

    func testIosConfigurationMakeVoipConfigurationWithAutomaticStrategyAndRegistryDelegate() throws {
        let conf = makeKaleyraVideoPluginConfiguration(voipHandlingStrategy: .automatic)

        let bandyerConf = try conf.makeKaleyraVideoSDKConfig()

        assertThat(bandyerConf.voip.isAutomatic, isTrue())
    }

    // MARK: - Helpers

    private func makeKaleyraVideoPluginConfiguration(callkit: CallKitConfiguration? = nil,
                                                     voipHandlingStrategy: VoipHandlingStrategy? = nil) -> KaleyraVideoConfiguration {
        .init(appID: "",
              environment: Environment(name: "sandbox"),
              iosConfig: IosConfiguration(callkit: callkit,
                                          voipHandlingStrategy: voipHandlingStrategy),
              logEnabled: false,
              region: Region(name: "europe"))
    }
}
