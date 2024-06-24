// SPDX-FileCopyrightText: 2024 Eyevinn Technology AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.encore.service.remotefiles

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import se.svt.oss.encore.service.remotefiles.s3.S3Properties
import se.svt.oss.encore.service.remotefiles.s3.S3UriConverter
import software.amazon.awssdk.regions.Region
import java.net.URI

class S3UriConverterTest {

    private val s3Properties = S3Properties(enabled = true, anonymousAccess = true)
    private val region = Region.of("eu-west-1")
    private val s3Uri = URI.create("s3://my-bucket/test2/test1_x264_3100.mp4")
    private val s3UriConverter = S3UriConverter(s3Properties, region)

    @Test
    fun toHttpReturnsCorrectUri() {
        val httpUri = s3UriConverter.toHttp(s3Uri)
        assertThat(httpUri)
            .isEqualTo("https://my-bucket.s3.eu-west-1.amazonaws.com/test2/test1_x264_3100.mp4")
    }

    @Test
    fun differentRegionReturnsCorrectUri() {
        val s3UriConverter = S3UriConverter(s3Properties, Region.of("eu-north-1"))

        val httpUri = s3UriConverter.toHttp(s3Uri)
        assertThat(httpUri)
            .isEqualTo("https://my-bucket.s3.eu-north-1.amazonaws.com/test2/test1_x264_3100.mp4")
    }

    @Test
    fun toHttpWithCustomEndpointReturnsCorrectUri() {
        val endpoint = "some-host:1234"
        val s3UriConverter = S3UriConverter(s3Properties.copy(endpoint = endpoint), region)

        val httpUri = s3UriConverter.toHttp(s3Uri)
        assertThat(httpUri)
            .isEqualTo("https://my-bucket.some-host:1234/test2/test1_x264_3100.mp4")
    }

    @Test
    fun getBucketAndKeyReturnsCorrectValues() {
        val (bucket, key) = s3UriConverter.getBucketAndKey(s3Uri)
        assertThat(bucket).isEqualTo("my-bucket")
        assertThat(key).isEqualTo("test2/test1_x264_3100.mp4")
    }

    @Test
    fun toHttpNonS3UriThrowsException() {
        val uri = URI.create("https://my-bucket/test2/test1_x264_3100.mp4")
        assertThatThrownBy { s3UriConverter.toHttp(uri) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid URI: $uri")
    }

    @Test
    fun getBucketAndKeyNonS3UriThrowsException() {
        val uri = URI.create("https://my-bucket/test2/test1_x264_3100.mp4")
        assertThatThrownBy { s3UriConverter.getBucketAndKey(uri) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid URI: $uri")
    }
}
