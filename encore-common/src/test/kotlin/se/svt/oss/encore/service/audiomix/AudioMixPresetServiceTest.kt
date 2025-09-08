package se.svt.oss.encore.service.audiomix

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import se.svt.oss.encore.config.AudioMixProperties
import java.io.IOException

class AudioMixPresetServiceTest {

    private lateinit var mixService: AudioMixPresetService
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @BeforeEach
    internal fun setUp() {
        mixService = AudioMixPresetService(
            AudioMixProperties(
                ClassPathResource("audiomixpreset/audio-mix-presets.yml"),
            ),
            objectMapper,
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
            AudioMixProperties(
                ClassPathResource("non-existent"),
            ),
            objectMapper,
        )
        assertThatThrownBy { mixService.getAudioMixPresets() }
            .isInstanceOf(IOException::class.java)
            .hasMessageStartingWith("class path resource [non-existent] cannot be opened because it does not exist")
    }
}
