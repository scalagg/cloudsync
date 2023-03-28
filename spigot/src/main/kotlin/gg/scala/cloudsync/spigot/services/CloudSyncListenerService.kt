package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
    @Inject
    lateinit var audiences: BukkitAudiences

    var filterPlayerCanReceiveNotifications = { player: Player -> true }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent)
    {
        if (event.player.hasPermission("cloudsync.login.notifications") && filterPlayerCanReceiveNotifications.invoke(event.player))
        {
            val start = System.currentTimeMillis()

            CloudSyncConnector.usePendingUpdates { asset, _, name ->
                audiences.player(event.player)
                    .sendMessage(
                        LegacyComponentSerializer.legacySection()
                            .deserialize(
                                "${CC.B_GREEN}Artifactory ${CC.B_GRAY}${Constants.DOUBLE_ARROW_RIGHT}${CC.SEC} A new version of ${CC.PRI}$name${CC.SEC} is available: ${CC.PRI}${asset.version}${CC.SEC}."
                            )
                    )
            }.thenRun {
                if (System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L))
                {
                    event.player.sendMessage("${CC.RED}[This process took more than 5 seconds, is the artifactory alright?]")
                }
            }
        }
    }
}
