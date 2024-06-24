package se.svt.oss.encore.service.remotefiles.s3

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConditionalOnProperty("remote-files.s3.access-key")
@Component
@ConfigurationProperties("remote-files.s3")
class S3Properties {
    lateinit var endpoint: String
    lateinit var accessKey: String
    lateinit var secretKey: String
}