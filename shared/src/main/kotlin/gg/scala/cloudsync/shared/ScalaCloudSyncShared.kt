package gg.scala.cloudsync.shared

import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import java.io.File

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
object ScalaCloudSyncShared
{
    lateinit var pluginContainer: File

    lateinit var authentication: CloudSyncAuthenticationService
        .ScalaConnectionAuthenticationWrapper

    lateinit var discoverable: CloudSyncDiscoveryService
        .DiscoverableAssets

    private lateinit var platform: ScalaCloudSyncPlatform

    val logger by lazy {
        platform.getLogger()
    }

    fun reload()
    {
        platform.configureServices(this)
    }

    val additionalDependencies = mutableListOf<String>()

    fun enable(platform: ScalaCloudSyncPlatform, platformName: String)
    {
        this.platform = platform
        platform.configureServices(this)

        platform.getInjector()
            .bind<ScalaCloudSyncShared>() to this

        val globalContextDirectory = File(
            System.getProperty("user.home"),
            ".cloudsync"
        )

        if (!globalContextDirectory.exists())
        {
            globalContextDirectory.mkdirs()
        }

        val globalContextFile = File(
            globalContextDirectory,
            "${platformName.lowercase()}.txt"
        )

        if (!globalContextFile.exists())
        {
            globalContextFile.createNewFile()
            logger.warning(
                "A $platformName context file has been created at: ${globalContextFile.absolutePath}. Use this file to store any updating plugins for your $platformName servers."
            )
            return
        }

        additionalDependencies.addAll(globalContextFile.readLines())
    }

    fun disable()
    {
        platform.getInjector().close()
    }

    fun createLoginMessage(
        vararg updates: Pair<String, String>,
        theme: String = "&6",
    ): String
    {
        var loginMessage =
            "${theme}Plugin updates are available for: "

        for ((index, pair) in updates.withIndex())
        {
            if (
                updates.size != 1 &&
                index == updates.size - 1
            )
            {
                loginMessage += "and "
            }

            loginMessage += "&f${pair.first} &7(${pair.second})${theme}${
                if (index == updates.size - 1) "." else ", "
            }"
        }

        return loginMessage
    }
}
