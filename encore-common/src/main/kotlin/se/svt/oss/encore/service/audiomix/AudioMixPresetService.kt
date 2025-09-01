package se.svt.oss.encore.service.audiomix

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import se.svt.oss.encore.config.AudioMixPreset
import se.svt.oss.encore.config.AudioMixProperties
import se.svt.oss.encore.model.EncoreJob
import se.svt.oss.encore.model.profile.AudioEncode
import java.io.File
import java.util.Locale

private val log = KotlinLogging.logger {}

@Service
@RegisterReflectionForBinding(AudioMixPreset::class)
@EnableConfigurationProperties(AudioMixProperties::class)
class AudioMixPresetService(
    private val properties: AudioMixProperties,
    private val objectMapper: ObjectMapper,
) {
    private val yamlMapper: YAMLMapper =
        YAMLMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) as YAMLMapper

    private fun mapper() =
        if (properties.location.filename?.let {
                File(it).extension.lowercase(Locale.getDefault()) in setOf("yml", "yaml")
            } == true) {
            yamlMapper
        } else {
            objectMapper
        }

    fun getAudioMixPreset(encode: AudioEncode): AudioMixPreset = try {
        log.debug { "Get audio mix preset ${encode.audioMixPreset}. Reading presets from ${properties.location}" }
        val presets = mapper().readValue<Map<String, AudioMixPreset>>(properties.location.inputStream)

        presets[encode.audioMixPreset]
            ?: throw RuntimeException("Could not find preset ${encode.audioMixPreset}! presets: ${presets}")
    } catch (e: JsonProcessingException) {
        throw RuntimeException("Error parsing audio mix preset ${e.message}")
    }
}
