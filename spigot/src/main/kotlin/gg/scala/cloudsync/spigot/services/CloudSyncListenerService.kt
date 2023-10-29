package gg.scala.cloudsync.spigot.services

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.cloudsync.spigot.ScalaCloudSyncSpigot
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
object CloudSyncListenerService : Listener
{
    var filterPlayerCanReceiveNotifications = { _: Player -> true }

    @Inject
    lateinit var plugin: ScalaCloudSyncSpigot

    fun configure(plugin: ScalaCloudSyncSpigot)
    {
        this.plugin = plugin
        plugin.server.pluginManager.registerEvents(
            this, plugin
        )
    }

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
                            ChatColor.translateAlternateColorCodes(
                                '&',
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
                            "${ChatColor.RED}[This process took more than 5 seconds, is the artifactory alright?]"
                        )
                    }
                }
        }
    }
}
