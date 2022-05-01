package gg.scala.cloudsync.shared.artifactoryv2.artifact

import gg.scala.cloudsync.shared.artifactoryv2.JFrogArtifactory
import gg.scala.cloudsync.shared.artifactoryv2.artifact.component.ArtifactComponentMetadata

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

    fun jar(): ArtifactComponentMetadata
    {
        val path = "$path${
            this.children.first {
                it.uri.endsWith(".jar")
            }.uri
        }"

        println(path)

        return JFrogArtifactory
            .findComponentMetadata(
                path
            )
    }
}
