package gg.scala.cloudsync.shared.authentication

import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 50)
object CloudSyncAuthenticationService
{
    @Inject
    lateinit var shared: ScalaCloudSyncShared

    private var authentication by Delegates
        .notNull<ScalaConnectionAuthenticationWrapper>()

    @Configure
    fun configure()
    {
        authentication = shared.authentication
    }

    fun getWrapper() = authentication

    class ScalaConnectionAuthenticationWrapper(
        val username: String = "admin",
        val password: String = "password"
    )
}
