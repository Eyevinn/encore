package se.svt.oss.encore.service.remotefiles.s3

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import io.minio.http.Method
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import se.svt.oss.encore.service.remotefiles.RemoteFileHandler
import java.net.URI
import java.util.concurrent.TimeUnit

@ConditionalOnBean(S3Properties::class)
@Service
class S3RemoteFileHandler(private val s3Properties: S3Properties): RemoteFileHandler {

    private val client = MinioClient.builder()
        .endpoint(s3Properties.endpoint)
        .credentials(s3Properties.accessKey, s3Properties.secretKey)
        .build()

    override fun getAccessUri(uri: String): String {
        val s3Uri = URI.create(uri);
        val presignedUrl = client.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(s3Uri.host)
                .expiry(12, TimeUnit.HOURS)
                .`object`(s3Uri.path)
                .build());
        return presignedUrl
    }

    override fun upload(localFile: String, remoteFile: String) {
        val s3Uri = URI.create(remoteFile);
        val bucket = s3Uri.host
        val objectName = s3Uri.path
        client.uploadObject(
            UploadObjectArgs.builder()
                .bucket(bucket)
                .`object`(objectName)
                .filename(localFile)
                .build()
        )
    }

    override val protocols = listOf("s3")
}

