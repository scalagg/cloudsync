package gg.scala.cloudsync.shared

import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import java.io.File

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
object ScalaCloudSyncShared
{
    lateinit var fileContainer: File

    lateinit var authentication: CloudSyncAuthenticationService
        .ScalaConnectionAuthenticationWrapper

    lateinit var discoverable: CloudSyncDiscoveryService
        .DiscoverableAssets

    private lateinit var platform: ScalaCloudSyncPlatform

    val logger by lazy {
        platform.getLogger()
    }

    private val flavor by lazy {
        Flavor.create<ScalaCloudSyncShared>(
            FlavorOptions(platform.getLogger())
        )
    }

    fun reload()
    {
        platform.configureServices(this)
    }

    fun enable(platform: ScalaCloudSyncPlatform)
    {
        this.platform = platform
        platform.configureServices(this)

        flavor.bind<ScalaCloudSyncShared>() to this
        flavor.startup()
    }

    fun disable()
    {
        flavor.close()
    }
}
