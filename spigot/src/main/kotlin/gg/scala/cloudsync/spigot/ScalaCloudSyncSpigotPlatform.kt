package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncPlatform
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.inject.Inject
import org.bukkit.Bukkit
import xyz.mkotb.configapi.ConfigFactory
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
object ScalaCloudSyncSpigotPlatform : ScalaCloudSyncPlatform
{
    @Inject
    lateinit var spigot: ScalaCloudSyncSpigot

    override fun getLogger(): Logger = spigot.logger

    override fun configureServices(shared: ScalaCloudSyncShared)
    {
        // bukkit file container
        shared.fileContainer = Bukkit.getWorldContainer()

        // configuration
        shared.authentication = spigot.config()
        shared.discoverable = spigot.config()
    }
}
