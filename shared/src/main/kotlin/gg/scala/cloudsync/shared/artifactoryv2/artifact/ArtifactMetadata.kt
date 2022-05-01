package gg.scala.cloudsync.shared.artifactoryv2.artifact

import gg.scala.cloudsync.shared.artifactoryv2.JFrogArtifactory

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
data class ArtifactMetadata(
    val path: String,
    val children: List<ArtifactChild>
)
{
    fun mostRecent() = children
        .lastOrNull { it.folder }

    fun jar() = JFrogArtifactory
        .findComponentMetadata(
            this.children.first {
                it.uri.endsWith(".jar")
            }.uri
        )
}
