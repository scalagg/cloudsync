package gg.scala.cloudsync.shared.artifactoryv2

import gg.scala.cloudsync.shared.ScalaCloudSyncSharedConstants
import gg.scala.cloudsync.shared.artifactoryv2.artifact.ArtifactMetadata
import gg.scala.cloudsync.shared.artifactoryv2.artifact.component.ArtifactComponentMetadata
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
        return Serializers.gson
            .fromJson(
                CloudSyncConnector.request(
                    "${ScalaCloudSyncSharedConstants.ARTIFACTORY_URL}/api/storage/${
                        ScalaCloudSyncSharedConstants.ARTIFACTORY_RELEASES
                    }/$path"
                ).asJson().body.toString(),
                ArtifactComponentMetadata::class.java
            )
    }

    fun findMetadata(
        path: String
    ): ArtifactMetadata
    {
        return Serializers.gson
            .fromJson(
                CloudSyncConnector.request(
                    "${ScalaCloudSyncSharedConstants.ARTIFACTORY_URL}/api/storage/${
                        ScalaCloudSyncSharedConstants.ARTIFACTORY_RELEASES
                    }/$path"
                ).asJson().body.toString(),
                ArtifactMetadata::class.java
            )
    }
}
