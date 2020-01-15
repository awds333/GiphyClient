package io.demo.fedchenko.giphyclient.okhttp

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.io.File
import java.io.IOException

class OkHttpFileDownloader(val context: Context) : FileDownloader {
    private val client = OkHttpClient.Builder().build()

    override suspend fun getFile(url: String): File {
        val request = Request.Builder().url(url).build()
        val result = client.newCall(request).await()
        if (result.isSuccessful) {
            val path = File(context.filesDir.path, "images")
            if (!path.isDirectory)
                path.mkdirs()

            val file = File(path, "test.gif")
            file.createNewFile()
            file.writeBytes(result.body()?.bytes() ?: throw IOException())
            return file
        } else
            throw IOException()
    }
}