package se.svt.oss.encore.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FfmpegExecutorTest {
    @Nested
    inner class TestGetProgress {
        @Test
        fun `valid time and duration, returns progress`() {
            val logLine = "frame=  240 fps= 24 q=28.0 size=    1024kB time=00:00:10.00 bitrate= 838.9kbits/s speed=1.00x"
            val duration = 20.0 // seconds
            val progress = getProgress(duration, logLine)
            assertNotNull(progress)
            assertEquals(50, progress)
        }

        @Test
        fun `logline with elapsed on end`() {
            val logLine = "[info] frame= 2896 fps= 75 q=27.0 size=   35328KiB time=00:01:55.76 bitrate=2500.1kbits/s dup=118 drop=0 speed=3.01x elapsed=0:00:38.51"
            val duration = 2894.0
            val progress = getProgress(duration, logLine)
            assertNotNull(progress)
            assertEquals(4, progress)
        }

        @Test
        fun `invalid logline, returns null`() {
            val logLine = "RANDOM LOG LINE"
            val duration = 20.0 // seconds
            val progress = getProgress(duration, logLine)
            assertNull(progress)
        }

        @Test
        fun `null duration, returns null`() {
            val logLine =
                "frame=  240 fps= 24 q=28.0 size=    1024kB time=00:00:10.00 bitrate= 838.9kbits/s speed=1.00x"
            val duration: Double? = null
            val progress = getProgress(duration, logLine)
            assertNull(progress)
        }
    }
}
