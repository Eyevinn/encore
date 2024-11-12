// SPDX-FileCopyrightText: 2024 Eyevinn Technology AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.encore.service.remotefiles.s3

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("remote-files.s3")
data class S3Properties(
    val enabled: Boolean = false,
    val endpoint: String = "",
    val presignDurationSeconds: Long = Duration.ofHours(12).seconds,
    val uploadTimeoutSeconds: Long = Duration.ofHours(1).seconds
)
