// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.encore.model.profile

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import se.svt.oss.encore.Assertions.assertThat
import se.svt.oss.encore.config.EncodingProperties
import se.svt.oss.encore.defaultEncoreJob
import se.svt.oss.encore.defaultVideoFile
import se.svt.oss.encore.model.input.AudioVideoInput
import se.svt.oss.encore.model.output.AudioStreamEncode
import se.svt.oss.encore.portraitVideoFile
import se.svt.oss.encore.rotateToPortraitVideoFile

abstract class VideoEncodeTest<T : VideoEncode> {

    abstract fun createEncode(
        width: Int?,
        height: Int?,
        twoPass: Boolean,
        params: LinkedHashMap<String, String>,
        filters: List<String>,
        audioEncode: AudioEncode?,
    ): T

    private val encodingProperties = EncodingProperties()
    private val filterSettings = FilterSettings()
    private val audioEncode = mockk<AudioEncode>()
    private val audioStreamEncode = mockk<AudioStreamEncode>()
    private val defaultParams = linkedMapOf("a" to "b")

    @BeforeEach
    internal fun setUp() {
        every { audioEncode.getOutput(any(), encodingProperties, filterSettings)?.audioStreams } returns listOf(audioStreamEncode)
    }

    @Test
    fun `scale portrait input within portrait box`() {
        val encode = createEncode(
            width = 1920,
            height = 1080,
            twoPass = false,
            params = defaultParams,
            filters = emptyList(),
            audioEncode = audioEncode,
        )
        listOf(portraitVideoFile, rotateToPortraitVideoFile).forEach { analyzedFile ->
            val output = encode.getOutput(
                defaultEncoreJob().copy(
                    inputs = listOf(
                        AudioVideoInput(
                            uri = "/test.mp4",
                            analyzed = analyzedFile,
                        ),
                    ),
                ),
                encodingProperties,
                filterSettings,
            )

            assertThat(output?.video).hasFilter("scale=1080:1920:force_original_aspect_ratio=decrease:force_divisible_by=2,setsar=1/1")
        }
    }

    @Test
    fun `scale portrait output within portrait box`() {
        val encode = createEncode(
            width = 1920,
            height = 1080,
            twoPass = false,
            params = defaultParams,
            filters = emptyList(),
            audioEncode = audioEncode,
        )
        val output = encode.getOutput(
            defaultEncoreJob().copy(
                inputs = listOf(
                    AudioVideoInput(
                        uri = "/test.mp4",
                        analyzed = defaultVideoFile,
                        cropTo = "9:16",
                    ),
                ),
            ),
            encodingProperties,
            filterSettings,
        )

        assertThat(output?.video).hasFilter("scale=1080:1920:force_original_aspect_ratio=decrease:force_divisible_by=2,setsar=1/1")
    }

    @Test
    fun `single pass scale to height with audio`() {
        val encode = createEncode(
            width = null,
            height = 1080,
            twoPass = false,
            params = defaultParams,
            filters = listOf("afilter"),
            audioEncode = audioEncode,
        )
        val output = encode.getOutput(defaultEncoreJob(), encodingProperties, filterSettings)
        assertThat(output)
            .hasOnlyAudioStreams(audioStreamEncode)
        val videoStreamEncode = output!!.video
        assertThat(videoStreamEncode)
            .isNotNull
            .hasNoFirstPassParams()
            .hasTwoPass(false)
            .hasFilter("scale=-2:1080,afilter")
        verifyFirstPassParams(encode, videoStreamEncode!!.firstPassParams)
        verifySecondPassParams(encode, videoStreamEncode.params)
    }

    @Test
    fun `single pass scale to height with custom scale filter`() {
        val filterSettings = FilterSettings(scaleFilter = "myscale")
        every { audioEncode.getOutput(any(), encodingProperties, filterSettings)?.audioStreams } returns listOf(audioStreamEncode)
        val encode = createEncode(
            width = null,
            height = 1080,
            twoPass = false,
            params = defaultParams,
            filters = listOf("afilter"),
            audioEncode = audioEncode,
        )
        val output = encode.getOutput(defaultEncoreJob(), encodingProperties, filterSettings)
        assertThat(output)
            .hasOnlyAudioStreams(audioStreamEncode)
        val videoStreamEncode = output!!.video
        assertThat(videoStreamEncode)
            .isNotNull
            .hasNoFirstPassParams()
            .hasTwoPass(false)
            .hasFilter("myscale=-2:1080,afilter")
        verifyFirstPassParams(encode, videoStreamEncode!!.firstPassParams)
        verifySecondPassParams(encode, videoStreamEncode.params)
    }

    @Test
    fun `two-pass encode`() {
        val encode = createEncode(
            width = null,
            height = 1080,
            twoPass = true,
            params = defaultParams,
            filters = listOf("afilter"),
            audioEncode = audioEncode,
        )
        val output = encode.getOutput(defaultEncoreJob(), encodingProperties, filterSettings)
        assertThat(output).isNotNull
        val videoStreamEncode = output!!.video
        assertThat(videoStreamEncode)
            .hasTwoPass(true)
        verifyFirstPassParams(encode, videoStreamEncode!!.firstPassParams)
        verifySecondPassParams(encode, videoStreamEncode.params)
    }

    open fun verifyFirstPassParams(encode: VideoEncode, params: List<String>) {
        if (encode.twoPass) {
            assertThat(params)
                .containsSequence("-a", "b")
                .containsSequence("-c:v", encode.codec)
                .containsSequence("-pass", "1")
                .containsSequence("-passlogfile", "log${encode.suffix}")
        } else {
            assertThat(params).isEmpty()
        }
    }

    open fun verifySecondPassParams(encode: VideoEncode, params: List<String>) {
        if (encode.twoPass) {
            assertThat(params)
                .containsSequence("-pass", "2")
                .containsSequence("-passlogfile", "log${encode.suffix}")
        } else {
            assertThat(params)
                .containsSequence("-a", "b")
                .containsSequence("-c:v", encode.codec)
                .doesNotContain("-pass", "-passlogfile")
        }
    }
}
