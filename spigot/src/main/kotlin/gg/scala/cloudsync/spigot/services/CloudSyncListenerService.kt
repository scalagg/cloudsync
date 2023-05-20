package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Constants
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
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

    var filterPlayerCanReceiveNotifications = { _: Player -> true }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent)
    {
        if (
            event.player.hasPermission("cloudsync.login.notifications") &&
            filterPlayerCanReceiveNotifications.invoke(event.player)
        )
        {
            val start = System.currentTimeMillis()
            val pendingUpdates = mutableListOf<Pair<String, String>>()

            CloudSyncConnector
                .usePendingUpdates { asset, _, name ->
                    pendingUpdates += name to asset.version
                }
                .thenRun {
                    if (pendingUpdates.isNotEmpty())
                    {
                        event.player.sendMessage(
                            Color.translate(
                                ScalaCloudSyncShared
                                    .createLoginMessage(
                                        *pendingUpdates.toTypedArray()
                                    )
                            )
                        )
                    }

                    if (
                        System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L) &&
                        pendingUpdates.isEmpty()
                    )
                    {
                        event.player.sendMessage(
                            "${CC.RED}[This process took more than 5 seconds, is the artifactory alright?]"
                        )
                    }
                }
        }
    }
}
