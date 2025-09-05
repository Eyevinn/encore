package se.svt.oss.encore.model.profile

import se.svt.oss.encore.model.input.DEFAULT_AUDIO_LABEL
import se.svt.oss.encore.model.output.AudioStreamEncode
import se.svt.oss.encore.model.output.Output
import se.svt.oss.encore.model.output.PostProcessor
import java.io.File

data class SpeechToText(
    val inputLabel: String = DEFAULT_AUDIO_LABEL,
    val model: String = "default",
    val language: String = "auto",
    val queue: Int = 3,
    val suffix: String = "_stt",
) : OutputProducer {
    override fun getOutput(
        context: OutputProducerContext,
    ): Output {
        val (job, encodingProperties, _, outputFolder) = context
        val modelPath = encodingProperties.speechToText.models[model]
            ?: throw RuntimeException("Model '$model' not found in configuration")
        val output = "$outputFolder/${job.baseName}$suffix.srt"

        return Output(
            video = null,
            id = "$suffix.srt",
            output = output,
            format = "null",
            audioStreams = listOf(
                AudioStreamEncode(
                    params = listOf(),
                    inputLabels = listOf(inputLabel),
                    filter = "whisper=model=$modelPath:queue=$queue:language=$language:format=srt:destination=$output",
                    preserveLayout = false,
                ),
            ),
            postProcessor = PostProcessor { _ -> listOf(File(output)) },
            sidechainOutput = true,
        )
    }
}
