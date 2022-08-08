package gg.scala.cloudsync.shared.artifactory

import gg.scala.cloudsync.shared.ScalaCloudSyncSharedConstants
import gg.scala.cloudsync.shared.artifactory.artifact.ArtifactMetadata
import gg.scala.cloudsync.shared.artifactory.artifact.component.ArtifactComponentMetadata
import gg.scala.cloudsync.shared.connection.CloudSyncConnector
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
object JFrogArtifactory
{
    fun findComponentMetadata(
        path: String
    ): ArtifactComponentMetadata
    {
        val body = CloudSyncConnector
            .request(
                "${ScalaCloudSyncSharedConstants.ARTIFACTORY_URL}/artifactory/api/storage/${
                    ScalaCloudSyncSharedConstants.ARTIFACTORY_RELEASES
                }/$path"
            )
            .asString().body

        return Serializers.gson
            .fromJson(
                body, ArtifactComponentMetadata::class.java
            )
    }

    fun findMetadata(
        path: String
    ):  ArtifactMetadata
    {
        val body = CloudSyncConnector
            .request(
                "${ScalaCloudSyncSharedConstants.ARTIFACTORY_URL}/artifactory/api/storage/${
                    ScalaCloudSyncSharedConstants.ARTIFACTORY_RELEASES
                }/$path"
            )
            .asString().body

        return Serializers.gson
            .fromJson(
                body, ArtifactMetadata::class.java
            )
    }
}
