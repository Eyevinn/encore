package se.svt.oss.encore.service.remotefiles

interface RemoteFileHandler {
    fun getAccessUri(uri: String): String
    fun upload(localFile: String, remoteFile: String)
    val protocols: List<String>
}
