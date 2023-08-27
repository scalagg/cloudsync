package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.inject.Inject
import org.bukkit.Bukkit
import xyz.mkotb.configapi.ConfigFactory
import java.io.File

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
object ScalaCloudSyncSpigotPlatform : ScalaCloudSyncPlatform
{
    @Inject
    lateinit var spigot: ScalaCloudSyncSpigot

    override fun getLogger() = spigot.logger
    override fun getInjector() = spigot.flavor

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        // bukkit file container
        shared.pluginContainer = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        val configFactory = ConfigFactory.newFactory(spigot)

        // configuration
        shared.authentication = configFactory.fromFile("authentication.yaml", CloudSyncAuthenticationService.ScalaConnectionAuthenticationWrapper::class.java)
        shared.discoverable = configFactory.fromFile("discovery.yaml", CloudSyncDiscoveryService.DiscoverableAssets::class.java)
            .apply {
                assets += "gg.scala.cloudsync:spigot:cloudsync"
                assets += "gg.scala.store:spigot:store-spigot"
            }
    }
}
