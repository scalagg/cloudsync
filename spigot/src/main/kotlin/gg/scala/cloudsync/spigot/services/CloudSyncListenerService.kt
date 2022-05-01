package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Listeners
object CloudSyncListenerService : Listener
{
    @EventHandler
    fun onJoin(event: PlayerJoinEvent)
    {
        if (!event.player.isOp)
            return

        val start = System.currentTimeMillis()

        CloudSyncConnector.usePendingUpdates { asset, _, name ->
            val fancyMessage = FancyMessage()
            fancyMessage.withMessage("${CC.GREEN}[JFrog] ${CC.D_AQUA}$name ${CC.YELLOW} is outdated! The newest version is: ${CC.GREEN}${asset.version}${CC.YELLOW}.")
            fancyMessage.sendToPlayer(event.player)
        }.thenRun {
            if (System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L))
            {
                event.player.sendMessage("${CC.RED}[This process took more than 5 seconds, is the artifactory alright?]")
            }
        }
    }
}
