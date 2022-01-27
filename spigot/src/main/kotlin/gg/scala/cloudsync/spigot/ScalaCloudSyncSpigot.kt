package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Plugin(
    name = "cloudsync",
    description = "Advanced server <-> cloud synchronization platform.",
    depends = [
        PluginDependency("Cubed")
    ]
)
class ScalaCloudSyncSpigot : ExtendedScalaPlugin()
{
    private val flavor by lazy {
        Flavor.create<ScalaCloudSyncSpigot>(
            FlavorOptions(logger, "gg.scala.cloudsync.spigot")
        )
    }

    override fun enable()
    {
        flavor.bind<ScalaCloudSyncSpigot>() to this
        flavor.inject(ScalaCloudSyncSpigotPlatform)

        ScalaCloudSyncShared.enable(ScalaCloudSyncSpigotPlatform)

        flavor.startup()
    }

    override fun disable()
    {
        flavor.close()
        ScalaCloudSyncShared.disable()
    }
}
