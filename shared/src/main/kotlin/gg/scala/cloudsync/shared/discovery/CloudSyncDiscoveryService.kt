package gg.scala.cloudsync.shared.discovery

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 45)
object CloudSyncDiscoveryService
{
    var discoverable by Delegates
        .notNull<DiscoverableAssets>()

    @Configure
    fun configure()
    {
        discoverable = ScalaCloudSyncShared.discoverable
    }

    class DiscoverableAssets(
        val discoveryUrl: String = "https://artifactory.scala.gg",
        val discoveryRepositories: List<String> = listOf(
            "gradle-release-local"
        ),
        val assets: MutableList<String> = mutableListOf(
            "gg.scala.commons:bukkit:sccommons-bukkit",
        )
    ) : AbstractTypeSerializable
    {
        override fun getAbstractType() =
            DiscoverableAssets::class.java
    }
}
