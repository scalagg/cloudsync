package gg.scala.cloudsync.shared.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.artifactoryv2.JFrogArtifactory
import gg.scala.cloudsync.shared.artifactoryv2.artifact.ArtifactMetadata
import gg.scala.cloudsync.shared.artifactoryv2.artifact.component.ArtifactComponentMetadata
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
    @Inject
    lateinit var shared: ScalaCloudSyncShared

    @Configure
    fun configure()
    {
        usePendingUpdates { asset, _, name ->
            shared.logger.warning("$name is outdated! Restart your server to update this plugin! (${asset.version})")
        }
    }

    @Close
    fun close()
    {
        this.shared.reload()
        applyPendingUpdates()
    }

    private fun applyPendingUpdates()
    {
        usePendingUpdatesSync { asset, file, name ->
            val start = System.currentTimeMillis()
            shared.logger.info("Downloading the latest $name (v${asset.version}) archive...")

            try
            {
                request(asset.downloadUri)
                    .asObject {
                        FileUtils.copyInputStreamToFile(
                            it.content, file
                        )
                    }

                shared.logger.warning(
                    "Successfully updated $name (took ${
                        System.currentTimeMillis() - start
                    }ms)!"
                )
            } catch (exception: Exception)
            {
                shared.logger.log(
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
        for (
        component in CloudSyncDiscoveryService
            .discoverable.assets
        )
        {
            val composite = component
                .split(":")

            val composted = "${
                composite[0].replace(".", "/")
            }/${composite[1]}"

            val metadata = JFrogArtifactory
                .findMetadata(composted)

            val mostRecent =
                metadata.mostRecent()
                    ?: continue

            val mostRecentMeta = JFrogArtifactory
                .findMetadata(mostRecent.uri)

            lambda.invoke(
                component, mostRecentMeta,
                mostRecentMeta.jar()
            )
        }
    }

    private fun usePendingUpdatesSync(
        lambda: (ArtifactComponentMetadata, File, String) -> Unit
    )
    {
        val pluginDirectory = File(
            shared.fileContainer, "plugins"
        )

        useTrackedAssets { discoverable, _, component ->
            val fileToOverride = discoverable
                .split(":")[2]

            val jarFile = "$fileToOverride.jar"

            val file = pluginDirectory.listFiles()!!
                .firstOrNull {
                    it.name.equals(jarFile, true)
                }
                ?: return@useTrackedAssets

            // checking if the hashes are the same, so we can save some time
            // and avoid updating already up-to-date archives
            val md5Hash = Files
                .asByteSource(file)
                .hash(Hashing.md5())
                .toString()

            if (md5Hash != component.checksums.md5)
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
        return CompletableFuture.runAsync {
            usePendingUpdatesSync(lambda)
        }
    }

    fun request(request: String): GetRequest
    {
        val wrapper =
            CloudSyncAuthenticationService.getWrapper()

        return Unirest.get(request)
            .basicAuth(wrapper.username, wrapper.password)
            .connectTimeout(1000)
    }
}
