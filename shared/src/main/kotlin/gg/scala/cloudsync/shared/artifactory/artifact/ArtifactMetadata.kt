package gg.scala.cloudsync.shared.artifactory.artifact

import gg.scala.cloudsync.shared.artifactory.JFrogArtifactory
import gg.scala.cloudsync.shared.artifactory.artifact.component.ArtifactComponentMetadata

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
data class ArtifactMetadata(
    val path: String,
    val children: List<ArtifactChild>?
)
{
    fun mostRecent() = children
        ?.lastOrNull { it.folder }

    fun archive(
        repositoryOverride: String? = null
    ): ArtifactComponentMetadata
    {
        val path = "$path${
            this.children!!
                .first {
                    it.uri.endsWith(".jar")
                }
                .uri
        }"

        return JFrogArtifactory
            .findComponentMetadata(
                path, repositoryOverride
            )
    }
}
