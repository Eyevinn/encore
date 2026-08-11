package se.svt.oss.encore.service.audiomix

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.stereotype.Service
import se.svt.oss.encore.config.AudioMixPreset
import se.svt.oss.encore.config.EncoreProperties
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue
import java.io.File
import java.util.Locale

private val log = KotlinLogging.logger {}

@Service
@RegisterReflectionForBinding(AudioMixPreset::class)
class AudioMixPresetService(
    private val jsonMapper: JsonMapper,
    private val yamlMapper: YAMLMapper,
    private val encoreProperties: EncoreProperties,
) {
    private fun mapper() =
        if (encoreProperties.encoding.audioMixPresetLocation?.filename?.let {
                File(it).extension.lowercase(Locale.getDefault()) in setOf("yml", "yaml")
            } == true
        ) {
            yamlMapper
        } else {
            jsonMapper
        }

    fun getAudioMixPresets(): Map<String, AudioMixPreset> = try {
        log.debug { "Reading presets from ${encoreProperties.encoding.audioMixPresetLocation}" }
        encoreProperties.encoding.audioMixPresetLocation?.let { location ->
            mapper().readValue<Map<String, AudioMixPreset>>(location.inputStream)
        } ?: encoreProperties.encoding.audioMixPresets
    } catch (e: JacksonException) {
        throw RuntimeException("Error parsing audio mix presets ${e.message}")
    }
}
