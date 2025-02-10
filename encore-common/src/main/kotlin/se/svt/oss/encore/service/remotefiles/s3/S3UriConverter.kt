// SPDX-FileCopyrightText: 2024 Eyevinn Technology AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.encore.service.remotefiles.s3

import software.amazon.awssdk.regions.Region
import java.net.URI

class S3UriConverter(
    private val s3Properties: S3Properties,
    private val region: Region
) {

    fun toHttp(s3Uri: URI): String {
        if (s3Uri.scheme != "s3") {
            throw IllegalArgumentException("Invalid URI: $s3Uri")
        }
        val bucket = s3Uri.host
        val key = s3Uri.path.stripLeadingSlash()

        if (s3Properties.endpoint.isNotBlank()) {
            return "https://$bucket.${s3Properties.endpoint}/$key"
        }
        return "https://$bucket.s3.$region.amazonaws.com/$key"
    }

    fun getBucketAndKey(s3Uri: URI): Pair<String, String> {
        if (s3Uri.scheme != "s3") {
            throw IllegalArgumentException("Invalid URI: $s3Uri")
        }
        val bucket = s3Uri.host
        val key = s3Uri.path.stripLeadingSlash()
        return Pair(bucket, key)
    }

    private fun String.stripLeadingSlash() = if (startsWith("/")) substring(1) else this
}
