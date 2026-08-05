package se.svt.oss.encore.service.audiomix

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import se.svt.oss.encore.config.EncodingProperties
import se.svt.oss.encore.config.EncoreProperties
import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.yaml.YAMLMapper
import java.io.IOException

class AudioMixPresetServiceTest {

    private lateinit var mixService: AudioMixPresetService
    private val jsonMapper = JsonMapper.builder().findAndAddModules().build()
    private val yamlMapper = YAMLMapper.builder().findAndAddModules().build()
    private val encoreProperties =
        EncoreProperties(encoding = EncodingProperties(ClassPathResource("audiomixpreset/audio-mix-presets.yml")))

    @BeforeEach
    internal fun setUp() {
        mixService = AudioMixPresetService(
            jsonMapper,
            yamlMapper,
            encoreProperties,
        )
    }

    @Test
    fun `successfully parses existing and valid presets`() {
        val presets = mixService.getAudioMixPresets()
        assertThat(presets).hasSize(2)
        assertThat(presets["default"]).isNotNull
        assertThat(presets["de"]).isNotNull
    }

    @Test
    fun `nonexistent preset throws error`() {
        mixService = AudioMixPresetService(
            jsonMapper,
            yamlMapper,
            encoreProperties.copy(
                encoding = encoreProperties.encoding.copy(
                    audioMixPresetLocation = ClassPathResource(
                        "i-dont-exist",
                    ),
                ),
            ),
        )
        assertThatThrownBy {
            mixService.getAudioMixPresets()
        }
            .isInstanceOf(IOException::class.java)
            .hasMessageStartingWith("class path resource [i-dont-exist] cannot be opened because it does not exist")
    }
}
