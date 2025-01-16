package gg.scala.cloudsync.shared.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.artifactory.JFrogArtifactory
import gg.scala.cloudsync.shared.artifactory.artifact.ArtifactMetadata
import gg.scala.cloudsync.shared.artifactory.artifact.component.ArtifactComponentMetadata
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kong.unirest.GetRequest
import kong.unirest.Unirest
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 40)
object CloudSyncConnector
{
    @Configure
    fun configure()
    {
        usePendingUpdates { asset, _, name ->
            ScalaCloudSyncShared.logger.warning("$name is outdated! Restart your server to update this plugin! (${asset.version})")
        }
    }

    @Close
    fun close()
    {
        ScalaCloudSyncShared.reload()
        applyPendingUpdates()
    }

    private fun applyPendingUpdates()
    {
        usePendingUpdatesSync { asset, file, name ->
            val start = System.currentTimeMillis()
            ScalaCloudSyncShared.logger.info(
                "Downloading the latest $name (v${asset.version}) archive..."
            )

            try
            {
                request(asset.downloadUri)
                    .asObject {
                        FileUtils.copyInputStreamToFile(
                            it.content, file
                        )
                    }

                ScalaCloudSyncShared.logger.warning(
                    "Successfully updated $name (took ${
                        System.currentTimeMillis() - start
                    }ms)!"
                )
            } catch (exception: Exception)
            {
                ScalaCloudSyncShared.logger.log(
                    Level.SEVERE,
                    "Oops, seems like an exception was thrown during the update process.",
                    exception
                )
            }
        }
    }

    private fun useTrackedAssets(
        lambda: (String, ArtifactMetadata, ArtifactComponentMetadata) -> Unit
    )
    {
        val assets = CloudSyncDiscoveryService
            .discoverable.assets.toMutableSet()

        ScalaCloudSyncShared
            .additionalDependencies
            .onEach(assets::add)

        for (component in assets)
        {
            val composite = component
                .removePrefix("gg.")
                .split(":")

            val composed = "${
                composite[0].replace(".", "/")
            }/${composite[1]}"

            val metadata = JFrogArtifactory
                .findMetadata(
                    composed,
                    composite.getOrNull(3)
                )

            val mostRecent =
                metadata.mostRecent()
                    ?: continue

            val latestVersion = mostRecent.uri
                .replace("/", "")

            val mostRecentMeta = JFrogArtifactory
                .findMetadata(
                    "$composed${mostRecent.uri}",
                    composite.getOrNull(3)
                )

            val archive = mostRecentMeta
                .archive(
                    composite.getOrNull(3)
                )
            archive.version = latestVersion

            lambda.invoke(
                component, mostRecentMeta, archive
            )
        }
    }

    private fun usePendingUpdatesSync(
        lambda: (ArtifactComponentMetadata, File, String) -> Unit
    )
    {
        useTrackedAssets { discoverable, _, component ->
            val fileToOverride = discoverable
                .split(":")[2]

            val jarFile = "$fileToOverride.jar"

            val file = ScalaCloudSyncShared.pluginContainer
                .listFiles()!!
                .firstOrNull {
                    it.name.equals(jarFile, true)
                }
                ?: return@useTrackedAssets

            // checking if the hashes are the same, so we can save some time
            // and avoid updating already up-to-date archives
            val md5Hash = Files
                .asByteSource(file)
                .hash(Hashing.sha256())
                .toString()

            if (md5Hash != component.checksums.sha256)
            {
                lambda.invoke(
                    component, file, fileToOverride
                )
            }
        }
    }

    fun usePendingUpdates(
        lambda: (ArtifactComponentMetadata, File, String) -> Unit
    ): CompletableFuture<Void>
    {
        return CompletableFuture
            .runAsync {
                usePendingUpdatesSync(lambda)
            }
            .exceptionally {
                ScalaCloudSyncShared.logger.log(
                    Level.WARNING, "Exception thrown during pending update task", it
                )
                return@exceptionally null
            }
    }

    fun request(request: String): GetRequest
    {
        val wrapper = CloudSyncAuthenticationService.getWrapper()

        return Unirest.get(request)
            .header("X-JFrog-Art-Api", wrapper.apiKey)
            .connectTimeout(1000)
    }
}
