package gg.scala.cloudsync.velocity

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.velocity.settings.VelocitySettingsModel
import gg.scala.cloudsync.velocity.settings.VelocitySettingsProcessor
import gg.scala.flavor.Flavor
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 4/4/2022
 */

object ScalaCloudSyncVelocityPlatform : ScalaCloudSyncPlatform
{
    override fun getLogger() =
        ScalaCloudSyncVelocity.INSTANCE.logger

    override fun getInjector() = Flavor.create<ScalaCloudSyncShared>()

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        contextual<ScalaCloudSyncVelocity> {
            VelocitySettingsProcessor.initialLoad()

            data class Authentication(
                val apiKey: String = "hey"
            ) : VelocitySettingsModel()
            {
                override fun getAbstractType() =
                    Authentication::class.java
            }

            data class Discoverable(
                val discoverable: List<String> =
                    mutableListOf("xyz:xyz:xyz")
            ) : VelocitySettingsModel()
            {
                override fun getAbstractType() =
                    Authentication::class.java
            }

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
                    .let {
                        CloudSyncAuthenticationService.ScalaConnectionAuthenticationWrapper(it.apiKey)
                    }

            shared.discoverable =
                VelocitySettingsProcessor.locate<Discoverable>()!!
                    .let {
                        CloudSyncDiscoveryService.DiscoverableAssets(it.discoverable.toMutableList())
                    }
        }
    }
}
