// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.encore.config

import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.core.io.Resource
import se.svt.oss.encore.model.profile.ChannelLayout

data class SegmentedEncodingProperties(
    val enabledForAudio: Boolean = true,
)

data class SpeechToTextProperties(
    val models: Map<String, String> = emptyMap(),
)

data class EncodingProperties(
    val audioMixPresetLocation: Resource? = null,
    @NestedConfigurationProperty
    val audioMixPresets: Map<String, AudioMixPreset> = mapOf("default" to AudioMixPreset()),
    @NestedConfigurationProperty
    val defaultChannelLayouts: Map<Int, ChannelLayout> = emptyMap(),
    val flipWidthHeightIfPortrait: Boolean = true,
    val exitOnError: Boolean = true,
    val globalParams: LinkedHashMap<String, Any?> = linkedMapOf(),
    @NestedConfigurationProperty
    val segmentedEncoding: SegmentedEncodingProperties = SegmentedEncodingProperties(),
    @NestedConfigurationProperty
    val speechToText: SpeechToTextProperties = SpeechToTextProperties(),
)
