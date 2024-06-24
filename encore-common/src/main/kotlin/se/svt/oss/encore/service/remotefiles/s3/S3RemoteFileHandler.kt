package se.svt.oss.encore.service.remotefiles.s3

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import se.svt.oss.encore.service.remotefiles.RemoteFileHandler
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

@ConditionalOnBean(S3Properties::class)
@Service
class S3RemoteFileHandler(
    private val client: S3AsyncClient,
    private val presigner: S3Presigner,
    private val s3Properties: S3Properties
) : RemoteFileHandler {

    private val log = mu.KotlinLogging.logger {}

    override fun getAccessUri(uri: String): String {
        val s3Uri = URI.create(uri)

        val objectRequest: GetObjectRequest = GetObjectRequest.builder()
            .bucket(s3Uri.host)
            .key(s3Uri.path.substring(1)) // Remove leading slash
            .build()
        val presignRequest: GetObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(java.time.Duration.ofSeconds(s3Properties.presignDurationSeconds.toLong()))
            .getObjectRequest(objectRequest)
            .build()

        val presignedRequest = presigner.presignGetObject(presignRequest)
        val url = presignedRequest.url().toExternalForm()
        return url
    }

    override fun upload(localFile: String, remoteFile: String) {
        log.info { "Uploading $localFile to $remoteFile" }
        val s3Uri = URI.create(remoteFile)
        val bucket = s3Uri.host
        val objectName = s3Uri.path.stripLeadingSlash()
        val putObjectRequest: PutObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(objectName)
            .build()
        val res = client.putObject(putObjectRequest, Paths.get(localFile)).get(s3Properties.presignDurationSeconds.toLong(), TimeUnit.SECONDS)
        log.info { "Upload result: $res" }
    }

    private fun String.stripLeadingSlash() = if (startsWith("/")) substring(1) else this

    override val protocols = listOf("s3")
}
