package se.svt.oss.encore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource

@ConfigurationProperties("audio-mix-presets")
data class AudioMixProperties(
    val location: Resource,
)
