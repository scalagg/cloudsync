package gg.scala.cloudsync.shared

import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 1/26/2022
 */
interface ScalaCloudSyncPlatform
{
    fun getLogger(): Logger
    fun configureServices(shared: ScalaCloudSyncShared)
}
