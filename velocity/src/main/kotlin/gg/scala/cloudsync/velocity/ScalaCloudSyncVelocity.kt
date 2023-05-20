package gg.scala.cloudsync.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 4/4/2022
 */
@Plugin(
    id = "cloudsync",
    name = "cloudsync",
    description = "Advanced server <-> cloud synchronization platform.",
    version = "1.0.1",
    url = "https://scala.gg/",
    authors = ["Scala"]
)
class ScalaCloudSyncVelocity
@Inject
constructor(
    val logger: Logger,
    @DataDirectory
    private val directory: Path
)
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates
            .notNull<ScalaCloudSyncVelocity>()
    }

    init
    {
        INSTANCE = this
    }

    val directoryFile: File = directory
        .toFile()
        .apply {
            if (!exists())
                mkdirs()
        }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent)
    {
        contextual<ScalaCloudSyncShared> {
            ScalaCloudSyncShared.enable(ScalaCloudSyncVelocityPlatform, "Velocity")
        }
    }

    @Subscribe(order = PostOrder.LATE)
    fun onLogin(event: ServerConnectedEvent)
    {
        if (
            !event.previousServer.isPresent &&
            event.player.hasPermission("cloudsync.login.notifications")
        )
        {
            val start = System.currentTimeMillis()
            val pendingUpdates = mutableListOf<Pair<String, String>>()

            fun String.legacyToComponent() = LegacyComponentSerializer
                .legacyAmpersand()
                .deserialize(this)

            CloudSyncConnector
                .usePendingUpdates { asset, _, name ->
                    pendingUpdates += name to asset.version
                }
                .thenRun {
                    if (pendingUpdates.isNotEmpty())
                    {
                        event.player.sendMessage(
                            ScalaCloudSyncShared
                                .createLoginMessage(
                                    *pendingUpdates.toTypedArray(),
                                    theme = "&b"
                                )
                                .legacyToComponent()
                        )
                    }

                    if (
                        System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L) &&
                        pendingUpdates.isEmpty()
                    )
                    {
                        event.player.sendMessage(
                            "&c[This process took more than 5 seconds, is the artifactory alright?]"
                                .legacyToComponent()
                        )
                    }
                }
        }
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent)
    {
        CloudSyncConnector.close()
    }
}

inline fun <reified T> contextual(
    noinline lambda: () -> Unit
)
{
    val classLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = T::class.java.classLoader

    runCatching(lambda)
        .onFailure {
            it.printStackTrace()
        }

    Thread.currentThread().contextClassLoader = classLoader
}
