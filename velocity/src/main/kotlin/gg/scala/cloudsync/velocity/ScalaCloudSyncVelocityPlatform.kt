package gg.scala.cloudsync.velocity

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.velocity.settings.VelocitySettingsModel
import gg.scala.cloudsync.velocity.settings.VelocitySettingsProcessor
import gg.scala.flavor.Flavor

/**
 * @author GrowlyX
 * @since 4/4/2022
 */

object ScalaCloudSyncVelocityPlatform : ScalaCloudSyncPlatform
{
    override fun getLogger() =
        ScalaCloudSyncVelocity.INSTANCE.logger

    data class Authentication(
        val apiKey: String = "hey"
    ) : VelocitySettingsModel()
    {
        override fun getAbstractType() =
            Authentication::class.java
    }

    data class Discoverable(
        val discoveryUrl: String = "https://artifactory.scala.gg",
        val discoveryRepositories: List<String> = listOf("gradle-release-local"),
        val discoverable: List<String> =
            mutableListOf("xyz:xyz:xyz")
    ) : VelocitySettingsModel()
    {
        override fun getAbstractType() =
            Authentication::class.java
    }

    override fun getInjector() = Flavor.create<ScalaCloudSyncShared>()

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        contextual<ScalaCloudSyncVelocity> {
            VelocitySettingsProcessor.initialLoad()

            VelocitySettingsProcessor
                .loadSettings<Authentication>("authentication")
            VelocitySettingsProcessor
                .loadSettings<Discoverable>("discoverable")

            // velocity file container
            shared.pluginContainer = ScalaCloudSyncVelocity.INSTANCE
                .directoryFile.parentFile

            // configuration
            shared.authentication =
                VelocitySettingsProcessor.locate<Authentication>()!!
                    .let {
                        CloudSyncAuthenticationService.ScalaConnectionAuthenticationWrapper(it.apiKey)
                    }

            shared.discoverable =
                VelocitySettingsProcessor.locate<Discoverable>()!!
                    .let {
                        CloudSyncDiscoveryService.DiscoverableAssets(
                            it.discoveryUrl,
                            it.discoveryRepositories,
                            it.discoverable.toMutableList(),
                        )
                    }

            getInjector().bind<ScalaCloudSyncPlatform>() to this@ScalaCloudSyncVelocityPlatform
            getInjector().bind<ScalaCloudSyncShared>() to ScalaCloudSyncShared

            CloudSyncAuthenticationService.configure()
            CloudSyncDiscoveryService.configure()
            CloudSyncConnector.configure()
        }
    }
}
