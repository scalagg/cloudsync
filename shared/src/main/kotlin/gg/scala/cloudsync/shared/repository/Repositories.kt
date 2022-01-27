package gg.scala.cloudsync.shared.repository

import gg.scala.cloudsync.shared.ScalaCloudSyncSharedConstants
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.cloudsync.shared.repository.repositories.ComponentPaginatedRepositoryWrapper
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
object Repositories
{
    /**
     * Retrieve all components within the
     * releases' category.
     *
     * Note: Nexus makes use of a pagination feature,
     * so we have to make multiple requests to retrieve all components.
     */
    fun components(): ComponentPaginatedRepositoryWrapper
    {
        var request = CloudSyncConnector.request(
            ScalaCloudSyncSharedConstants.COMPONENTS
        )

        val wrapper = ComponentPaginatedRepositoryWrapper()
        var iterated: ComponentPaginatedRepositoryWrapper? = null

        while (iterated == null || iterated.continuationToken != null)
        {
            if (iterated?.continuationToken != null)
            {
                request = CloudSyncConnector.request(
                    ScalaCloudSyncSharedConstants.COMPONENTS +
                            "&continuationToken=${iterated.continuationToken}"
                )
            }

            iterated = Serializers.gson
                .fromJson(
                    request.asJson().body.toPrettyString(),
                    ComponentPaginatedRepositoryWrapper::class.java
                )

            wrapper.items.addAll(iterated.items)
        }

        return wrapper
    }

    fun asyncComponents(): CompletableFuture<ComponentPaginatedRepositoryWrapper>
    {
        return CompletableFuture
            .supplyAsync { components() }
    }
}
