package gg.scala.cloudsync.spigot.command

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.spigot.pagination.CloudSyncPaginatedResult
import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@AutoRegister
@CommandAlias("cs|cloudsync")
@CommandPermission("cloudsync.command")
object CloudSyncCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("updates")
    @Description("View all pending plugin updates.")
    fun onUpdates(player: Player)
    {
        player.sendMessage("${CC.GREEN}Finding pending plugin updates...")

        var iterated = 0

        CloudSyncConnector.usePendingUpdates { asset, _, name ->
            val fancyMessage = FancyMessage()
            fancyMessage.withMessage("${CC.D_AQUA}$name${CC.YELLOW} is outdated! The newest version is: ${CC.GREEN}${asset.version}${CC.YELLOW}.")
            fancyMessage.sendToPlayer(player)

            iterated++
        }.thenRun {
            if (iterated == 0)
            {
                player.sendMessage("No updates are pending.")
            }
        }
    }

    @Subcommand("tracked")
    @Description("View all tracked components.")
    fun onTracked(sender: CommandSender, @Optional page: Int?)
    {
        CloudSyncPaginatedResult.display(
            sender, CloudSyncDiscoveryService
                .discoverable.assets,
            page ?: 1, "cloudsync tracked %s"
        )

        if (ScalaCloudSyncShared.additionalDependencies.isNotEmpty())
        {
            sender.sendMessage(
                "${CC.WHITE}=== Showing additional dependencies ==="
            )
            CloudSyncPaginatedResult.display(
                sender, ScalaCloudSyncShared.additionalDependencies,
                page ?: 1,
            )
        }
    }
}
