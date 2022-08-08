package gg.scala.cloudsync.shared.artifactory

import gg.scala.cloudsync.shared.artifactory.artifact.ArtifactMetadata
import gg.scala.cloudsync.shared.artifactory.artifact.component.ArtifactComponentMetadata
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
object JFrogArtifactory
{
    fun findComponentMetadata(
        path: String, repositoryOverride: String? = null
    ): ArtifactComponentMetadata
    {
        val assets = CloudSyncDiscoveryService.discoverable

        val body = CloudSyncConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return Serializers.gson
            .fromJson(
                body, ArtifactComponentMetadata::class.java
            )
    }

    fun findMetadata(
        path: String, repositoryOverride: String? = null
    ):  ArtifactMetadata
    {
        val assets = CloudSyncDiscoveryService.discoverable

        val body = CloudSyncConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return Serializers.gson
            .fromJson(
                body, ArtifactMetadata::class.java
            )
    }
}
