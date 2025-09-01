package se.svt.oss.encore.service.audiomix

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import se.svt.oss.encore.config.AudioMixProperties
import se.svt.oss.encore.model.profile.AudioEncode

class AudioMixPresetServiceTest {

    private lateinit var mixService: AudioMixPresetService
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @BeforeEach
    internal fun setUp() {
        mixService = AudioMixPresetService(
            AudioMixProperties(
                ClassPathResource("audiomixpreset/audio-mix-presets.yml")
            ),
            objectMapper,
        )
    }

    @Test
    fun `successfully parses existing and valid presets`() {
        listOf("default", "de").forEach {
            mixService.getAudioMixPreset(encodeWithMixPreset(it))
        }
    }

    @Test
    fun `nonexistent preset throws error`(){
        assertThatThrownBy {mixService.getAudioMixPreset(encodeWithMixPreset("non-existent"))}
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageStartingWith("Could not find preset")
    }


    private fun encodeWithMixPreset(preset: String) = AudioEncode(audioMixPreset = preset)
}
