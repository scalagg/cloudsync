package gg.scala.cloudsync.velocity

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.velocity.settings.VelocitySettingsProcessor

/**
 * @author GrowlyX
 * @since 4/4/2022
 */
typealias Authentication =
        CloudSyncAuthenticationService.ScalaConnectionAuthenticationWrapper

typealias Discoverable =
        CloudSyncDiscoveryService.DiscoverableAssets

object ScalaCloudSyncVelocityPlatform : ScalaCloudSyncPlatform
{
    override fun getLogger() =
        ScalaCloudSyncVelocity.INSTANCE.logger

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        VelocitySettingsProcessor.initialLoad()

        VelocitySettingsProcessor
            .loadSettings<Authentication>("authentication")
        VelocitySettingsProcessor
            .loadSettings<Discoverable>("discoverable")

        // velocity file container
        shared.fileContainer = ScalaCloudSyncVelocity.INSTANCE
            .directoryFile.parentFile.parentFile

        // configuration
        shared.authentication =
            VelocitySettingsProcessor.locate<Authentication>()!!

        shared.discoverable =
            VelocitySettingsProcessor.locate<Discoverable>()!!
    }
}
