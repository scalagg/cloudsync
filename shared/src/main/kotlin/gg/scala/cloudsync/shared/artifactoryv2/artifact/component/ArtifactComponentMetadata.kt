package gg.scala.cloudsync.shared.artifactoryv2.artifact.component

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
data class ArtifactComponentMetadata(
    val size: Int,
    val path: String,
    val checksums: ArtifactComponentChecksums,
    val downloadUri: String,
    val mimeType: String,
    val uri: String,
    val originalChecksums: ArtifactComponentChecksums
)
{
    val version by lazy {
        val split = this.path
            .split("/")

        split[split.size - 2]
    }
}
