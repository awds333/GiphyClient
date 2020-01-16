package io.demo.fedchenko.giphyclient.okhttp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.io.File
import java.io.IOException
import kotlin.coroutines.coroutineContext

class OkHttpFileDownloader(val context: Context) : FileDownloader {
    private val client = OkHttpClient.Builder().build()

    override suspend fun getFile(url: String): File {
        val request = Request.Builder().url(url).build()
        val result = client.newCall(request).await()
        if (result.isSuccessful) {
            val path = File(context.filesDir.path, "images")
            if (!path.isDirectory)
                path.mkdirs()

            val file = File(path, "temp.gif")
            withContext(Dispatchers.IO){
                file.createNewFile()
                file.writeBytes(result.body()?.bytes() ?: throw IOException("Response body is empty"))
            }
            return file
        } else
            throw IOException("Failed to download file")
    }
}