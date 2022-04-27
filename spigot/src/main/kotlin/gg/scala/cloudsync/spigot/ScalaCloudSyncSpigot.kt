package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Plugin(
    name = "cloudsync",
    apiVersion = "1.18",
    description = "Advanced server <-> cloud synchronization platform.",
    depends = [
        PluginDependency("scala-commons")
    ]
)
class ScalaCloudSyncSpigot : ExtendedScalaPlugin()
{
    override fun enable()
    {
        flavor {
            bind<ScalaCloudSyncSpigot>() to this@ScalaCloudSyncSpigot
            inject(ScalaCloudSyncSpigotPlatform)
        }

        ScalaCloudSyncShared
            .enable(ScalaCloudSyncSpigotPlatform)
    }

    override fun disable()
    {
        ScalaCloudSyncShared.disable()
    }
}
