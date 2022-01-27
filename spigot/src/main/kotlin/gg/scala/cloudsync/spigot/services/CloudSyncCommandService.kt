package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.spigot.ScalaCloudSyncSpigot
import gg.scala.cloudsync.spigot.command.CloudSyncCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.command.manager.CubedCommandManager

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@Service
object CloudSyncCommandService
{
    @Inject
    lateinit var plugin: ScalaCloudSyncSpigot

    @Configure
    fun configure()
    {
        val manager = CubedCommandManager(plugin)
        manager.registerCommand(CloudSyncCommand)
    }
}
