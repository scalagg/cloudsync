package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.inject.Inject
import org.bukkit.Bukkit
import xyz.mkotb.configapi.ConfigFactory

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
object ScalaCloudSyncSpigotPlatform : ScalaCloudSyncPlatform
{
    @Inject
    lateinit var spigot: ScalaCloudSyncSpigot

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        val factory = ConfigFactory.newFactory(spigot)

        // bukkit file container
        shared.fileContainer = Bukkit.getWorldContainer()

        // configuration
        shared.authentication = factory.fromFile(
            "authentication", CloudSyncAuthenticationService
                .ScalaConnectionAuthenticationWrapper::class.java
        )
        shared.discoverable = factory.fromFile(
            "discoverable", CloudSyncDiscoveryService
                .DiscoverableAssets::class.java
        )
    }
}
