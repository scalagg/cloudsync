package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginDependencyComposite
import gg.scala.commons.core.plugin.PluginWebsite

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Plugin(
    name = "cloudsync",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons")
)
@ContainerConfig(
    value = "authentication",
    model = CloudSyncAuthenticationService
        .ScalaConnectionAuthenticationWrapper::class
)
@ContainerConfig(
    value = "discoverable",
    model = CloudSyncDiscoveryService
        .DiscoverableAssets::class
)
class ScalaCloudSyncSpigot : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {
        flavor {
            bind<ScalaCloudSyncSpigot>() to this@ScalaCloudSyncSpigot
            inject(ScalaCloudSyncSpigotPlatform)
        }

        ScalaCloudSyncShared
            .enable(ScalaCloudSyncSpigotPlatform)
    }
}
