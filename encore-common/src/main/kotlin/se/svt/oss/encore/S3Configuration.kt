package se.svt.oss.encore

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import se.svt.oss.encore.service.remotefiles.s3.S3Properties
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@ConditionalOnBean(S3Properties::class)
@Configuration
class S3Configuration {

    @Bean
    fun s3Region() =
        Region.of(System.getProperty("aws.region") ?: System.getenv("AWS_REGION") ?: "us-east-1")

    @Bean
    fun s3Client(s3Region: Region, s3Properties: S3Properties) = S3AsyncClient.builder()
        .region(s3Region)
        .crossRegionAccessEnabled(true)
        .multipartEnabled(true)
        .apply {
            if (!s3Properties.endpoint.isNullOrBlank()) {
                endpointOverride(URI.create(s3Properties.endpoint))
            }
        }
        .build()

    @Bean
    fun s3Presigner(s3Region: Region, s3Properties: S3Properties) = S3Presigner.builder()
        .region(s3Region)
        .apply {
            if (!s3Properties.endpoint.isNullOrBlank()) {
                endpointOverride(URI.create(s3Properties.endpoint))
            }
        }
        .build()
}
