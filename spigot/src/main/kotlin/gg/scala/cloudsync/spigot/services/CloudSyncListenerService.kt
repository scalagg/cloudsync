package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.spigot.ScalaCloudSyncSpigot
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service
object CloudSyncListenerService
{
    @Inject
    lateinit var plugin: ScalaCloudSyncSpigot

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                CloudSyncConnector.usePendingUpdates { asset, _, name ->
                    val fancyMessage = FancyMessage()
                    fancyMessage.withMessage("${CC.GREEN}[$name]${CC.YELLOW} I'm outdated! The newest version is: ${CC.GREEN}${asset.maven2!!.version}${CC.YELLOW}.")
                    fancyMessage.sendToPlayer(it.player)
                }
            }
            .bindWith(plugin)
    }
}
