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
        val username: String = "admin",
        val password: String = "6^QaR8lEt5gVEEOYLHlof^EenbUqzBp3!480XnrLY!SAH4&hdZ",
    ) : AbstractTypeSerializable
    {
        override fun getAbstractType() =
            ScalaConnectionAuthenticationWrapper::class.java
    }
}
