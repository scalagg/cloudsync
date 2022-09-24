package gg.scala.cloudsync.shared.authentication

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 50)
object CloudSyncAuthenticationService
{
    lateinit var authentication: ScalaConnectionAuthenticationWrapper

    @Configure
    fun configure()
    {
        authentication = ScalaCloudSyncShared.authentication
    }

    fun getWrapper() = authentication

    class ScalaConnectionAuthenticationWrapper(
        val apiKey: String = "token"
    ) : AbstractTypeSerializable
    {
        override fun getAbstractType() =
            ScalaConnectionAuthenticationWrapper::class.java
    }
}
