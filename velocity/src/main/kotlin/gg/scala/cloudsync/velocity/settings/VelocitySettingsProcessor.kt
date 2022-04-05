package gg.scala.cloudsync.velocity.settings

import com.google.common.io.Files
import gg.scala.cloudsync.velocity.ScalaCloudSyncVelocity
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import java.io.File
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/15/2021
 */
object VelocitySettingsProcessor
{
    val settings = mutableMapOf<KClass<*>, AbstractTypeSerializable>()

    fun initialLoad()
    {
        Serializers.useGsonBuilderThenRebuild {
            it.setPrettyPrinting()
            it.registerTypeAdapter(
                VelocitySettingsModel::class.java,
                AbstractTypeSerializer<AbstractTypeSerializable>()
            )
        }

        ScalaCloudSyncVelocity.INSTANCE.logger
            .info("Configured Serializers.kt to support VelocitySettingsModel.")
    }

    inline fun <reified T> locate(): T?
    {
        return settings[T::class] as T
    }

    inline fun <reified T : AbstractTypeSerializable> loadSettings(id: String)
    {
        saveSettingsToFile(T::class, id)

        val reader = File(
            ScalaCloudSyncVelocity.INSTANCE.directoryFile,
            "$id.json"
        ).reader()

        settings[T::class] = Serializers.gson.fromJson(reader, T::class.java)
    }

    fun saveSettingsToFile(
        kClass: KClass<*>, id: String
    )
    {
        val file = File(
            ScalaCloudSyncVelocity.INSTANCE.directoryFile,
            "$id.json"
        )

        if (!file.exists())
        {
            file.createNewFile()

            settings.putIfAbsent(
                kClass, kClass.java.newInstance() as VelocitySettingsModel
            )
        }

        if (settings[kClass] != null)
        {
            Files.write(
                Serializers.gson.toJson(
                    settings[kClass], kClass.java
                ),
                file, Charsets.UTF_8
            )
        }
    }
}
