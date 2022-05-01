package gg.scala.cloudsync.shared.artifactoryv2.artifact.component

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
data class ArtifactComponentChecksums(
    val sha1: String,
    val md5: String,
    val sha256: String
)
