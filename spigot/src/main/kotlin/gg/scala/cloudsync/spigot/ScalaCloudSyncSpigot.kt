package gg.scala.cloudsync.spigot

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ExtendedJavaPlugin

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
class ScalaCloudSyncSpigot : ExtendedJavaPlugin()
{
    val flavor by lazy {
        Flavor
            .create<ScalaCloudSyncSpigot>(
                FlavorOptions(logger)
            )
    }

    override fun enable()
    {
        ScalaCloudSyncShared
            .enable(ScalaCloudSyncSpigotPlatform(this), "Spigot")
    }

    override fun disable()
    {
        ScalaCloudSyncShared.disable()
    }
}
