package se.svt.oss.encore.service.remotefiles

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.URI

@Service
class RemoteFileService(private val remoteFileHandlers: List<RemoteFileHandler>) {

    private val log = KotlinLogging.logger {}

    private val localFileHandler = LocalFileHandler()

    fun isRemoteFile(uriOrPath: String): Boolean {
        val uri = URI.create(uriOrPath)
        if (uri.scheme.isNullOrEmpty() || uri.scheme.lowercase() == "file") {
            return false;
        }
        return true;
    }

    fun getAccessUri(uriOrPath: String): String {
        val uri = URI.create(uriOrPath)
        return getHandler(uri).getAccessUri(uriOrPath)
    }

    fun upload(localFile: String, remoteFile: String) {
        val uri = URI.create(remoteFile)
        getHandler(uri).upload(localFile, remoteFile)
    }

    private fun getHandler(uri: URI): RemoteFileHandler {
        log.info { "Getting handler for uri $uri. Available protocols: ${remoteFileHandlers.flatMap {it.protocols} }" }
        if (uri.scheme.isNullOrEmpty() || uri.scheme.lowercase() == "file") {
            return localFileHandler;
        }
        return remoteFileHandlers.firstOrNull { it.protocols.contains(uri.scheme) }
            ?: throw IllegalArgumentException("No remote file handler found for protocol ${uri.scheme}")
    }

    private class LocalFileHandler : RemoteFileHandler {
        override fun getAccessUri(uri: String): String {
            return uri;
        }

        override fun upload(localFile: String, remoteFile: String) {
            // Do nothing
        }

        override val protocols: List<String> = listOf("file")
    }
}