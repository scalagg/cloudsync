package gg.scala.cloudsync.shared.artifactoryv2.artifact.component

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
data class ArtifactComponentMetadata(
    val path: String,
    val uri: String,
    val mimeType: String,
    val downloadUri: String,
    val checksums: ArtifactComponentChecksums,
)
{
    var version = "???"
}
