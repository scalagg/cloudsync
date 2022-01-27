package gg.scala.cloudsync.shared.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.cloudsync.shared.repository.Repositories
import gg.scala.cloudsync.shared.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.shared.ScalaCloudSyncShared
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.shared.repository.component.ComponentWrapper
import gg.scala.cloudsync.shared.repository.component.asset.ComponentAssetWrapper
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kong.unirest.GetRequest
import kong.unirest.Unirest
import kong.unirest.UnirestInstance
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import kotlin.properties.Delegates


/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 40)
object CloudSyncConnector
{
    @Inject
    lateinit var shared: ScalaCloudSyncShared

    private var unirest by Delegates
        .notNull<UnirestInstance>()

    @Configure
    fun configure()
    {
        unirest = Unirest.primaryInstance()
    }

    @Close
    fun close()
    {
        // Get the plugins directory
        val pluginDirectory = File(
            shared.fileContainer, "plugins"
        )

        shared.reload()

        getTrackedAssets().forEach { component ->
            val discoverable = CloudSyncDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[1] == component.name
                }
                ?: return@forEach

            val asset = component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: return@forEach

            val fileToOverride = discoverable
                .split(":")[2]

            val jarFile = "$fileToOverride.jar"

            val file = pluginDirectory.listFiles()!!
                .firstOrNull {
                    it.name.equals(jarFile, true)
                }
                ?: File(pluginDirectory, jarFile)

            // creating the file if it
            // doesn't already exist
            if (!file.exists())
            {
                file.createNewFile()
            } else
            {
                // checking if the hashes are the same, so we can save some time
                // and avoid updating already up-to-date archives
                val md5Hash = Files
                    .asByteSource(file)
                    .hash(Hashing.md5())
                    .toString()

                if (md5Hash == asset.checksum.md5)
                {
                    shared.logger.info("You're running the latest version of $fileToOverride!")
                    return@forEach
                }
            }

            val start = System.currentTimeMillis()
            shared.logger.info("Downloading the latest $fileToOverride archive...")

            try
            {
                request(asset.downloadUrl).asObject {
                    FileUtils.copyInputStreamToFile(
                        it.content, file
                    )
                }

                shared.logger.warning(
                    "Successfully updated $fileToOverride (took ${
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

    fun getTrackedAssetsAsync(): CompletableFuture<List<ComponentWrapper>>
    {
        return Repositories.asyncComponents().thenApply { request ->
            val assets = mutableListOf<ComponentWrapper>()

            for (component in request.items)
            {
                // Checks if this asset should be updated
                CloudSyncDiscoveryService
                    .discoverable.assets
                    .firstOrNull {
                        it.split(":")[0] == component.group &&
                                it.split(":")[1] == component.name
                    }
                    ?: continue

                // Find the jar archive asset we need
                component.assets
                    .firstOrNull {
                        it.maven2 != null &&
                                it.maven2.extension == "jar"
                    }
                    ?: continue

                assets.add(component)
            }

            return@thenApply assets
        }
    }

    private fun getTrackedAssets(): List<ComponentWrapper>
    {
        val assets = mutableListOf<ComponentWrapper>()
        val request = Repositories.components()

        for (component in request.items)
        {
            // Checks if this asset should be updated
            CloudSyncDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[1] == component.name
                }
                ?: continue

            // Find the jar archive asset we need
            component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: continue

            assets.add(component)
        }

        return assets
    }

    fun usePendingUpdates(
        lambda: (String, ComponentAssetWrapper, ComponentWrapper) -> Unit
    )
    {
        val pluginDirectory = File(
            shared.fileContainer, "plugins"
        )

        getTrackedAssetsAsync().thenApplyAsync { wrappers ->
            wrappers.forEach { component ->
                val discoverable = CloudSyncDiscoveryService
                    .discoverable.assets
                    .firstOrNull {
                        it.split(":")[0] == component.group &&
                                it.split(":")[1] == component.name
                    }
                    ?: return@forEach

                val asset = component.assets
                    .firstOrNull {
                        it.maven2 != null &&
                                it.maven2.extension == "jar"
                    }
                    ?: return@forEach

                val fileToOverride = discoverable
                    .split(":")[2]

                val jarFile = "$fileToOverride.jar"

                val file = pluginDirectory.listFiles()!!
                    .firstOrNull {
                        it.name.equals(jarFile, true)
                    }

                // creating the file if it
                // doesn't already exist
                if (file != null)
                {
                    // checking if the hashes are the same, so we can save some time
                    // and avoid updating already up-to-date archives
                    val md5Hash = Files
                        .asByteSource(file)
                        .hash(Hashing.md5())
                        .toString()

                    if (md5Hash != asset.checksum.md5)
                    {
                        lambda.invoke(fileToOverride, asset, component)
                    }
                }
            }
        }
    }

    fun request(request: String): GetRequest
    {
        val wrapper =
            CloudSyncAuthenticationService.getWrapper()

        return unirest.get(request)
            .basicAuth(wrapper.username, wrapper.password)
            .connectTimeout(1000)
    }
}
