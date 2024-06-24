package se.svt.oss.encore.service.remotefiles.s3

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@ConditionalOnProperty("remote-files.s3.enabled", havingValue = "true")
@Component
@ConfigurationProperties("remote-files.s3")
class S3Properties {
    var enabled = false
    var endpoint: String = ""
    var presignDurationSeconds: Int = Duration.ofHours(12).seconds.toInt()
    var uploadTimeoutSeconds: Int = Duration.ofHours(1).seconds.toInt()
}
